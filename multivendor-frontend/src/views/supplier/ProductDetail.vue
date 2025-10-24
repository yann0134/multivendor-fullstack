<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-btn
              icon
              @click="$router.go(-1)"
              class="mr-3"
            >
              <v-icon>mdi-arrow-left</v-icon>
            </v-btn>
            <v-icon class="mr-3" color="primary">mdi-package-variant</v-icon>
            <span>📦 Détails du Produit</span>
          </v-card-title>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="product">
      <!-- Galerie d'images -->
      <v-col cols="12" md="6">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2" color="primary">mdi-image-multiple</v-icon>
            <span>🖼️ Galerie d'Images</span>
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              variant="outlined"
              size="small"
              @click="triggerImageUpload"
              :loading="uploading"
            >
              <v-icon left>mdi-plus</v-icon>
              Ajouter une image
            </v-btn>
          </v-card-title>
          <v-card-text>
            <ProductImageGallery 
              :images="product.images || []" 
              :show-delete-buttons="true"
              @delete-image="removeImage"
            />
            
            <!-- Input file caché pour l'upload -->
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleImageUpload"
            />
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Informations du produit -->
      <v-col cols="12" md="6">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2" color="primary">mdi-information</v-icon>
            <span>📋 Informations du Produit</span>
          </v-card-title>
          <v-card-text>
            <h2 class="text-h4 mb-4">{{ product.title }}</h2>
            <p class="text-body-1 mb-6">{{ product.description }}</p>
            
            <!-- Statut du produit -->
            <v-chip
              :color="getStatusColor(product.status)"
              :text-color="getStatusTextColor(product.status)"
              class="mb-4"
              size="large"
            >
              <v-icon left>{{ getStatusIcon(product.status) }}</v-icon>
              {{ getStatusText(product.status) }}
            </v-chip>

            <!-- Informations de base -->
            <v-list density="compact" class="mb-4">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-tag</v-icon>
                </template>
                <v-list-item-title>Catégorie</v-list-item-title>
                <v-list-item-subtitle>{{ product.category?.name || 'N/A' }}</v-list-item-subtitle>
              </v-list-item>

              <v-list-item v-if="product.subCategory">
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-tag-multiple</v-icon>
                </template>
                <v-list-item-title>Sous-catégorie</v-list-item-title>
                <v-list-item-subtitle>{{ product.subCategory.name }}</v-list-item-subtitle>
              </v-list-item>

              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-currency-usd</v-icon>
                </template>
                <v-list-item-title>Prix Fournisseur</v-list-item-title>
                <v-list-item-subtitle>{{ formatPrice(product.supplierPrice) }}</v-list-item-subtitle>
              </v-list-item>

              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-currency-usd-circle</v-icon>
                </template>
                <v-list-item-title>Prix de Vente</v-list-item-title>
                <v-list-item-subtitle>{{ formatPrice(product.sellingPrice) }}</v-list-item-subtitle>
              </v-list-item>

              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-package-variant</v-icon>
                </template>
                <v-list-item-title>Stock Disponible</v-list-item-title>
                <v-list-item-subtitle>{{ formatQuantity(product.supplierAvailableQuantity) }} {{ product.unit || 'unité(s)' }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>

            <!-- Actions -->
            <v-row class="mt-4">
              <v-col cols="6">
                <v-btn
                  color="primary"
                  variant="outlined"
                  block
                  @click="editProduct"
                >
                  <v-icon left>mdi-pencil</v-icon>
                  Modifier
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn
                  color="error"
                  variant="outlined"
                  block
                  @click="deleteProduct"
                >
                  <v-icon left>mdi-delete</v-icon>
                  Supprimer
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Informations agricoles -->
    <v-row v-if="product">
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2" color="success">mdi-sprout</v-icon>
            <span>🌾 Informations Agricoles</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4" v-if="product.origin">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-map-marker</v-icon>
                  </template>
                  <v-list-item-title>Origine</v-list-item-title>
                  <v-list-item-subtitle>{{ product.origin }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>

              <v-col cols="12" md="4" v-if="product.farmingMethod">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-leaf</v-icon>
                  </template>
                  <v-list-item-title>Méthode de Culture</v-list-item-title>
                  <v-list-item-subtitle>{{ product.farmingMethod }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>

              <v-col cols="12" md="4" v-if="product.season">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-calendar</v-icon>
                  </template>
                  <v-list-item-title>Saison</v-list-item-title>
                  <v-list-item-subtitle>{{ product.season }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>

              <v-col cols="12" md="4" v-if="product.weight">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-scale</v-icon>
                  </template>
                  <v-list-item-title>Poids</v-list-item-title>
                  <v-list-item-subtitle>{{ formatWeight(product.weight) }} {{ product.unit || 'kg' }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>

              <v-col cols="12" md="4" v-if="product.storageConditions">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-fridge</v-icon>
                  </template>
                  <v-list-item-title>Conditions de Stockage</v-list-item-title>
                  <v-list-item-subtitle>{{ product.storageConditions }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>

              <v-col cols="12" md="4" v-if="product.nutritionalInfo">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-food</v-icon>
                  </template>
                  <v-list-item-title>Informations Nutritionnelles</v-list-item-title>
                  <v-list-item-subtitle>{{ product.nutritionalInfo }}</v-list-item-subtitle>
                </v-list-item>
              </v-col>
            </v-row>

            <!-- Badges de caractéristiques -->
            <div class="d-flex flex-wrap gap-2 mt-4">
              <v-chip
                v-if="product.organic"
                color="success"
                variant="tonal"
              >
                <v-icon left>mdi-leaf</v-icon>
                Bio
              </v-chip>

              <v-chip
                v-if="product.local"
                color="info"
                variant="tonal"
              >
                <v-icon left>mdi-map-marker</v-icon>
                Local
              </v-chip>

              <v-chip
                v-if="product.fresh"
                color="green"
                variant="tonal"
              >
                <v-icon left>mdi-sprout</v-icon>
                Frais
              </v-chip>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Loading state -->
    <v-row v-else-if="loading">
      <v-col cols="12">
        <v-card>
          <v-card-text class="text-center pa-8">
            <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
            <div class="mt-4">Chargement des détails du produit...</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Error state -->
    <v-row v-else-if="error">
      <v-col cols="12">
        <v-card>
          <v-card-text class="text-center pa-8">
            <v-icon size="64" color="error">mdi-alert-circle</v-icon>
            <div class="text-h6 mt-4">Erreur lors du chargement</div>
            <div class="text-body-2 text-grey">{{ error }}</div>
            <v-btn color="primary" class="mt-4" @click="fetchProduct">
              <v-icon left>mdi-refresh</v-icon>
              Réessayer
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import ProductImageGallery from '@/components/supplier/ProductImageGallery.vue'
import { getProductImages, addProductImage, deleteProductImage } from '@/services/productImages'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(false)
const error = ref(null)
const uploading = ref(false)
const fileInput = ref(null)

// Utility functions
const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price).replace('XOF', 'FCFA')
}

const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
}

const formatWeight = (weight) => {
  if (!weight) return '0'
  return new Intl.NumberFormat('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3
  }).format(weight)
}

const getStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'green',
    'REJECTED': 'red',
    'SUSPENDED': 'grey',
    'DRAFT': 'blue'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return status === 'APPROVED' ? 'white' : 'white'
}

const getStatusIcon = (status) => {
  const icons = {
    'PENDING_APPROVAL': 'mdi-clock',
    'APPROVED': 'mdi-check-circle',
    'REJECTED': 'mdi-close-circle',
    'SUSPENDED': 'mdi-pause-circle',
    'DRAFT': 'mdi-file-document'
  }
  return icons[status] || 'mdi-help-circle'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En attente d\'approbation',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu',
    'DRAFT': 'Brouillon'
  }
  return texts[status] || status
}


