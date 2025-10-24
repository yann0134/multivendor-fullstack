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
                  @click="confirmShipment(item)"
                  :loading="processingOrders.includes(item.id)"
                >
                  <v-icon left>mdi-truck-delivery</v-icon>
                  Expédier
                </v-btn>
                
                <v-btn
                  v-if="item.shipmentStatus === 'SHIPPED'"
                  color="info"
                  size="small"
                  disabled
                >
                  <v-icon left>mdi-check-circle</v-icon>
                  Expédié
                </v-btn>
                
                <v-btn
                  v-if="item.shipmentStatus === 'DELIVERED'"
                  color="success"
                  size="small"
                  disabled
                >
                  <v-icon left>mdi-truck-check</v-icon>
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

    <!-- Modal de confirmation d'expédition -->
    <v-dialog v-model="showShipmentModal" max-width="500px" persistent>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-truck-delivery</v-icon>
          <span>Confirmer l'Expédition</span>
        </v-card-title>
        
        <v-card-text>
          <div class="mb-4">
            <v-alert type="info" variant="tonal">
              <v-icon left>mdi-information</v-icon>
              Vous êtes sur le point d'expédier le produit suivant :
            </v-alert>
          </div>
          
          <div v-if="selectedProduct" class="product-info">
            <v-card variant="outlined" class="pa-3">
              <div class="d-flex align-center">
                <v-avatar size="50" class="mr-3">
                  <v-img 
                    v-if="selectedProduct.images && selectedProduct.images.length > 0" 
                    :src="selectedProduct.images[0].imageUrl" 
                    alt="Product"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
                <div>
                  <div class="text-h6 font-weight-bold">{{ selectedProduct.title }}</div>
                  <div class="text-caption text-grey-600">{{ selectedProduct.description?.substring(0, 50) }}...</div>
                  <div class="text-body-2 mt-1">
                    <strong>Prix :</strong> {{ formatPrice(selectedProduct.supplierPrice) }}
                  </div>
                </div>
              </div>
            </v-card>
          </div>
          
          <v-alert type="warning" variant="tonal" class="mt-4">
            <v-icon left>mdi-alert-circle</v-icon>
            <strong>Attention :</strong> Cette action marquera le produit comme expédié et ne pourra pas être annulée.
          </v-alert>
        </v-card-text>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="cancelShipment"
            :disabled="processingOrders.includes(selectedProduct?.id)"
          >
            Annuler
          </v-btn>
          <v-btn
            color="success"
            variant="flat"
            @click="confirmShipmentAction"
            :loading="processingOrders.includes(selectedProduct?.id)"
            :disabled="!selectedProduct"
          >
            <v-icon left>mdi-truck-delivery</v-icon>
            Confirmer l'Expédition
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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

// Variables pour le modal de confirmation
const showShipmentModal = ref(false)
const selectedProduct = ref(null)

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
    const response = await api.get('/api/supply-orders/supplier/expedier')
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

const confirmShipment = (item) => {
  selectedProduct.value = item
  showShipmentModal.value = true
}

const cancelShipment = () => {
  showShipmentModal.value = false
  selectedProduct.value = null
}

const confirmShipmentAction = () => {
  if (selectedProduct.value) {
    updateShipmentStatus(selectedProduct.value.id, 'SHIPPED')
    showShipmentModal.value = false
    selectedProduct.value = null
  }
}

const updateShipmentStatus = async (productId, status) => {
  processingOrders.value.push(productId)
  
  try {
    console.log(`🚚 Mise à jour du statut d'expédition pour le produit ${productId} vers ${status}`)
    
    const response = await api.put(`/api/products/${productId}/shipment-status`, { 
      status: status 
    })
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = orders.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        orders.value[index] = updatedProduct
      }
      
      console.log('✅ Statut d\'expédition mis à jour avec succès:', updatedProduct)
      
      // Fermer le modal après succès
      showShipmentModal.value = false
      selectedProduct.value = null
      
      // Afficher une notification de succès
      // TODO: Intégrer un système de notifications
      alert(`✅ Produit expédié avec succès !`)
    }
  } catch (error) {
    console.error('❌ Erreur lors de la mise à jour du statut d\'expédition:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
    
    // Afficher une notification d'erreur
    alert(`❌ Erreur lors de l'expédition: ${error.response?.data?.message || error.message}`)
  } finally {
    const index = processingOrders.value.indexOf(productId)
    if (index > -1) {
      processingOrders.value.splice(index, 1)
    }
  }
}


onMounted(() => {
  fetchOrders()
})
</script>