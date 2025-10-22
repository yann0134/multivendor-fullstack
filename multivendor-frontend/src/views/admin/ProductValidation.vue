<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="error">mdi-clipboard-check</v-icon>
            <span>📋 Validation des Produits</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher par nom ou fournisseur"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
            <v-select
              v-model="selectedStatus"
              :items="statusOptions"
              label="Filtrer par statut"
              density="compact"
              class="flex-grow-0"
              style="max-width: 200px;"
            ></v-select>
          </v-card-title>
          <v-card-subtitle>
            Gérez l'approbation et le rejet des produits soumis par les fournisseurs.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="filteredProducts"
            :loading="loading"
            class="elevation-1"
            item-value="id"
          >
            <template v-slot:item.status="{ item }">
              <v-chip 
                :color="getStatusColor(item.status)" 
                :text-color="getStatusTextColor(item.status)"
                small
              >
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>

            <template v-slot:item.supplier="{ item }">
              <div>
                <div class="font-weight-medium">{{ item.supplier?.supplierName || 'N/A' }}</div>
                <div class="text-caption text-grey-600">{{ item.supplier?.email || 'N/A' }}</div>
              </div>
            </template>

            <template v-slot:item.price="{ item }">
              <div>
                <div class="font-weight-medium">{{ formatPrice(item.sellingPrice) }}</div>
                <div class="text-caption text-grey-600">MRP: {{ formatPrice(item.mrpPrice) }}</div>
              </div>
            </template>

            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>

            <template v-slot:item.statusUpdatedAt="{ item }">
              {{ item.statusUpdatedAt ? formatDate(item.statusUpdatedAt) : '-' }}
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="success" 
                  class="mr-2"
                  @click="approveProduct(item.id)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-check</v-icon>
                </v-btn>
                
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="primary" 
                  class="mr-2"
                  @click="openQuantityDialog(item)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-package-variant</v-icon>
                </v-btn>
                
                <!-- Bouton de validation finale après confirmation du fournisseur -->
                <v-btn 
                  v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                  icon 
                  small 
                  color="success" 
                  class="mr-2"
                  @click="finalApproval(item.id)"
                  :loading="processingProducts.includes(item.id)"
                  title="Validation finale après confirmation fournisseur"
                >
                  <v-icon>mdi-check-all</v-icon>
                </v-btn>
                
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="error" 
                  class="mr-2"
                  @click="openRejectDialog(item)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-close</v-icon>
                </v-btn>

                <v-btn 
                  v-if="item.status === 'APPROVED'"
                  icon 
                  small 
                  color="warning" 
                  class="mr-2"
                  @click="suspendProduct(item.id)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-pause</v-icon>
                </v-btn>

                <v-btn 
                  icon 
                  small 
                  color="info"
                  @click="viewProductDetails(item)"
                >
                  <v-icon>mdi-eye</v-icon>
                </v-btn>
              </div>
            </template>

            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">Aucun produit trouvé.</v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de rejet -->
    <v-dialog v-model="rejectDialog" max-width="500">
      <v-card>
        <v-card-title>Rejeter le Produit</v-card-title>
        <v-card-text>
          <v-textarea
            v-model="rejectionReason"
            label="Raison du rejet"
            placeholder="Expliquez pourquoi ce produit est rejeté..."
            rows="3"
            required
          ></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="rejectDialog = false">Annuler</v-btn>
          <v-btn color="error" @click="confirmReject" :loading="rejecting">Rejeter</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de demande de quantité -->
    <v-dialog v-model="quantityDialog" max-width="500">
      <v-card>
        <v-card-title>Demander une Quantité Spécifique</v-card-title>
        <v-card-text v-if="selectedProduct">
          <v-alert type="info" variant="tonal" class="mb-4">
            <strong>Produit:</strong> {{ selectedProduct.title }}<br>
            <strong>Quantité disponible:</strong> {{ selectedProduct.supplierAvailableQuantity }} unités
          </v-alert>
          
          <v-text-field
            v-model.number="requestedQuantity"
            label="Quantité demandée"
            type="number"
            :max="selectedProduct.supplierAvailableQuantity"
            :min="1"
            :rules="[
              v => !!v || 'La quantité est requise',
              v => v > 0 || 'La quantité doit être positive',
              v => v <= selectedProduct.supplierAvailableQuantity || 'Ne peut pas dépasser la quantité disponible'
            ]"
            required
          ></v-text-field>
          
          <v-alert type="warning" variant="tonal" class="mt-4">
            <v-icon class="mr-2">mdi-information</v-icon>
            Le fournisseur devra confirmer cette quantité avant que le produit soit approuvé.
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="quantityDialog = false">Annuler</v-btn>
          <v-btn color="primary" @click="confirmQuantityRequest" :loading="requestingQuantity">
            Demander la Quantité
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de détails du produit -->
    <v-dialog v-model="detailsDialog" max-width="800">
      <v-card>
        <v-card-title>Détails du Produit</v-card-title>
        <v-card-text v-if="selectedProduct">
          <v-row>
            <v-col cols="12" md="6">
              <h3>{{ selectedProduct.title }}</h3>
              <p class="text-grey-600">{{ selectedProduct.description }}</p>
              <div class="mt-4">
                <div><strong>Prix de vente:</strong> {{ formatPrice(selectedProduct.sellingPrice) }}</div>
                <div><strong>Prix MRP:</strong> {{ formatPrice(selectedProduct.mrpPrice) }}</div>
                <div><strong>Remise:</strong> {{ selectedProduct.discountPercent }}%</div>
                <div><strong>Couleur:</strong> {{ selectedProduct.color }}</div>
                <div><strong>Tailles:</strong> {{ selectedProduct.sizes }}</div>
              </div>
              
              <!-- Informations de stock -->
              <div class="mt-4">
                <h4>📦 Informations de Stock</h4>
                <div><strong>Quantité disponible (Fournisseur):</strong> {{ selectedProduct.supplierAvailableQuantity || 0 }} unités</div>
                <div v-if="selectedProduct.adminRequestedQuantity > 0">
                  <strong>Quantité demandée (Admin):</strong> {{ selectedProduct.adminRequestedQuantity }} unités
                </div>
                <div v-if="selectedProduct.stockNegotiationPending">
                  <v-chip color="orange" size="small" class="mt-2">
                    <v-icon left>mdi-clock</v-icon>
                    Négociation en cours
                  </v-chip>
                </div>
              </div>
            </v-col>
            <v-col cols="12" md="6">
              <div v-if="selectedProduct.supplier">
                <h4>Fournisseur</h4>
                <div><strong>Nom:</strong> {{ selectedProduct.supplier.supplierName }}</div>
                <div><strong>Email:</strong> {{ selectedProduct.supplier.email }}</div>
              </div>
              <div class="mt-4">
                <h4>Statut</h4>
                <v-chip 
                  :color="getStatusColor(selectedProduct.status)" 
                  :text-color="getStatusTextColor(selectedProduct.status)"
                >
                  {{ getStatusText(selectedProduct.status) }}
                </v-chip>
                <div v-if="selectedProduct.rejectionReason" class="mt-2">
                  <strong>Raison du rejet:</strong> {{ selectedProduct.rejectionReason }}
                </div>
                <div v-if="selectedProduct.reviewedBy" class="mt-2">
                  <strong>Révisé par:</strong> {{ selectedProduct.reviewedBy }}
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="detailsDialog = false">Fermer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getPendingProducts, getApprovedProducts, getRejectedProducts, approveProduct, rejectProduct, suspendProduct } from '@/services/products'
import api from '@/services/api'

