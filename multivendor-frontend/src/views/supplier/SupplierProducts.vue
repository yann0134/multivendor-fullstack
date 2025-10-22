<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="warning">mdi-package-variant</v-icon>
            <span>📦 Mes Produits</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher mes produits"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
            <v-btn color="warning" @click="goToAddProduct">
              <v-icon left>mdi-plus</v-icon>
              Nouveau Produit
            </v-btn>
          </v-card-title>
          <v-card-subtitle>
            Gérez vos produits agricoles soumis à la plateforme.
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
            <template v-slot:item.title="{ item }">
              <div class="d-flex align-center">
                <v-avatar size="40" class="mr-3">
                  <v-img 
                    v-if="item.images && item.images.length > 0" 
                    :src="item.images[0]" 
                    alt="Product"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
                <div>
                  <div class="font-weight-medium">{{ item.title }}</div>
                  <div class="text-caption text-grey-600">{{ item.description?.substring(0, 50) }}...</div>
                </div>
              </div>
            </template>

            <template v-slot:item.status="{ item }">
              <v-chip 
                :color="getStatusColor(item.status)" 
                :text-color="getStatusTextColor(item.status)"
                small
              >
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>

            <template v-slot:item.price="{ item }">
              <div>
                <div class="font-weight-medium">{{ formatPrice(item.sellingPrice) }}</div>
                <div class="text-caption text-grey-600">MRP: {{ formatPrice(item.mrpPrice) }}</div>
                <div class="text-caption text-success">{{ item.discountPercent }}% de remise</div>
              </div>
            </template>

            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>

            <template v-slot:item.statusUpdatedAt="{ item }">
              {{ item.statusUpdatedAt ? formatDate(item.statusUpdatedAt) : '-' }}
            </template>

            <!-- Template pour afficher le stock disponible -->
            <template v-slot:item.supplierAvailableQuantity="{ item }">
              <div class="d-flex align-center">
                <v-icon color="blue" size="small" class="mr-1">mdi-package-variant</v-icon>
                <span class="font-weight-medium">{{ item.supplierAvailableQuantity || 0 }}</span>
                <span class="text-caption ml-1">unités</span>
              </div>
            </template>

            <!-- Template pour afficher la demande admin -->
            <template v-slot:item.adminRequestedQuantity="{ item }">
              <div v-if="item.adminRequestedQuantity > 0" class="d-flex align-center">
                <v-icon color="orange" size="small" class="mr-1">mdi-account-tie</v-icon>
                <span class="font-weight-medium text-orange">{{ item.adminRequestedQuantity }}</span>
                <span class="text-caption ml-1">unités</span>
                <v-chip 
                  v-if="item.stockNegotiationPending"
                  color="orange" 
                  size="x-small" 
                  class="ml-2"
                >
                  Négociation
                </v-chip>
              </div>
              <div v-else class="text-grey-500">
                <v-icon color="grey" size="small" class="mr-1">mdi-minus</v-icon>
                Aucune demande
              </div>
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <!-- Bouton pour confirmer la quantité demandée par l'admin -->
                <v-btn 
                  v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                  icon 
                  small 
                  color="success" 
                  class="mr-2"
                  @click="confirmQuantity(item)"
                  :loading="processingProducts.includes(item.id)"
                  title="Accepter la demande admin"
                >
                  <v-icon>mdi-check-circle</v-icon>
                </v-btn>
                
                <!-- Bouton pour refuser la demande admin -->
                <v-btn 
                  v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                  icon 
                  small 
                  color="error" 
                  class="mr-2"
                  @click="rejectQuantityRequest(item)"
                  :loading="processingProducts.includes(item.id)"
                  title="Refuser la demande admin"
                >
                  <v-icon>mdi-close-circle</v-icon>
                </v-btn>

                <v-btn 
                  icon 
                  small 
                  color="info" 
                  class="mr-2"
                  @click="viewProductDetails(item)"
                >
                  <v-icon>mdi-eye</v-icon>
                </v-btn>

                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="warning" 
                  class="mr-2"
                  @click="editProduct(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>

                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="error"
                  @click="deleteProduct(item.id)"
                  :loading="deletingProducts.includes(item.id)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </div>
            </template>

            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">
                <div class="text-center">
                  <v-icon size="48" class="mb-2">mdi-package-variant-closed</v-icon>
                  <div>Aucun produit trouvé.</div>
                  <v-btn color="warning" class="mt-2" @click="goToAddProduct">
                    Ajouter votre premier produit
                  </v-btn>
                </div>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

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
                <h4>Informations de prix</h4>
                <div><strong>Prix de vente:</strong> {{ formatPrice(selectedProduct.sellingPrice) }}</div>
                <div><strong>Prix MRP:</strong> {{ formatPrice(selectedProduct.mrpPrice) }}</div>
                <div><strong>Remise:</strong> {{ selectedProduct.discountPercent }}%</div>
                <div><strong>Couleur:</strong> {{ selectedProduct.color || 'Non spécifiée' }}</div>
                <div><strong>Tailles:</strong> {{ selectedProduct.sizes || 'Non spécifiées' }}</div>
              </div>

              <div class="mt-4" v-if="selectedProduct.origin || selectedProduct.farmingMethod">
                <h4>Informations agricoles</h4>
                <div v-if="selectedProduct.origin"><strong>Origine:</strong> {{ selectedProduct.origin }}</div>
                <div v-if="selectedProduct.farmingMethod"><strong>Méthode:</strong> {{ selectedProduct.farmingMethod }}</div>
                <div v-if="selectedProduct.season"><strong>Saison:</strong> {{ selectedProduct.season }}</div>
                <div v-if="selectedProduct.unit"><strong>Unité:</strong> {{ selectedProduct.unit }}</div>
              </div>
            </v-col>
            
            <v-col cols="12" md="6">
              <div class="mt-4">
                <h4>Statut</h4>
                <v-chip 
                  :color="getStatusColor(selectedProduct.status)" 
                  :text-color="getStatusTextColor(selectedProduct.status)"
                  class="mb-2"
                >
                  {{ getStatusText(selectedProduct.status) }}
                </v-chip>
                
                <div v-if="selectedProduct.rejectionReason" class="mt-2">
                  <strong>Raison du rejet:</strong> 
                  <div class="text-error mt-1">{{ selectedProduct.rejectionReason }}</div>
                </div>
                
                <div v-if="selectedProduct.reviewedBy" class="mt-2">
                  <strong>Révisé par:</strong> {{ selectedProduct.reviewedBy }}
                </div>
                
                <div class="mt-2">
                  <strong>Créé le:</strong> {{ formatDate(selectedProduct.createdAt) }}
                </div>
                
                <div v-if="selectedProduct.statusUpdatedAt" class="mt-2">
                  <strong>Dernière mise à jour:</strong> {{ formatDate(selectedProduct.statusUpdatedAt) }}
                </div>
              </div>

              <!-- Images du produit -->
              <div v-if="selectedProduct.images && selectedProduct.images.length > 0" class="mt-4">
                <h4>Images</h4>
                <v-row>
                  <v-col 
                    v-for="(image, index) in selectedProduct.images" 
                    :key="index" 
                    cols="6"
                  >
                    <v-img
                      :src="image"
                      height="100"
                      cover
                      class="rounded"
                    ></v-img>
                  </v-col>
                </v-row>
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
import { useRouter } from 'vue-router'
import { getProductsByStatusAndSupplierEmail, deleteProduct as deleteProductApi } from '@/services/products'
import api from '@/services/api'

