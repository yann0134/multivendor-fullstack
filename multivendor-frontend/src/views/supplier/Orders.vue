<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-package-variant</v-icon>
            <span>📦 Produits Approuvés - Gestion des Stocks</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher par produit"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
          </v-card-title>
          <v-card-subtitle>
            Gérez vos produits approuvés et leurs quantités de stock.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="filteredOrders"
            :loading="loading"
            class="elevation-1"
            item-value="id"
          >
            <template v-slot:item.product="{ item }">
              <div class="d-flex align-center">
                <v-avatar size="40" class="mr-3">
                  <v-img 
                    v-if="item.images && item.images.length > 0" 
                    :src="item.images[0].imageUrl" 
                    alt="Product"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
                <div>
                  <div class="font-weight-medium">{{ item.title || 'N/A' }}</div>
                  <div class="text-caption text-grey-600">{{ item.description?.substring(0, 30) }}...</div>
                </div>
              </div>
            </template>

            <template v-slot:item.quantity="{ item }">
              <QuantityDisplay
                :product="item"
              />
            </template>

            <template v-slot:item.shipmentStatus="{ item }">
              <v-chip 
                :color="getShipmentStatusColor(item.shipmentStatus)" 
                :text-color="getShipmentStatusTextColor(item.shipmentStatus)"
                small
              >
                <v-icon left size="small">{{ getShipmentStatusIcon(item.shipmentStatus) }}</v-icon>
                {{ getShipmentStatusText(item.shipmentStatus) }}
              </v-chip>
            </template>

            <template v-slot:item.receptionStatus="{ item }">
              <v-chip 
                :color="getReceptionStatusColor(item.receptionStatus)" 
                :text-color="getReceptionStatusTextColor(item.receptionStatus)"
                small
              >
                <v-icon left size="small">{{ getReceptionStatusIcon(item.receptionStatus) }}</v-icon>
                {{ getReceptionStatusText(item.receptionStatus) }}
              </v-chip>
            </template>

            <template v-slot:item.supplierPrice="{ item }">
              <div class="font-weight-medium">{{ formatPrice(item.supplierPrice) }}</div>
            </template>

            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>

            <template v-slot:item.deliveryDate="{ item }">
              <div class="d-flex align-center">
                <v-icon color="green" size="small" class="mr-1">mdi-truck-delivery</v-icon>
                <span>{{ item.createdAt ? new Date(new Date(item.createdAt).getTime() + 3 * 24 * 60 * 60 * 1000).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' }) : 'N/A' }}</span>
              </div>
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <v-btn
                  v-if="item.shipmentStatus === 'NOT_SHIPPED'"
                  color="success"
                  size="small"
                  @click="updateShipmentStatus(item.id, 'PREPARING')"
                  :loading="processingOrders.includes(item.id)"
                >
                  <v-icon left>mdi-package-variant</v-icon>
                  Préparer
                </v-btn>
                
                <v-btn
                  v-if="item.shipmentStatus === 'PREPARING'"
                  color="primary"
                  size="small"
                  @click="updateShipmentStatus(item.id, 'SHIPPED')"
                  :loading="processingOrders.includes(item.id)"
                >
                  <v-icon left>mdi-truck</v-icon>
                  Expédier
                </v-btn>
                
                <v-btn
                  v-if="item.shipmentStatus === 'SHIPPED'"
                  color="info"
                  size="small"
                  @click="updateShipmentStatus(item.id, 'DELIVERED')"
                  :loading="processingOrders.includes(item.id)"
                >
                  <v-icon left>mdi-check-circle</v-icon>
                  Livré
                </v-btn>
              </div>
            </template>

            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">
                <div class="text-center">
                  <v-icon size="48" class="mb-2">mdi-package-variant-outline</v-icon>
                  <div>Aucun produit approuvé trouvé.</div>
                  <div class="text-caption mt-2">Vos produits approuvés apparaîtront ici.</div>
                </div>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'
import QuantityDisplay from '@/components/supplier/QuantityDisplay.vue'

const orders = ref([])
const loading = ref(false)
const searchQuery = ref('')
const processingOrders = ref([])

const headers = [
  { title: 'Produit', key: 'product', sortable: true },
  { title: 'Quantité', key: 'quantity', sortable: true },
  { title: 'Statut Envoi', key: 'shipmentStatus', sortable: true },
  { title: 'Statut Réception', key: 'receptionStatus', sortable: true },
  { title: 'Prix Fournisseur', key: 'supplierPrice', sortable: true },
  { title: 'Date Commande', key: 'createdAt', sortable: true },
  { title: 'Date Livraison', key: 'deliveryDate', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

const filteredOrders = computed(() => {
  if (!searchQuery.value) {
    return orders.value
  }
  const query = searchQuery.value.toLowerCase()
  return orders.value.filter(item => 
    item.title?.toLowerCase().includes(query) ||
    item.description?.toLowerCase().includes(query)
  )
})



// Fonctions pour les statuts d'envoi
const getShipmentStatusColor = (status) => {
  const colors = {
    'NOT_SHIPPED': 'grey',
    'PREPARING': 'orange',
    'SHIPPED': 'blue',
    'IN_TRANSIT': 'purple',
    'DELIVERED': 'green',
    'FAILED_DELIVERY': 'red'
  }
  return colors[status] || 'grey'
}

const getShipmentStatusTextColor = (status) => {
  return 'white'
}

const getShipmentStatusText = (status) => {
  const texts = {
    'NOT_SHIPPED': 'Non expédié',
    'PREPARING': 'En préparation',
    'SHIPPED': 'Expédié',
    'IN_TRANSIT': 'En transit',
    'DELIVERED': 'Livré',
    'FAILED_DELIVERY': 'Échec'
  }
  return texts[status] || status
}

const getShipmentStatusIcon = (status) => {
  const icons = {
    'NOT_SHIPPED': 'mdi-package-variant-closed',
    'PREPARING': 'mdi-package-variant',
    'SHIPPED': 'mdi-truck',
    'IN_TRANSIT': 'mdi-truck-delivery',
    'DELIVERED': 'mdi-check-circle',
    'FAILED_DELIVERY': 'mdi-alert-circle'
  }
  return icons[status] || 'mdi-package-variant'
}

// Fonctions pour les statuts de réception
const getReceptionStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'RECEIVED': 'green',
    'PARTIALLY_RECEIVED': 'blue',
    'REJECTED': 'red',
    'DAMAGED': 'purple',
    'QUANTITY_MISMATCH': 'yellow'
  }
  return colors[status] || 'grey'
}

const getReceptionStatusTextColor = (status) => {
  return 'white'
}

const getReceptionStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'RECEIVED': 'Reçu',
    'PARTIALLY_RECEIVED': 'Partiellement reçu',
    'REJECTED': 'Rejeté',
    'DAMAGED': 'Endommagé',
    'QUANTITY_MISMATCH': 'Écart quantité'
  }
  return texts[status] || status
}

const getReceptionStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock',
    'RECEIVED': 'mdi-check-circle',
    'PARTIALLY_RECEIVED': 'mdi-check-circle-outline',
    'REJECTED': 'mdi-close-circle',
    'DAMAGED': 'mdi-alert',
    'QUANTITY_MISMATCH': 'mdi-alert-circle'
  }
  return icons[status] || 'mdi-clock'
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF'
  }).format(price)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}


const fetchOrders = async () => {
  loading.value = true
  try {
    console.log('🔄 Récupération des produits approuvés...')
    const response = await api.get('/api/products/status/APPROVED/supplier')
    console.log('📊 Réponse API:', response)
    console.log('📦 Données reçues:', response.data)
    orders.value = response.data.content || response.data
    console.log('📦 Produits approuvés chargés:', orders.value)
    console.log('📦 Nombre de produits:', orders.value.length)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des produits approuvés:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const updateShipmentStatus = async (orderId, status) => {
  processingOrders.value.push(orderId)
  
  try {
    const response = await api.put(`/api/supply-orders/${orderId}/shipment-status`, { status })
    
    if (response.status === 200) {
      const updatedOrder = response.data
      const index = orders.value.findIndex(o => o.id === updatedOrder.id)
      if (index !== -1) {
        orders.value[index] = updatedOrder
      }
      
      console.log('✅ Statut d\'envoi mis à jour avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur lors de la mise à jour du statut d\'envoi:', error)
  } finally {
    const index = processingOrders.value.indexOf(orderId)
    if (index > -1) {
      processingOrders.value.splice(index, 1)
    }
  }
}


onMounted(() => {
  fetchOrders()
})
</script>