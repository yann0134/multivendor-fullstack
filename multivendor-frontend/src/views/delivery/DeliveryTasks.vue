<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="success">mdi-truck-delivery</v-icon>
            <span>🚚 Livraisons aux Clients</span>
          </v-card-title>
          <v-card-subtitle>
            Livraison des commandes depuis l'entrepôt vers les clients
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mt-4">
      <v-col cols="12" md="3">
        <v-card color="blue" dark>
          <v-card-text class="text-center">
            <v-icon size="40">mdi-package-variant-closed</v-icon>
            <div class="text-h4">{{ stats.pending }}</div>
            <div>En attente</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="orange" dark>
          <v-card-text class="text-center">
            <v-icon size="40">mdi-truck-delivery</v-icon>
            <div class="text-h4">{{ stats.inProgress }}</div>
            <div>En cours</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="green" dark>
          <v-card-text class="text-center">
            <v-icon size="40">mdi-check-circle</v-icon>
            <div class="text-h4">{{ stats.completed }}</div>
            <div>Livrées</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="purple" dark>
          <v-card-text class="text-center">
            <v-icon size="40">mdi-currency-usd</v-icon>
            <div class="text-h4">{{ stats.earnings }}  FCFA</div>
            <div>Gains du jour</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Filtres -->
    <v-row class="mt-4">
      <v-col cols="12" md="3">
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          label="Filtrer par statut"
          clearable
        ></v-select>
      </v-col>
      <v-col cols="12" md="3">
        <v-text-field
          v-model="searchQuery"
          label="Rechercher par client"
          prepend-inner-icon="mdi-magnify"
          clearable
        ></v-text-field>
      </v-col>
      <v-col cols="12" md="3">
        <v-menu
          v-model="dateMenu"
          :close-on-content-click="false"
          transition="scale-transition"
          offset-y
          min-width="auto"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
              v-model="dateFilter"
              label="Filtrer par date"
              prepend-inner-icon="mdi-calendar"
              readonly
              v-bind="attrs"
              v-on="on"
            ></v-text-field>
          </template>
          <v-date-picker
            v-model="dateFilter"
            @change="dateMenu = false"
          ></v-date-picker>
        </v-menu>
      </v-col>
      <v-col cols="12" md="3">
        <v-btn color="primary" @click="loadDeliveries">
          <v-icon left>mdi-refresh</v-icon>
          Actualiser
        </v-btn>
      </v-col>
    </v-row>

    <!-- Liste des livraisons -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>📦 Commandes à Livrer</v-card-title>
          <v-data-table
            :headers="headers"
            :items="filteredDeliveries"
            :loading="loading"
            item-key="id"
          >
            <!-- Image du produit -->
            <template v-slot:item.productImage="{ item }">
              <v-avatar size="60" rounded>
                <v-img :src="item.productImage" :alt="item.productName"></v-img>
              </v-avatar>
            </template>

            <!-- Statut avec badge coloré -->
            <template v-slot:item.status="{ item }">
              <v-chip
                :color="getStatusColor(item.status)"
                :text-color="getStatusTextColor(item.status)"
                small
              >
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>

            <!-- Adresse de livraison -->
            <template v-slot:item.deliveryAddress="{ item }">
              <div>
                <div class="font-weight-medium">{{ item.customerName }}</div>
                <div class="text-caption">{{ item.deliveryAddress }}</div>
                <div class="text-caption">
                  <v-icon small>mdi-phone</v-icon>
                  {{ item.customerPhone }}
                </div>
              </div>
            </template>

            <!-- Actions -->
            <template v-slot:item.actions="{ item }">
              <v-btn
                v-if="item.status === 'PENDING'"
                color="primary"
                small
                @click="startDelivery(item)"
              >
                <v-icon left small>mdi-play</v-icon>
                Commencer
              </v-btn>
              <v-btn
                v-if="item.status === 'IN_PROGRESS'"
                color="success"
                small
                @click="completeDelivery(item)"
              >
                <v-icon left small>mdi-check</v-icon>
                Livrer
              </v-btn>
              <v-btn
                v-if="item.status === 'COMPLETED'"
                color="info"
                small
                @click="viewDetails(item)"
              >
                <v-icon left small>mdi-eye</v-icon>
                Détails
              </v-btn>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de détails -->
    <v-dialog v-model="detailsDialog" max-width="800">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">mdi-package-variant</v-icon>
          Détails de la Livraison
        </v-card-title>
        <v-card-text v-if="selectedDelivery">
          <v-row>
            <v-col cols="12" md="6">
              <h4>📦 Commande</h4>
              <p><strong>ID:</strong> #{{ selectedDelivery.orderId }}</p>
              <p><strong>Produit:</strong> {{ selectedDelivery.productName }}</p>
              <p><strong>Quantité:</strong> {{ selectedDelivery.quantity }} {{ selectedDelivery.unit }}</p>
              <p><strong>Prix total:</strong> {{ selectedDelivery.totalPrice }}  FCFA</p>
            </v-col>
            <v-col cols="12" md="6">
              <h4>👤 Client</h4>
              <p><strong>Nom:</strong> {{ selectedDelivery.customerName }}</p>
              <p><strong>Adresse:</strong> {{ selectedDelivery.deliveryAddress }}</p>
              <p><strong>Téléphone:</strong> {{ selectedDelivery.customerPhone }}</p>
            </v-col>
          </v-row>
          <v-row v-if="selectedDelivery.notes">
            <v-col cols="12">
              <h4>📝 Notes de livraison</h4>
              <p>{{ selectedDelivery.notes }}</p>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="detailsDialog = false">Fermer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'DeliveryTasks',
  setup() {
    const loading = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const dateFilter = ref('')
    const dateMenu = ref(false)
    const detailsDialog = ref(false)
    const selectedDelivery = ref(null)

    const deliveries = ref([
      {
        id: 1,
        orderId: 'CMD-001',
        productName: 'Tomates Bio',
        productImage: '/images/tomates.jpg',
        quantity: 5,
        unit: 'kg',
        totalPrice: 4000,
        customerName: 'Marie Nguema',
        deliveryAddress: 'Douala, Bonanjo',
        customerPhone: '+237 123 456 789',
        status: 'PENDING',
        createdAt: '2024-10-19',
        notes: 'Livrer entre 14h et 16h'
      },
      {
        id: 2,
        orderId: 'CMD-002',
        productName: 'Carottes',
        productImage: '/images/carottes.jpg',
        quantity: 3,
        unit: 'kg',
        totalPrice: 1800,
        customerName: 'Jean Mballa',
        deliveryAddress: 'Yaoundé, Mfoundi',
        customerPhone: '+237 987 654 321',
        status: 'IN_PROGRESS',
        createdAt: '2024-10-19',
        notes: 'Code porte: 1234'
      },
      {
        id: 3,
        orderId: 'CMD-003',
        productName: 'Bananes Plantain',
        productImage: '/images/bananes.jpg',
        quantity: 2,
        unit: 'régime',
        totalPrice: 4000,
        customerName: 'Fatou Diallo',
        deliveryAddress: 'Bafoussam, Centre',
        customerPhone: '+237 555 123 456',
        status: 'COMPLETED',
        createdAt: '2024-10-18',
        notes: 'Livraison réussie'
      }
    ])

    const stats = ref({
      pending: 1,
      inProgress: 1,
      completed: 1,
      earnings: 15000
    })

    const headers = [
      { text: 'Image', value: 'productImage', sortable: false, width: '80px' },
      { text: 'Commande', value: 'orderId' },
      { text: 'Produit', value: 'productName' },
      { text: 'Quantité', value: 'quantity' },
      { text: 'Client', value: 'deliveryAddress' },
      { text: 'Statut', value: 'status' },
      { text: 'Date', value: 'createdAt' },
      { text: 'Actions', value: 'actions', sortable: false }
    ]

    const statusOptions = [
      { text: 'En attente', value: 'PENDING' },
      { text: 'En cours', value: 'IN_PROGRESS' },
      { text: 'Livrée', value: 'COMPLETED' }
    ]

    const filteredDeliveries = computed(() => {
      let filtered = deliveries.value

      if (statusFilter.value) {
        filtered = filtered.filter(delivery => delivery.status === statusFilter.value)
      }

      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(delivery => 
          delivery.customerName.toLowerCase().includes(query) ||
          delivery.productName.toLowerCase().includes(query)
        )
      }

      if (dateFilter.value) {
        filtered = filtered.filter(delivery => 
          delivery.createdAt === dateFilter.value
        )
      }

      return filtered
    })

    const getStatusColor = (status) => {
      const colors = {
        'PENDING': 'blue',
        'IN_PROGRESS': 'orange',
        'COMPLETED': 'green'
      }
      return colors[status] || 'grey'
    }

    const getStatusTextColor = (status) => {
      return 'white'
    }

    const getStatusText = (status) => {
      const texts = {
        'PENDING': 'En attente',
        'IN_PROGRESS': 'En cours',
        'COMPLETED': 'Livrée'
      }
      return texts[status] || status
    }

    const startDelivery = (delivery) => {
      delivery.status = 'IN_PROGRESS'
      console.log('Début de livraison:', delivery)
    }

    const completeDelivery = (delivery) => {
      delivery.status = 'COMPLETED'
      console.log('Livraison terminée:', delivery)
    }

    const viewDetails = (delivery) => {
      selectedDelivery.value = delivery
      detailsDialog.value = true
    }

    const loadDeliveries = () => {
      loading.value = true
      setTimeout(() => {
        loading.value = false
      }, 1000)
    }

    onMounted(() => {
      loadDeliveries()
    })

    return {
      loading,
      searchQuery,
      statusFilter,
      dateFilter,
      dateMenu,
      detailsDialog,
      selectedDelivery,
      deliveries,
      stats,
      headers,
      statusOptions,
      filteredDeliveries,
      getStatusColor,
      getStatusTextColor,
      getStatusText,
      startDelivery,
      completeDelivery,
      viewDetails,
      loadDeliveries
    }
  }
}
</script>

<style scoped>
.v-card {
  margin-bottom: 16px;
}
</style>
