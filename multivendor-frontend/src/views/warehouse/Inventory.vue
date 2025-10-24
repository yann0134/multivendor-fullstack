<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">📦 Gestion de l'Inventaire</h1>
        <p class="text-body-1 text-grey-600 mb-6">
          Produits validés et reçus par l'entrepôt
        </p>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <v-card color="green" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Produits Reçus</h3>
          <p class="text-h4">{{ stats.totalReceived }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="blue" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Stock Total</h3>
          <p class="text-h4">{{ stats.totalStock }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="orange" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-currency-usd</v-icon>
          <h3 class="text-h6">Valeur Totale</h3>
          <p class="text-h4">{{ formatPrice(stats.totalValue) }}</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Tableau de l'inventaire -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-warehouse</v-icon>
            <span>Inventaire de l'Entrepôt</span>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="refreshInventory">
              <v-icon left>mdi-refresh</v-icon>
              Actualiser
            </v-btn>
          </v-card-title>
          
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="inventory"
              :loading="loading"
              class="elevation-1"
            >
              <!-- Image du produit -->
              <template v-slot:item.image="{ item }">
                <v-avatar size="50" class="mr-3">
                  <v-img 
                    v-if="item.images && item.images.length > 0" 
                    :src="item.images[0].imageUrl" 
                    alt="Product"
                    @error="handleImageError"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
              </template>

              <!-- Informations du produit -->
              <template v-slot:item.productInfo="{ item }">
                <div>
                  <div class="text-h6 font-weight-bold">{{ item.title }}</div>
                  <div class="text-caption text-grey-600">{{ item.description?.substring(0, 50) }}...</div>
                  <div class="text-caption">
                    <v-chip size="small" color="green">{{ item.category?.name || 'Non catégorisé' }}</v-chip>
                  </div>
                </div>
              </template>

              <!-- Fournisseur -->
              <template v-slot:item.supplier="{ item }">
                <div>
                  <div class="font-weight-bold">{{ getSupplierName(item.supplier) }}</div>
                  <div class="text-caption text-grey-600">{{ getSupplierLocation(item.supplier) }}</div>
                </div>
              </template>

              <!-- Quantité -->
              <template v-slot:item.quantity="{ item }">
                <div class="text-center">
                  <div class="text-h6">{{ getDisplayQuantity(item) }}</div>
                  <div class="text-caption">{{ getQuantityType(item) }}</div>
                </div>
              </template>

              <!-- Prix -->
              <template v-slot:item.price="{ item }">
                <div class="text-right">
                  <div class="font-weight-bold">{{ formatPrice(item.supplierPrice) }}</div>
                  <div class="text-caption">par {{ getQuantityType(item) }}</div>
                </div>
              </template>

              <!-- Statuts -->
              <template v-slot:item.status="{ item }">
                <div class="d-flex flex-column gap-1">
                  <v-chip 
                    :color="getProductStatusColor(item.status)" 
                    size="small"
                  >
                    {{ getProductStatusText(item.status) }}
                  </v-chip>
                  <v-chip 
                    :color="getReceptionStatusColor(item.receptionStatus)" 
                    size="small"
                  >
                    {{ getReceptionStatusText(item.receptionStatus) }}
                  </v-chip>
                </div>
              </template>

              <!-- Date de livraison -->
              <template v-slot:item.deliveryDate="{ item }">
                <div class="text-caption">
                  {{ formatDate(getDeliveryDate(item)) }}
                </div>
              </template>

              <!-- Date de réception -->
              <template v-slot:item.receivedAt="{ item }">
                <div class="text-caption">
                  {{ formatDate(getReceptionDate(item)) }}
                </div>
              </template>

              <!-- Actions -->
              <template v-slot:item.actions="{ item }">
                <v-btn 
                  color="info" 
                  small 
                  text 
                  @click="viewProductDetails(item)"
                >
                  <v-icon small left>mdi-eye</v-icon>
                  Détails
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Modal de détails du produit -->
    <v-dialog v-model="showDetailsModal" max-width="800px">
      <v-card v-if="selectedProduct">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="info">mdi-information</v-icon>
          <span>📦 Détails du Produit en Inventaire</span>
        </v-card-title>
        
        <v-card-text>
          <v-row>
            <!-- Informations du produit -->
            <v-col cols="12" md="6">
              <h3 class="text-h6 mb-4">Informations Produit</h3>
              
              <v-list>
                <v-list-item>
                  <v-list-item-title>Nom du Produit</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.title }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Description</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.description || 'Aucune description' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Catégorie</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.category?.name || 'Non catégorisé' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Quantité en Stock</v-list-item-title>
                  <v-list-item-subtitle>{{ getDisplayQuantity(selectedProduct) }} {{ getQuantityType(selectedProduct) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Date de Livraison</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(getDeliveryDate(selectedProduct)) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Date de Réception</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(getReceptionDate(selectedProduct)) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="getDeliveryDate(selectedProduct) && getReceptionDate(selectedProduct)">
                  <v-list-item-title>
                    <v-icon color="success" class="mr-2">mdi-sync</v-icon>
                    Synchronisation
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    Les dates de livraison et réception sont synchronisées
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Prix Unitaire</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.supplierPrice?.toLocaleString() || 'N/A' }}  FCFA</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Méthode de Culture</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.farmingMethod || 'Non spécifiée' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Origine</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.origin || 'Non spécifiée' }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            
            <!-- Informations du fournisseur -->
            <v-col cols="12" md="6">
              <h3 class="text-h6 mb-4">Informations Fournisseur</h3>
              
              <v-list>
                <v-list-item>
                  <v-list-item-title>Nom du Fournisseur</v-list-item-title>
                  <v-list-item-subtitle>{{ getSupplierName(selectedProduct.supplier) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Email</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.supplier?.email || 'N/A' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Localisation</v-list-item-title>
                  <v-list-item-subtitle>{{ getSupplierLocation(selectedProduct.supplier) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="selectedProduct.supplier?.businessDetails">
                  <v-list-item-title>Entreprise</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.supplier.businessDetails.businessName || 'N/A' }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
              
              <!-- Statuts -->
              <h3 class="text-h6 mb-4 mt-6">Statuts</h3>
              
              <v-list>
                <v-list-item>
                  <v-list-item-title>Statut Produit</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getProductStatusColor(selectedProduct.status)" small>
                      {{ getProductStatusText(selectedProduct.status) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Statut Expédition</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getShipmentStatusColor(selectedProduct.shipmentStatus)" small>
                      {{ getShipmentStatusText(selectedProduct.shipmentStatus) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Statut Réception</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getReceptionStatusColor(selectedProduct.receptionStatus)" small>
                      {{ getReceptionStatusText(selectedProduct.receptionStatus) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="closeDetailsModal">
            Fermer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'

// Variables réactives
const inventory = ref([])
const loading = ref(false)
const showDetailsModal = ref(false)
const selectedProduct = ref(null)

// Statistiques
const stats = ref({
  totalReceived: 0,
  totalStock: 0,
  totalValue: 0
})

// En-têtes du tableau
const headers = [
  { text: 'Image', value: 'image', sortable: false, width: '80px' },
  { text: 'Produit', value: 'productInfo', sortable: true },
  { text: 'Fournisseur', value: 'supplier', sortable: true },
  { text: 'Quantité', value: 'quantity', sortable: true },
  { text: 'Prix', value: 'price', sortable: true },
  { text: 'Statuts', value: 'status', sortable: false },
  { text: 'Livré le', value: 'deliveryDate', sortable: true },
  { text: 'Reçu le', value: 'receivedAt', sortable: true },
  { text: 'Actions', value: 'actions', sortable: false, width: '120px' }
]

// Fonctions de récupération des données
const fetchInventory = async () => {
  loading.value = true
  try {
    console.log('📦 Récupération de l\'inventaire...')
    const response = await api.get('/api/products/received-products')
    console.log('📊 Réponse API:', response)
    console.log('📦 Données reçues:', response.data)
    inventory.value = response.data || []
    console.log('📦 Inventaire chargé:', inventory.value.length, 'produits')
    
    // Calculer les statistiques
    updateStats()
  } catch (error) {
    console.error('❌ Erreur lors du chargement de l\'inventaire:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.value.totalReceived = inventory.value.length
  stats.value.totalStock = inventory.value.reduce((total, product) => {
    return total + (getDisplayQuantity(product) || 0)
  }, 0)
  stats.value.totalValue = inventory.value.reduce((total, product) => {
    const quantity = getDisplayQuantity(product) || 0
    const price = product.supplierPrice || 0
    return total + (quantity * price)
  }, 0)
}

const refreshInventory = () => {
  fetchInventory()
}

// Fonctions utilitaires
const getSupplierName = (supplier) => {
  if (!supplier) return 'N/A'
  
  // Essayer différents champs pour le nom
  if (supplier.supplierName) return supplier.supplierName
  if (supplier.businessDetails?.businessName) return supplier.businessDetails.businessName
  if (supplier.email) return supplier.email.split('@')[0]
  
  return 'Fournisseur inconnu'
}

const getSupplierLocation = (supplier) => {
  if (!supplier) return 'Localisation non définie'
  
  // Essayer différents champs pour la localisation
  if (supplier.pickupAddress?.city) return supplier.pickupAddress.city
  if (supplier.businessDetails?.businessAddress) return supplier.businessDetails.businessAddress
  if (supplier.pickupAddress?.address) return supplier.pickupAddress.address
  
  return 'Localisation non définie'
}

const getDisplayQuantity = (product) => {
  return product.adminRequestedQuantity || product.supplierAvailableQuantity || 0
}

const getQuantityType = (product) => {
  return product.unit || 'unité'
}

const formatPrice = (price) => {
  if (!price) return '0  FCFA'
  return new Intl.NumberFormat('fr-FR').format(price) + '  FCFA'
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getMostRecentDate = (item) => {
  if (!item) return null
  
  const receivedAt = item.receivedAt ? new Date(item.receivedAt) : null
  const updatedAt = item.updatedAt ? new Date(item.updatedAt) : null
  
  // Si les deux dates existent, retourner la plus récente
  if (receivedAt && updatedAt) {
    return receivedAt > updatedAt ? item.receivedAt : item.updatedAt
  }
  
  // Si seule une date existe, la retourner
  if (receivedAt) return item.receivedAt
  if (updatedAt) return item.updatedAt
  
  // Aucune date disponible
  return null
}

const getDeliveryDate = (item) => {
  if (!item) return null
  return item.deliveryDate || null
}

const getReceptionDate = (item) => {
  if (!item) return null
  return item.receivedAt || null
}

// Fonctions de statut
const getProductStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'green',
    'REJECTED': 'red',
    'SUSPENDED': 'grey',
    'DRAFT': 'blue'
  }
  return colors[status] || 'grey'
}

const getProductStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En attente d\'approbation',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu',
    'DRAFT': 'Brouillon'
  }
  return texts[status] || status
}

const getShipmentStatusColor = (status) => {
  const colors = {
    'NOT_SHIPPED': 'orange',
    'SHIPPED': 'blue',
    'DELIVERED': 'green',
    'RETURNED': 'red'
  }
  return colors[status] || 'grey'
}

const getShipmentStatusText = (status) => {
  const texts = {
    'NOT_SHIPPED': 'Non expédié',
    'SHIPPED': 'Expédié',
    'DELIVERED': 'Livré',
    'RETURNED': 'Retourné'
  }
  return texts[status] || status
}

const getReceptionStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'RECEIVED': 'green',
    'PARTIALLY_RECEIVED': 'blue',
    'REJECTED': 'red',
    'DAMAGED': 'red',
    'QUANTITY_MISMATCH': 'orange'
  }
  return colors[status] || 'grey'
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

// Fonctions de gestion des modals
const viewProductDetails = (product) => {
  console.log('📦 Affichage des détails du produit:', product)
  selectedProduct.value = product
  showDetailsModal.value = true
}

const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedProduct.value = null
}

const handleImageError = (event) => {
  event.target.src = '/images/default-product.jpg'
}

onMounted(() => {
  fetchInventory()
})
</script>