const products = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('PENDING_APPROVAL')
const processingProducts = ref([])
const rejectDialog = ref(false)
const rejecting = ref(false)
const rejectionReason = ref('')
const selectedProductForReject = ref(null)
const detailsDialog = ref(false)
const selectedProduct = ref(null)

// Variables pour la gestion des quantités
const quantityDialog = ref(false)
const requestingQuantity = ref(false)
const requestedQuantity = ref(0)
const selectedProductForQuantity = ref(null)

const statusOptions = [
  { title: 'En Attente', value: 'PENDING_APPROVAL' },
  { title: 'Approuvés', value: 'APPROVED' },
  { title: 'Rejetés', value: 'REJECTED' },
  { title: 'Suspendus', value: 'SUSPENDED' }
]

const headers = [
  { title: 'Produit', key: 'title', sortable: true },
  { title: 'Fournisseur', key: 'supplier', sortable: false },
  { title: 'Prix', key: 'price', sortable: true },
  { title: 'Stock Fournisseur', key: 'supplierAvailableQuantity', sortable: true },
  { title: 'Demande Admin', key: 'adminRequestedQuantity', sortable: true },
  { title: 'Statut', key: 'status', sortable: true },
  { title: 'Créé le', key: 'createdAt', sortable: true },
  { title: 'Mis à jour', key: 'statusUpdatedAt', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

const filteredProducts = computed(() => {
  let filtered = products.value

  if (selectedStatus.value) {
    filtered = filtered.filter(p => p.status === selectedStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.title.toLowerCase().includes(query) ||
      (p.supplier?.supplierName && p.supplier.supplierName.toLowerCase().includes(query)) ||
      (p.supplier?.email && p.supplier.email.toLowerCase().includes(query))
    )
  }

  return filtered
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'success',
    'REJECTED': 'error',
    'SUSPENDED': 'warning'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return status === 'PENDING_APPROVAL' ? 'white' : 'white'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En Attente',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu'
  }
  return texts[status] || status
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

const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await getPendingProducts()
    products.value = response.data.content || response.data
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  } finally {
    loading.value = false
  }
}

const approveProductAction = async (productId) => {
  processingProducts.value.push(productId)
  try {
    await approveProduct(productId)
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors de l\'approbation:', error)
  } finally {
    processingProducts.value = processingProducts.value.filter(id => id !== productId)
  }
}

