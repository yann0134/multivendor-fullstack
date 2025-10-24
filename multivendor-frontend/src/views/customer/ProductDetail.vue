<template>
  <v-container v-if="product">
    <!-- Modal d'authentification -->
    <AuthModal ref="authModal" />
    <v-row>
      <v-col cols="12" md="6">
        <!-- Galerie d'images du produit -->
        <ProductImageGallery
          :images="productImages"
          :product-name="product.title"
        />
        
        <!-- Debug: Afficher le nombre d'images -->
        <div v-if="productImages.length > 0" class="text-caption text-grey">
          {{ productImages.length }} image(s) chargée(s)
        </div>
      </v-col>
      
      <v-col cols="12" md="6">
        <h1 class="text-h4 mb-4">{{ product.title }}</h1>
        
        <div class="d-flex align-center mb-4">
          <v-rating
            :model-value="product.numRatings || 0"
            color="amber"
            density="compact"
            size="small"
            readonly
          />
          <span class="text-caption ml-2">({{ product.numRatings || 0 }} avis)</span>
        </div>
        
        <div class="d-flex align-center mb-4">
          <span class="text-h4 text-primary mr-4 font-weight-bold">
            {{ formatPrice(product.sellingPrice) }}
          </span>
          <span v-if="product.mrpPrice > product.sellingPrice" class="text-decoration-line-through text-grey text-h6">
            {{ formatPrice(product.mrpPrice) }}
          </span>
          <v-chip v-if="product.discountPercent > 0" color="success" class="ml-2" size="small">
            <v-icon left>mdi-percent</v-icon>
            -{{ product.discountPercent }}%
          </v-chip>
        </div>
        
        <p class="text-body-1 mb-4">{{ product.description }}</p>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Variété</h3>
          <v-chip-group v-model="selectedVariety">
            <v-chip
              v-for="variety in varieties"
              :key="variety"
              :value="variety"
            >
              {{ variety }}
            </v-chip>
          </v-chip-group>
        </div>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Conditionnement</h3>
          <v-chip-group v-model="selectedPackaging">
            <v-chip
              v-for="packaging in packagingOptions"
              :key="packaging"
              :value="packaging"
            >
              {{ packaging }}
            </v-chip>
          </v-chip-group>
        </div>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Quantité</h3>
          <v-text-field
            v-model.number="quantity"
            type="number"
            min="1"
            :max="product.supplierAvailableQuantity || product.quantity"
            variant="outlined"
            style="max-width: 120px"
            suffix="unité(s)"
            :rules="[v => v > 0 || 'Quantité requise']"
          />
        </div>
        
        <!-- Informations de stock -->
        <!--<div class="mb-4">
          <h3 class="text-h6 mb-2">📦 Disponibilité</h3>
          <v-alert 
            v-if="product.supplierAvailableQuantity > 0"
            type="success" 
            variant="tonal"
            class="mb-2"
          >
            <v-icon class="mr-2">mdi-check-circle</v-icon>
            <strong>{{ product.supplierAvailableQuantity }} unités disponibles</strong>
          </v-alert>
          
          <v-alert 
            v-else
            type="warning" 
            variant="tonal"
            class="mb-2"
          >
            <v-icon class="mr-2">mdi-alert-circle</v-icon>
            <strong>Rupture de stock</strong> - Ce produit n'est plus disponible
          </v-alert>
          
          <div v-if="product.adminRequestedQuantity > 0" class="mt-2">
            <v-chip color="info" size="small">
              <v-icon left>mdi-information</v-icon>
              Demande admin: {{ product.adminRequestedQuantity }} unités
            </v-chip>
          </div>
        </div>-->
        
        <div class="d-flex gap-2">
          <v-btn
            color="primary"
            size="large"
            @click="handleAddToCart"
            :loading="loading"
            :disabled="!selectedVariety || !selectedPackaging"
          >
            <v-icon left>mdi-cart-plus</v-icon>
            Ajouter au panier
          </v-btn>
          
          <v-btn
            color="success"
            variant="outlined"
            size="large"
            @click="buyNow"
            :loading="loading"
            :disabled="!selectedVariety || !selectedPackaging"
          >
            Acheter maintenant
          </v-btn>
        </div>
      </v-col>
    </v-row>
    

     <!-- Caractéristiques du produit -->
     <v-row class="mt-8">
      <v-col cols="12">
      
        <v-card>
          <v-card-text>
            <v-row>
              <!-- Informations générales du produit -->
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">🌱 Informations Agronomiques</h3>
                <v-list density="compact">
                  <v-list-item v-if="product.origin">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-map-marker</v-icon>
                      Origine
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.origin }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.season">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-calendar</v-icon>
                      Saison de récolte
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.season }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.variety">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-seed</v-icon>
                      Variété
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.variety }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.growingMethod">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-sprout</v-icon>
                      Méthode de culture
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.growingMethod }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.nutritionalInfo">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-food-apple</v-icon>
                      Valeurs nutritionnelles
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.nutritionalInfo }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.storageConditions">
                    <v-list-item-title>
                      <v-icon class="mr-2">mdi-thermometer</v-icon>
                      Conditions de stockage
                    </v-list-item-title>
                    <v-list-item-subtitle>{{ product.storageConditions }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
              
              <!-- Informations de stock -->
              <!--<v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">📦 Informations de Stock</h3>
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>Quantité disponible</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip 
                        :color="product.supplierAvailableQuantity > 0 ? 'success' : 'error'"
                        size="small"
                      >
                        <v-icon left>mdi-package-variant</v-icon>
                        {{ formatQuantity(product.supplierAvailableQuantity || 0) }} unités
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.adminRequestedQuantity > 0">
                    <v-list-item-title>Demande administrateur</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="info" size="small">
                        <v-icon left>mdi-account-cog</v-icon>
                        {{ formatQuantity(product.adminRequestedQuantity) }} unités
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.stockNegotiationPending">
                    <v-list-item-title>Statut de négociation</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="orange" size="small">
                        <v-icon left>mdi-clock</v-icon>
                        En cours
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>-->
            </v-row>
            
            <!-- Informations de prix -->
            <!--<v-row class="mt-4">
              <v-col cols="12">
                <h3 class="text-h6 mb-3">💰 Informations de Prix</h3>
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>Prix de vente</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="primary" size="small">
                        <v-icon left>mdi-currency-usd</v-icon>
                        {{ formatPrice(product.sellingPrice) }}
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.mrpPrice > product.sellingPrice">
                    <v-list-item-title>Prix MRP</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="grey" size="small" variant="outlined">
                        <v-icon left>mdi-tag</v-icon>
                        <span class="text-decoration-line-through">{{ formatPrice(product.mrpPrice) }}</span>
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.discountPercent > 0">
                    <v-list-item-title>Remise</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="success" size="small">
                        <v-icon left>mdi-percent</v-icon>
                        -{{ product.discountPercent }}%
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>-->
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    
    <!-- Avis et commentaires -->
    <v-row class="mt-8">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Avis clients</h2>
        <v-card>
          <v-card-text>
            <div v-if="reviews.length === 0" class="text-center pa-8">
              <v-icon size="48" color="grey">mdi-comment-outline</v-icon>
              <p class="text-grey">Aucun avis pour ce produit</p>
            </div>
            <div v-else>
              <v-list>
                <v-list-item
                  v-for="review in reviews"
                  :key="review.id"
                >
                  <template v-slot:prepend>
                    <v-avatar>
                      <v-icon>mdi-account</v-icon>
                    </v-avatar>
                  </template>
                  
                  <v-list-item-title>{{ review.user?.fullName || 'Anonyme' }}</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-rating
                      :model-value="review.rating"
                      color="amber"
                      density="compact"
                      size="small"
                      readonly
                    />
                    {{ review.comment }}
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    
  </v-container>
  
  <v-container v-else>
    <v-row justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" />
        <p class="mt-4">Chargement du produit...</p>
      </v-col>
    </v-row>
  </v-container>

  <!-- Notification Toast -->
  <NotificationToast
    v-model="showNotification"
    :message="notificationMessage"
    :type="notificationType"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'
import { useCartStore } from '@/stores/cart'
import { useReviewStore } from '@/stores/reviews'
import ProductImageGallery from '@/components/customer/ProductImageGallery.vue'
import NotificationToast from '@/components/common/NotificationToast.vue'
import AuthModal from '@/components/common/AuthModal.vue'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()
const reviewStore = useReviewStore()

// Référence au modal d'authentification
const authModal = ref(null)

const product = ref(null)
const productImages = ref([])
const loading = ref(false)
const quantity = ref(1)
const selectedVariety = ref('')
const selectedPackaging = ref('')
const reviews = computed(() => reviewStore.reviews)

// Notifications
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('info')

// Vérifier si l'utilisateur est authentifié
const isAuthenticated = computed(() => {
  const token = localStorage.getItem('jwt_token')
  return token && token !== 'null' && token.trim() !== ''
})

// Options dynamiques selon le type de produit
const varieties = computed(() => {
  if (!product.value) return []
  
  if (product.value.category?.type === 'ANIMAL') {
    return ['Standard', 'Premium', 'Bio', 'Élevage traditionnel']
  } else if (product.value.category?.type === 'VEGETAL') {
    return ['Standard', 'Bio', 'Local', 'Importé']
  }
  return ['Standard']
})

const packagingOptions = computed(() => {
  if (!product.value) return []
  
  const unit = product.value.unit || 'kg'
  if (unit === 'kg') {
    return ['1kg', '2kg', '5kg', '10kg']
  } else if (unit === 'piece') {
    return ['1 pièce', '5 pièces', '10 pièces', '20 pièces']
  } else if (unit === 'Sac') {
    return ['1 Sac', '2 Sacs', '5 Sacs', '10 Sacs']
  }
  return [unit]
})


const addToCart = async () => {
  loading.value = true
  try {
    const options = {
      variety: selectedVariety.value,
      packaging: selectedPackaging.value,
      weight: product.value.weight,
      storageConditions: product.value.storageConditions,
      organic: product.value.organic,
      fresh: product.value.fresh
    }
    
    await cartStore.addToCart(product.value, quantity.value, options)
    
    // Afficher notification de succès
    showNotification.value = true
    notificationMessage.value = 'Produit ajouté au panier avec succès !'
    notificationType.value = 'success'
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
    
    // Si l'erreur est liée à l'authentification, afficher le modal
    if (error.message === 'AUTHENTICATION_REQUIRED') {
      showAuthModal()
      return
    }
    
    // Afficher notification d'erreur
    showNotification.value = true
    notificationMessage.value = error.message || 'Erreur lors de l\'ajout au panier'
    notificationType.value = 'error'
  } finally {
    loading.value = false
  }
}

const buyNow = async () => {
  await addToCart()
  router.push('/customer/checkout')
}

// Gestion du clic sur le bouton "Ajouter au panier"
const handleAddToCart = () => {
  // Si l'utilisateur n'est pas authentifié, afficher le modal
  if (!isAuthenticated.value) {
    showAuthModal()
    return
  }
  
  // Sinon, procéder à l'ajout au panier
  addToCart()
}

// Méthode pour afficher le modal d'authentification
const showAuthModal = () => {
  if (authModal.value) {
    authModal.value.openModal()
  }
}

// Fonction de formatage des prix
const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price).replace('XOF', 'FCFA')
}

// Fonction de formatage du poids
const formatWeight = (weight) => {
  if (!weight) return '0'
  return new Intl.NumberFormat('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3
  }).format(weight)
}

// Fonction de formatage des quantités
const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
}

onMounted(async () => {
  const productId = route.params.id
  try {
    product.value = await productStore.fetchProductById(productId)
    console.log('📦 Produit chargé:', product.value)
    
    // Utiliser les images directement du produit (comme dans l'interface fournisseur)
    if (product.value && product.value.images) {
      // Construire des URLs complètes pour les images
      const imagesWithFullUrls = product.value.images.map(image => ({
        ...image,
        imageUrl: image.imageUrl.startsWith('http') 
          ? image.imageUrl 
          : `http://localhost:3026${image.imageUrl}`
      }))
      productImages.value = imagesWithFullUrls
      console.log('🖼️ Images du produit avec URLs complètes:', productImages.value)
    } else {
      console.log('⚠️ Aucune image trouvée dans le produit')
      productImages.value = []
    }
    
    // Charger les avis
    await reviewStore.fetchProductReviews(productId)
  } catch (error) {
    console.error('Erreur lors du chargement du produit:', error)
  }
})
</script>
