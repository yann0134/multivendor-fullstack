<template>
  <v-container fluid>
    <!-- En-tête -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-truck-delivery</v-icon>
            <span>📦 Réceptions Entrepôt</span>
          </v-card-title>
          <v-card-subtitle>
            Validez la réception des produits expédiés par les fournisseurs
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <v-card color="green" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Reçus Aujourd'hui</h3>
          <p class="text-h4">{{ stats.todayReceived }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="blue" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
          <h3 class="text-h6">En Transit</h3>
          <p class="text-h4">{{ stats.inProgress }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="red" dark class="text-center pa-4">
          <v-icon size="48" class="mb-2">mdi-alert-circle</v-icon>
          <h3 class="text-h6">Problèmes</h3>
          <p class="text-h4">{{ stats.problems }}</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Tableau des réceptions -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center">
            <span>📋 Produits en Attente de Réception</span>
            <v-btn 
              color="primary" 
              @click="refreshReceptions"
              :loading="loading"
            >
              <v-icon left>mdi-refresh</v-icon>
              Actualiser
            </v-btn>
          </v-card-title>
          
          <v-data-table
            :headers="headers"
            :items="receptions"
            :loading="loading"
            :items-per-page="10"
            class="elevation-1"
          >
            <!-- Image du produit -->
            <template v-slot:item.image="{ item }">
              <v-avatar size="60" rounded>
                <v-img 
                  :src="item.image || '/images/default-product.jpg'" 
                  :alt="item.title"
                  @error="handleImageError"
                ></v-img>
              </v-avatar>
            </template>

            <!-- Informations du produit -->
            <template v-slot:item.productInfo="{ item }">
              <div>
                <div class="font-weight-medium text-h6">{{ item.title }}</div>
                <div class="text-caption text-grey">{{ item.category?.name || 'Non catégorisé' }}</div>
                <div class="text-caption">
                  <v-icon small class="mr-1">mdi-tag</v-icon>
                  ID: {{ item.id }}
                </div>
              </div>
            </template>

            <!-- Fournisseur -->
            <template v-slot:item.supplier="{ item }">
              <div>
                <div class="font-weight-medium">
                  {{ getSupplierName(item.supplier) }}
                </div>
                <div class="text-caption">
                  {{ item.supplier?.email || 'Email non disponible' }}
                </div>
                <div class="text-caption">
                  <v-icon small class="mr-1">mdi-map-marker</v-icon>
                  {{ getSupplierLocation(item.supplier) }}
                </div>
              </div>
            </template>

            <!-- Quantité -->
            <template v-slot:item.quantity="{ item }">
              <div class="text-center">
                <div class="text-h6 font-weight-bold">
                  {{ getDisplayQuantity(item) }}
                </div>
                <div class="text-caption text-grey">
                  {{ getQuantityType(item) }}
                </div>
              </div>
            </template>

            <!-- Statut d'envoi -->
            <template v-slot:item.shipmentStatus="{ item }">
              <v-chip
                :color="getShipmentStatusColor(item.shipmentStatus)"
                small
                :text-color="getShipmentStatusTextColor(item.shipmentStatus)"
              >
                <v-icon small left>{{ getShipmentStatusIcon(item.shipmentStatus) }}</v-icon>
                {{ getShipmentStatusText(item.shipmentStatus) }}
              </v-chip>
            </template>

            <!-- Statut de réception -->
            <template v-slot:item.receptionStatus="{ item }">
              <v-chip
                :color="getReceptionStatusColor(item.receptionStatus)"
                small
                :text-color="getReceptionStatusTextColor(item.receptionStatus)"
              >
                <v-icon small left>{{ getReceptionStatusIcon(item.receptionStatus) }}</v-icon>
                {{ getReceptionStatusText(item.receptionStatus) }}
              </v-chip>
            </template>

            <!-- Date d'expédition -->
            <template v-slot:item.shippedAt="{ item }">
              <div class="text-center">
                <div class="text-body-2">{{ formatDate(item.updatedAt) }}</div>
                <div class="text-caption text-grey">
                  <v-icon small class="mr-1">mdi-truck</v-icon>
                  Expédié
                </div>
              </div>
            </template>

            <!-- Actions -->
            <template v-slot:item.actions="{ item }">
              <div class="d-flex flex-column gap-2">
                <!-- Bouton Valider -->
                <v-btn
                  v-if="item.receptionStatus === 'PENDING'"
                  color="success"
                  small
                  :loading="processingReceptions.includes(item.id)"
                  @click="confirmReception(item)"
                  :disabled="item.shipmentStatus !== 'SHIPPED'"
                >
                  <v-icon small left>mdi-check</v-icon>
                  Valider
                </v-btn>

                <!-- Bouton Détails -->
                <v-btn
                  color="info"
                  small
                  text
                  @click="viewProductDetails(item)"
                >
                  <v-icon small left>mdi-eye</v-icon>
                  Détails
                </v-btn>
              </div>
            </template>

            <!-- Message si pas de données -->
            <template v-slot:no-data>
              <div class="text-center pa-8">
                <v-icon size="64" color="grey lighten-2">mdi-package-variant-closed</v-icon>
                <h3 class="text-h6 mt-4 mb-2">Aucun produit en attente de réception</h3>
                <p class="text-body-2 text-grey">
                  Tous les produits ont été traités ou aucun produit n'a été expédié récemment.
                </p>
                <v-btn 
                  color="primary" 
                  class="mt-4"
                  @click="refreshReceptions"
                >
                  <v-icon left>mdi-refresh</v-icon>
                  Actualiser
                </v-btn>
              </div>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Modal de détails du produit -->
    <v-dialog v-model="showDetailsModal" max-width="800px">
      <v-card v-if="selectedProduct">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="info">mdi-information</v-icon>
          <span>📦 Détails du Produit</span>
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
                  <v-list-item-title>Quantité</v-list-item-title>
                  <v-list-item-subtitle>{{ getDisplayQuantity(selectedProduct) }} {{ getQuantityType(selectedProduct) }}</v-list-item-subtitle>
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
          <v-btn 
            v-if="selectedProduct.receptionStatus === 'PENDING'"
            color="success" 
            @click="confirmReception(selectedProduct)"
            :disabled="selectedProduct.shipmentStatus !== 'SHIPPED'"
          >
            <v-icon left>mdi-check</v-icon>
            Valider la Réception
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Modal de confirmation de réception -->
    <v-dialog v-model="showReceptionModal" max-width="600px" persistent>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="success">mdi-check-circle</v-icon>
          <span>Confirmer la Réception</span>
        </v-card-title>
        
        <v-card-text v-if="selectedProduct">
          <div class="mb-4">
            <h3 class="text-h6 mb-2">📦 {{ selectedProduct.title }}</h3>
            <p class="text-body-2 text-grey mb-3">{{ selectedProduct.category?.name || 'Non catégorisé' }}</p>
          </div>

          <v-alert type="info" class="mb-4">
            <v-icon left>mdi-information</v-icon>
            <strong>Quantité à recevoir :</strong> {{ getDisplayQuantity(selectedProduct) }} {{ getQuantityType(selectedProduct) }}
          </v-alert>

          <v-alert type="warning" class="mb-4">
            <v-icon left>mdi-alert</v-icon>
            <strong>Vérifiez la qualité et la quantité</strong> avant de confirmer la réception.
          </v-alert>

          <div class="d-flex align-center mb-4">
            <v-icon class="mr-2" color="primary">mdi-truck</v-icon>
            <span class="text-body-1">
              <strong>Fournisseur :</strong> {{ selectedProduct.supplier?.supplierName || 'N/A' }}
            </span>
          </div>
          
          <div class="d-flex align-center mb-4">
            <v-icon class="mr-2" color="primary">mdi-map-marker</v-icon>
            <span class="text-body-1">
              <strong>Localisation :</strong> {{ getSupplierLocation(selectedProduct.supplier) }}
            </span>
          </div>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn 
            color="grey" 
            text 
            @click="cancelReception"
            :disabled="processingReceptions.includes(selectedProduct?.id)"
          >
            Annuler
          </v-btn>
          <v-btn 
            color="success" 
            @click="confirmReceptionAction"
            :loading="processingReceptions.includes(selectedProduct?.id)"
          >
            <v-icon left>mdi-check</v-icon>
            Confirmer la Réception
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

// Variables réactives
const receptions = ref([])
const loading = ref(false)
const processingReceptions = ref([])
const showReceptionModal = ref(false)
const showDetailsModal = ref(false)
const selectedProduct = ref(null)

// Statistiques
const stats = ref({
  todayReceived: 0,
  inProgress: 0,
  problems: 0
})

// En-têtes du tableau
const headers = [
  { text: 'Image', value: 'image', sortable: false, width: '80px' },
  { text: 'Produit', value: 'productInfo', sortable: true },
  { text: 'Fournisseur', value: 'supplier', sortable: true },
  { text: 'Quantité', value: 'quantity', sortable: true, align: 'center' },
  { text: 'Statut Envoi', value: 'shipmentStatus', sortable: true, align: 'center' },
  { text: 'Statut Réception', value: 'receptionStatus', sortable: true, align: 'center' },
  { text: 'Date Expédition', value: 'shippedAt', sortable: true, align: 'center' },
  { text: 'Actions', value: 'actions', sortable: false, align: 'center' }
]

// Fonctions utilitaires
const getDisplayQuantity = (product) => {
  if (product.adminRequestedQuantity > 0) {
    return product.adminRequestedQuantity
  } else if (product.supplierAvailableQuantity > 0) {
    return product.supplierAvailableQuantity
  }
  return 0
}

const getQuantityType = (product) => {
  return product.unit || 'unité(s)'
}

const getSupplierName = (supplier) => {
  if (!supplier) {
    console.log('❌ Fournisseur null ou undefined pour le nom')
    return 'Fournisseur non trouvé'
  }
  
  console.log('🔍 Debug nom fournisseur:', supplier)
  
  // Essayer supplierName d'abord
  if (supplier.supplierName) {
    console.log('✅ Nom trouvé (supplierName):', supplier.supplierName)
    return supplier.supplierName
  }
  
  // Essayer businessDetails.businessName
  if (supplier.businessDetails?.businessName) {
    console.log('✅ Nom trouvé (businessDetails.businessName):', supplier.businessDetails.businessName)
    return supplier.businessDetails.businessName
  }
  
  // Essayer l'email comme fallback
  if (supplier.email) {
    console.log('⚠️ Utilisation de l\'email comme nom:', supplier.email)
    return `Fournisseur (${supplier.email})`
  }
  
  console.log('❌ Aucun nom trouvé pour le fournisseur')
  return 'Nom non défini'
}

const getSupplierLocation = (supplier) => {
  if (!supplier) {
    console.log('❌ Fournisseur null ou undefined')
    return 'Fournisseur non trouvé'
  }
  
  console.log('🔍 Debug fournisseur:', supplier)
  
  // Essayer pickupAddress.city d'abord
  if (supplier.pickupAddress?.city) {
    console.log('✅ Localisation trouvée (pickupAddress.city):', supplier.pickupAddress.city)
    return supplier.pickupAddress.city
  }
  
  // Essayer businessDetails.businessAddress
  if (supplier.businessDetails?.businessAddress) {
    console.log('✅ Localisation trouvée (businessDetails.businessAddress):', supplier.businessDetails.businessAddress)
    return supplier.businessDetails.businessAddress
  }
  
  // Essayer pickupAddress.address
  if (supplier.pickupAddress?.address) {
    console.log('✅ Localisation trouvée (pickupAddress.address):', supplier.pickupAddress.address)
    return supplier.pickupAddress.address
  }
  
  // Essayer l'email comme fallback
  if (supplier.email) {
    console.log('⚠️ Utilisation de l\'email comme localisation:', supplier.email)
    return `Contact: ${supplier.email}`
  }
  
  console.log('❌ Aucune localisation trouvée pour le fournisseur')
  return 'Localisation non définie'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

// Fonctions de statut
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

const getShipmentStatusIcon = (status) => {
  const icons = {
    'NOT_SHIPPED': 'mdi-package-variant',
    'PREPARING': 'mdi-package-up',
    'SHIPPED': 'mdi-truck',
    'IN_TRANSIT': 'mdi-truck-delivery',
    'DELIVERED': 'mdi-check-circle',
    'FAILED_DELIVERY': 'mdi-alert-circle'
  }
  return icons[status] || 'mdi-help'
}

const getShipmentStatusText = (status) => {
  const texts = {
    'NOT_SHIPPED': 'Non expédié',
    'PREPARING': 'En préparation',
    'SHIPPED': 'Expédié',
    'IN_TRANSIT': 'En transit',
    'DELIVERED': 'Livré',
    'FAILED_DELIVERY': 'Échec livraison'
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
    'QUANTITY_MISMATCH': 'red'
  }
  return colors[status] || 'grey'
}

const getReceptionStatusTextColor = (status) => {
  return 'white'
}

const getReceptionStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock',
    'RECEIVED': 'mdi-check-circle',
    'PARTIALLY_RECEIVED': 'mdi-check-circle-outline',
    'REJECTED': 'mdi-close-circle',
    'DAMAGED': 'mdi-alert-circle',
    'QUANTITY_MISMATCH': 'mdi-alert'
  }
  return icons[status] || 'mdi-help'
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

// Fonctions pour les statuts de produit
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

// Fonctions de gestion
const fetchReceptions = async () => {
  loading.value = true
  try {
    console.log('📦 Récupération des produits en attente de réception...')
    
    const response = await api.get('/api/products/pending-reception')
    
    if (response.status === 200) {
      receptions.value = response.data
      console.log('✅ Produits récupérés:', receptions.value.length)
      
      // Debug détaillé de chaque produit
      receptions.value.forEach((product, index) => {
        console.log(`🔍 Debug Produit ${index + 1}:`, {
          id: product.id,
          title: product.title,
          supplier: product.supplier,
          supplierName: product.supplier?.supplierName,
          supplierEmail: product.supplier?.email,
          pickupAddress: product.supplier?.pickupAddress,
          businessDetails: product.supplier?.businessDetails
        })
        
        // Debug complet de l'objet supplier
        console.log(`📦 Objet supplier complet pour ${product.title}:`, product.supplier)
        
        // Vérifier si supplier existe
        if (product.supplier) {
          console.log(`✅ Supplier trouvé pour ${product.title}:`, product.supplier)
        } else {
          console.log(`❌ Supplier NULL pour ${product.title}`)
        }
      })
      
      // Mettre à jour les statistiques
      updateStats()
    }
  } catch (error) {
    console.error('❌ Erreur lors de la récupération des réceptions:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
    
    // Afficher une notification d'erreur
    alert(`❌ Erreur lors du chargement: ${error.response?.data?.message || error.message}`)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.value.todayReceived = receptions.value.filter(p => {
    const today = new Date().toDateString()
    const receivedDate = p.updatedAt ? new Date(p.updatedAt).toDateString() : null
    return p.receptionStatus === 'RECEIVED' && receivedDate === today
  }).length
  stats.value.inProgress = receptions.value.filter(p => p.receptionStatus === 'PENDING').length
  stats.value.problems = receptions.value.filter(p => 
    ['REJECTED', 'DAMAGED', 'QUANTITY_MISMATCH'].includes(p.receptionStatus)
  ).length
}

const refreshReceptions = () => {
  fetchReceptions()
}

const confirmReception = (product) => {
  selectedProduct.value = product
  showReceptionModal.value = true
}

const cancelReception = () => {
  showReceptionModal.value = false
  selectedProduct.value = null
}

const confirmReceptionAction = async () => {
  if (!selectedProduct.value) return
  
  const productId = selectedProduct.value.id
  processingReceptions.value.push(productId)
  
  try {
    console.log(`📦 Confirmation de réception pour le produit ${productId}`)
    
    const response = await api.put(`/api/products/${productId}/reception-status`, { 
      status: 'RECEIVED' 
    })
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = receptions.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        receptions.value[index] = updatedProduct
      }
      
      console.log('✅ Réception confirmée avec succès:', updatedProduct)
      
      // Fermer le modal
      showReceptionModal.value = false
      selectedProduct.value = null
      
      // Mettre à jour les statistiques
      updateStats()
      
      // Afficher une notification de succès
      alert(`✅ Produit reçu avec succès !`)
    }
  } catch (error) {
    console.error('❌ Erreur lors de la confirmation de réception:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
    
    // Afficher une notification d'erreur
    alert(`❌ Erreur lors de la réception: ${error.response?.data?.message || error.message}`)
  } finally {
    const index = processingReceptions.value.indexOf(productId)
    if (index > -1) {
      processingReceptions.value.splice(index, 1)
    }
  }
}

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

// Cycle de vie
onMounted(() => {
  fetchReceptions()
})
</script>

<style scoped>
.gap-2 {
  gap: 8px;
}
</style>
