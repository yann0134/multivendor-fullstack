<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-truck-delivery</v-icon>
            <span>🔄 Tâches de Récupération</span>
          </v-card-title>
          <v-card-subtitle>
            Récupération des produits validés chez les fournisseurs
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Filtres -->
    <v-row class="mt-4">
      <v-col cols="12" md="4">
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          label="Filtrer par statut"
          clearable
        ></v-select>
      </v-col>
      <v-col cols="12" md="4">
        <v-text-field
          v-model="searchQuery"
          label="Rechercher par fournisseur"
          prepend-inner-icon="mdi-magnify"
          clearable
        ></v-text-field>
      </v-col>
      <v-col cols="12" md="4">
        <v-btn color="primary" @click="loadTasks">
          <v-icon left>mdi-refresh</v-icon>
          Actualiser
        </v-btn>
      </v-col>
    </v-row>

    <!-- Liste des tâches de récupération -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>📦 Produits à Récupérer</v-card-title>
          <v-data-table
            :headers="headers"
            :items="filteredTasks"
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

            <!-- Actions -->
            <template v-slot:item.actions="{ item }">
              <v-btn
                v-if="item.status === 'PENDING'"
                color="primary"
                small
                @click="startCollection(item)"
              >
                <v-icon left small>mdi-play</v-icon>
                Commencer
              </v-btn>
              <v-btn
                v-if="item.status === 'IN_PROGRESS'"
                color="success"
                small
                @click="completeCollection(item)"
              >
                <v-icon left small>mdi-check</v-icon>
                Terminer
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

            <!-- Détails du fournisseur -->
            <template v-slot:item.supplierInfo="{ item }">
              <div>
                <div class="font-weight-medium">{{ item.supplierName }}</div>
                <div class="text-caption text--secondary">{{ item.supplierAddress }}</div>
                <div class="text-caption">
                  <v-icon small>mdi-phone</v-icon>
                  {{ item.supplierPhone }}
                </div>
              </div>
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
          Détails de la Récupération
        </v-card-title>
        <v-card-text v-if="selectedTask">
          <v-row>
            <v-col cols="12" md="6">
              <h4>📦 Produit</h4>
              <p><strong>Nom:</strong> {{ selectedTask.productName }}</p>
              <p><strong>Quantité:</strong> {{ selectedTask.quantity }} {{ selectedTask.unit }}</p>
              <p><strong>Prix unitaire:</strong> {{ selectedTask.unitPrice }} FCFA</p>
            </v-col>
            <v-col cols="12" md="6">
              <h4>🌾 Fournisseur</h4>
              <p><strong>Nom:</strong> {{ selectedTask.supplierName }}</p>
              <p><strong>Adresse:</strong> {{ selectedTask.supplierAddress }}</p>
              <p><strong>Téléphone:</strong> {{ selectedTask.supplierPhone }}</p>
            </v-col>
          </v-row>
          <v-row v-if="selectedTask.notes">
            <v-col cols="12">
              <h4>📝 Notes</h4>
              <p>{{ selectedTask.notes }}</p>
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
  name: 'AgentTasks',
  setup() {
    const loading = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const detailsDialog = ref(false)
    const selectedTask = ref(null)

    const tasks = ref([
      {
        id: 1,
        productName: 'Tomates Bio',
        productImage: '/images/tomates.jpg',
        quantity: 50,
        unit: 'kg',
        unitPrice: 800,
        supplierName: 'Ferme Bio Yannick',
        supplierAddress: 'Douala, Cameroun',
        supplierPhone: '+237 123 456 789',
        status: 'PENDING',
        createdAt: '2024-10-19',
        notes: 'Produits frais récoltés ce matin'
      },
      {
        id: 2,
        productName: 'Carottes',
        productImage: '/images/carottes.jpg',
        quantity: 30,
        unit: 'kg',
        unitPrice: 600,
        supplierName: 'Ferme Vert',
        supplierAddress: 'Yaoundé, Cameroun',
        supplierPhone: '+237 987 654 321',
        status: 'IN_PROGRESS',
        createdAt: '2024-10-19',
        notes: 'À récupérer avant 16h'
      },
      {
        id: 3,
        productName: 'Bananes Plantain',
        productImage: '/images/bananes.jpg',
        quantity: 25,
        unit: 'régime',
        unitPrice: 2000,
        supplierName: 'Plantation Manga',
        supplierAddress: 'Bafoussam, Cameroun',
        supplierPhone: '+237 555 123 456',
        status: 'COMPLETED',
        createdAt: '2024-10-18',
        notes: 'Récupération terminée avec succès'
      }
    ])

    const headers = [
      { text: 'Image', value: 'productImage', sortable: false, width: '80px' },
      { text: 'Produit', value: 'productName' },
      { text: 'Quantité', value: 'quantity' },
      { text: 'Fournisseur', value: 'supplierInfo' },
      { text: 'Statut', value: 'status' },
      { text: 'Date', value: 'createdAt' },
      { text: 'Actions', value: 'actions', sortable: false }
    ]

    const statusOptions = [
      { text: 'En attente', value: 'PENDING' },
      { text: 'En cours', value: 'IN_PROGRESS' },
      { text: 'Terminé', value: 'COMPLETED' }
    ]

    const filteredTasks = computed(() => {
      let filtered = tasks.value

      if (statusFilter.value) {
        filtered = filtered.filter(task => task.status === statusFilter.value)
      }

      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(task => 
          task.supplierName.toLowerCase().includes(query) ||
          task.productName.toLowerCase().includes(query)
        )
      }

      return filtered
    })

    const getStatusColor = (status) => {
      const colors = {
        'PENDING': 'orange',
        'IN_PROGRESS': 'blue',
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
        'COMPLETED': 'Terminé'
      }
      return texts[status] || status
    }

    const startCollection = (task) => {
      task.status = 'IN_PROGRESS'
      // Ici on ferait un appel API pour mettre à jour le statut
      console.log('Début de récupération:', task)
    }

    const completeCollection = (task) => {
      task.status = 'COMPLETED'
      // Ici on ferait un appel API pour marquer comme terminé
      console.log('Récupération terminée:', task)
    }

    const viewDetails = (task) => {
      selectedTask.value = task
      detailsDialog.value = true
    }

    const loadTasks = () => {
      loading.value = true
      // Simuler un appel API
      setTimeout(() => {
        loading.value = false
      }, 1000)
    }

    onMounted(() => {
      loadTasks()
    })

    return {
      loading,
      searchQuery,
      statusFilter,
      detailsDialog,
      selectedTask,
      tasks,
      headers,
      statusOptions,
      filteredTasks,
      getStatusColor,
      getStatusTextColor,
      getStatusText,
      startCollection,
      completeCollection,
      viewDetails,
      loadTasks
    }
  }
}
</script>

<style scoped>
.v-card {
  margin-bottom: 16px;
}
</style>