const openRejectDialog = (product) => {
  selectedProductForReject.value = product
  rejectionReason.value = ''
  rejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectionReason.value.trim()) {
    return
  }

  rejecting.value = true
  try {
    await rejectProduct(selectedProductForReject.value.id, rejectionReason.value)
    rejectDialog.value = false
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors du rejet:', error)
  } finally {
    rejecting.value = false
  }
}

const suspendProductAction = async (productId) => {
  processingProducts.value.push(productId)
  try {
    await suspendProduct(productId)
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors de la suspension:', error)
  } finally {
    processingProducts.value = processingProducts.value.filter(id => id !== productId)
  }
}

const viewProductDetails = (product) => {
  selectedProduct.value = product
  detailsDialog.value = true
}

// Méthodes pour la gestion des quantités
const openQuantityDialog = (product) => {
  selectedProductForQuantity.value = product
  selectedProduct.value = product
  requestedQuantity.value = 0
  quantityDialog.value = true
}

const confirmQuantityRequest = async () => {
  if (!requestedQuantity.value || requestedQuantity.value <= 0) {
    return
  }
  
  requestingQuantity.value = true
  
  try {
    // ✅ Utiliser l'instance API configurée
    const response = await api.put(`/api/products/${selectedProductForQuantity.value.id}/request-quantity?requestedQuantity=${requestedQuantity.value}`)
    
    if (response.status === 200) {
      // Mettre à jour le produit dans la liste
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      quantityDialog.value = false
      // Afficher un message de succès
      console.log('✅ Demande de quantité envoyée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    requestingQuantity.value = false
  }
}

// Validation finale après confirmation du fournisseur
const finalApproval = async (productId) => {
  processingProducts.value.push(productId)
  
  try {
    const response = await api.put(`/api/products/${productId}/final-approval`)
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Validation finale effectuée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur lors de la validation finale:', error)
  } finally {
    const index = processingProducts.value.indexOf(productId)
    if (index > -1) {
      processingProducts.value.splice(index, 1)
    }
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.flex-grow-0 {
  flex-grow: 0;
}
</style>