// Actions
const editProduct = () => {
  router.push(`/supplier/products/edit/${product.value.id}`)
}

const deleteProduct = async () => {
  if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
    try {
      await api.delete(`/api/products/${product.value.id}`)
      router.push('/supplier/products')
    } catch (error) {
      console.error('Erreur lors de la suppression:', error)
    }
  }
}

// Fetch product data
const fetchProduct = async () => {
  loading.value = true
  error.value = null
  
  try {
    const productId = route.params.id
    console.log('🔄 Récupération du produit:', productId)
    
    // Charger les données du produit
    const response = await api.get(`/api/products/${productId}`)
    product.value = response.data
    
    console.log('📦 Produit chargé:', product.value)
    
    // Charger les images du produit séparément
    await fetchProductImages(productId)
    
  } catch (err) {
    console.error('❌ Erreur lors du chargement du produit:', err)
    error.value = err.response?.data?.message || 'Erreur lors du chargement du produit'
  } finally {
    loading.value = false
  }
}

// Fetch product images
const fetchProductImages = async (productId) => {
  try {
    console.log('🖼️ Récupération des images du produit:', productId)
    const response = await getProductImages(productId)
    
    // Associer les images au produit
    if (product.value) {
      product.value.images = response.data || response || []
      console.log('🖼️ Images associées au produit:', product.value.images)
      console.log('📊 Nombre d\'images:', product.value.images.length)
      
      if (product.value.images.length === 0) {
        console.log('⚠️ Aucune image trouvée pour ce produit')
      }
    }
  } catch (err) {
    console.error('❌ Erreur lors du chargement des images:', err)
    // En cas d'erreur, initialiser un tableau vide
    if (product.value) {
      product.value.images = []
    }
  }
}

// Fonctions pour l'upload d'images
const triggerImageUpload = () => {
  fileInput.value?.click()
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // Vérifier le type de fichier
  if (!file.type.startsWith('image/')) {
    alert('Veuillez sélectionner un fichier image')
    return
  }
  
  // Vérifier la taille (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('La taille du fichier ne doit pas dépasser 5MB')
    return
  }
  
  await uploadImage(file)
}

const uploadImage = async (file) => {
  uploading.value = true
  
  try {
    console.log('📸 Upload de l\'image:', file.name)
    
    const productId = route.params.id
    const response = await addProductImage(productId, file)
    
    console.log('✅ Image uploadée avec succès:', response)
    
    // Recharger les images du produit
    await fetchProductImages(productId)
    
    // Afficher un message de succès
    alert('Image ajoutée avec succès !')
    
  } catch (error) {
    console.error('❌ Erreur lors de l\'upload:', error)
    alert('Erreur lors de l\'ajout de l\'image. Veuillez réessayer.')
  } finally {
    uploading.value = false
    // Réinitialiser l'input file
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// Fonction pour supprimer une image
const removeImage = async (imageId) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette image ?')) {
    return
  }
  
  try {
    console.log('🗑️ Suppression de l\'image:', imageId)
    
    const productId = route.params.id
    await deleteProductImage(productId, imageId)
    
    console.log('✅ Image supprimée avec succès')
    
    // Recharger les images du produit
    await fetchProductImages(productId)
    
    // Afficher un message de succès
    alert('Image supprimée avec succès !')
    
  } catch (error) {
    console.error('❌ Erreur lors de la suppression:', error)
    alert('Erreur lors de la suppression de l\'image. Veuillez réessayer.')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
}
</style>