const router = useRouter()

const products = ref([])
const loading = ref(false)
const searchQuery = ref('')
const detailsDialog = ref(false)
const selectedProduct = ref(null)
const deletingProducts = ref([])
const processingProducts = ref([])

const headers = [
  { title: 'Produit', key: 'title', sortable: true },
  { title: 'Statut', key: 'status', sortable: true },
  { title: 'Prix', key: 'price', sortable: true },
  { title: 'Stock Disponible', key: 'supplierAvailableQuantity', sortable: true },
  { title: 'Demande Admin', key: 'adminRequestedQuantity', sortable: true },
  { title: 'Créé le', key: 'createdAt', sortable: true },
  { title: 'Mis à jour', key: 'statusUpdatedAt', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

const filteredProducts = computed(() => {
  if (!searchQuery.value) {
    return products.value
  }
  const query = searchQuery.value.toLowerCase()
  return products.value.filter(p => 
    p.title.toLowerCase().includes(query) ||
    p.description.toLowerCase().includes(query) ||
    p.color?.toLowerCase().includes(query)
  )
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'success',
    'REJECTED': 'error',
    'SUSPENDED': 'warning',
    'DRAFT': 'grey'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return 'white'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En Attente',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu',
    'DRAFT': 'Brouillon'
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
    // Récupérer tous les statuts pour ce fournisseur
    const allStatuses = ['PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'SUSPENDED', 'DRAFT']
    const allProducts = []
    
    for (const status of allStatuses) {
      try {
        const response = await getProductsByStatusAndSupplierEmail(status, { page: 0, size: 100 })
        const productsData = response.data.content || response.data
        allProducts.push(...productsData)
      } catch (error) {
        console.warn(`Erreur lors du chargement des produits ${status}:`, error)
      }
    }
    
    products.value = allProducts
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  } finally {
    loading.value = false
  }
}

const goToAddProduct = () => {
  router.push('/supplier/products/add')
}

const viewProductDetails = (product) => {
  selectedProduct.value = product
  detailsDialog.value = true
}

const editProduct = (product) => {
  router.push(`/supplier/products/edit/${product.id}`)
}

const deleteProduct = async (productId) => {
  if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
    deletingProducts.value.push(productId)
    try {
      await deleteProductApi(productId)
      await fetchProducts()
    } catch (error) {
      console.error('Erreur lors de la suppression:', error)
    } finally {
      deletingProducts.value = deletingProducts.value.filter(id => id !== productId)
    }
  }
}

// Méthode pour confirmer la quantité demandée par l'admin
const confirmQuantity = async (product) => {
  processingProducts.value.push(product.id)
  
  try {
    const response = await api.put(`/api/products/${product.id}/confirm-quantity`)
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Quantité confirmée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    const index = processingProducts.value.indexOf(product.id)
    if (index > -1) {
      processingProducts.value.splice(index, 1)
    }
  }
}

// Méthode pour refuser la demande de quantité de l'admin
const rejectQuantityRequest = async (product) => {
  processingProducts.value.push(product.id)
  
  try {
    const response = await api.put(`/api/products/${product.id}/reject-quantity-request`)
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Demande admin refusée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    const index = processingProducts.value.indexOf(product.id)
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
