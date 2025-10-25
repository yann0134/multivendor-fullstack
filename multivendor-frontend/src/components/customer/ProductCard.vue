<template>
  <v-card 
    class="product-card" 
    :loading="loading"
    @click="goToProduct"
  >
    <v-img
      :src="productImage"
      height="200"
      cover
      class="product-image"
      @error="handleImageError"
    >
      <template v-slot:error>
        <div class="d-flex align-center justify-center fill-height bg-grey-lighten-4">
          <div class="text-center">
            <v-icon size="48" color="grey">mdi-image-off</v-icon>
            <div class="text-caption text-grey mt-2">Image non disponible</div>
          </div>
        </div>
      </template>
      
      <!-- Badges pour produits spéciaux -->
      <div class="product-badges">
        <v-chip 
          v-if="product.organic" 
          color="green" 
          size="small" 
          class="ma-2"
        >
          🌱 Bio
        </v-chip>
        <v-chip 
          v-if="product.local" 
          color="orange" 
          size="small" 
          class="ma-2"
        >
          🏠 Local
        </v-chip>
        <v-chip 
          v-if="product.fresh" 
          color="blue" 
          size="small" 
          class="ma-2"
        >
          ❄️ Frais
        </v-chip>
        <v-chip 
          v-if="product.discountPercent > 0" 
          color="red" 
          size="small" 
          class="ma-2"
        >
          -{{ product.discountPercent }}%
        </v-chip>
      </div>
    </v-img>

    <v-card-title class="text-h6 product-title">
      {{ product.title }}
    </v-card-title>

    <v-card-subtitle class="product-origin">
      📍 {{ product.origin }}
    </v-card-subtitle>

    <v-card-text>
      <!-- Description courte -->
      <p class="text-body-2 text-grey mb-2">
        {{ product.description?.substring(0, 80) }}...
      </p>

      <!-- Informations agricoles -->
      <div class="product-info mb-3">
        <div class="d-flex align-center mb-1">
          <v-icon size="16" class="mr-2">mdi-scale</v-icon>
          <span class="text-caption">{{ product.weight }} {{ product.unit }}</span>
        </div>
        
        <div class="d-flex align-center mb-1" v-if="product.farmingMethod">
          <v-icon size="16" class="mr-2">mdi-sprout</v-icon>
          <span class="text-caption">{{ product.farmingMethod }}</span>
        </div>
        
        <div class="d-flex align-center mb-1" v-if="product.season">
          <v-icon size="16" class="mr-2">mdi-calendar</v-icon>
          <span class="text-caption">{{ product.season }}</span>
        </div>
      </div>

      <!-- Prix -->
      <div class="price-section">
        <div class="d-flex align-center">
          <span class="text-h6 text-primary font-weight-bold">
            {{ formatPrice(product.sellingPrice) }}
          </span>
          <span 
            v-if="product.discountPercent > 0" 
            class="text-decoration-line-through text-grey ml-2"
          >
            {{ formatPrice(product.mrpPrice) }}
          </span>
        </div>
        <div class="text-caption text-grey">
          par {{ product.unit }}
        </div>
      </div>

      <!-- Stock -->
      <div class="stock-info mt-2">
        <v-chip 
          :color="stockColor" 
          size="small" 
          variant="tonal"
        >
          {{ stockText }}
        </v-chip>
      </div>
    </v-card-text>

    <v-card-actions>
      <v-btn 
        v-if="!isProductInCart"
        color="primary" 
        variant="flat" 
        block
        :disabled="!canAddToCart"
        @click.stop="handleAddToCart"
      >
        <v-icon left>mdi-cart-plus</v-icon>
        {{ canAddToCart ? 'Ajouter au panier' : 'Indisponible' }}
      </v-btn>
      
      <v-btn 
        v-else
        color="success" 
        variant="outlined" 
        block
        @click.stop="goToProduct"
      >
        <v-icon left>mdi-eye</v-icon>
        Voir le produit
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { getDefaultProductImage } from '@/utils/defaultImages'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['showAuthModal'])

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)

// Vérifier si le produit est dans le panier
const isProductInCart = computed(() => {
  return cartStore.cartItems.some(item => item.product.id === props.product.id)
})

// Image par défaut si pas d'image
const productImage = computed(() => {
  console.log('🖼️ ProductCard - Produit:', props.product.title)
  console.log('🖼️ ProductCard - Images:', props.product.images)
  
  if (props.product.images && props.product.images.length > 0) {
    const firstImage = props.product.images[0]
    console.log('🖼️ ProductCard - Première image:', firstImage)
    
    // Construire l'URL complète si nécessaire
    if (firstImage.imageUrl && !firstImage.imageUrl.startsWith('http')) {
      const fullUrl = `http://localhost:3026${firstImage.imageUrl}`
      console.log('🖼️ ProductCard - URL complète construite:', fullUrl)
      return fullUrl
    }
    const finalUrl = firstImage.imageUrl || firstImage.url || getDefaultProductImage(props.product)
    console.log('🖼️ ProductCard - URL finale:', finalUrl)
    return finalUrl
  }
  
  console.log('🖼️ ProductCard - Aucune image, utilisation de l\'image par défaut')
  // Utiliser la fonction configurée pour obtenir une image par défaut
  return getDefaultProductImage(props.product)
})

// Gestion du stock
const canAddToCart = computed(() => {
  // Utiliser supplierAvailableQuantity pour les produits reçus par l'entrepôt
  const availableQuantity = props.product.supplierAvailableQuantity || props.product.quantity || 0
  return availableQuantity > 0 && props.product.active
})

// Vérifier si l'utilisateur est authentifié
const isAuthenticated = computed(() => {
  const token = localStorage.getItem('jwt_token')
  return token && token !== 'null' && token.trim() !== ''
})

const stockColor = computed(() => {
  const availableQuantity = props.product.supplierAvailableQuantity || props.product.quantity || 0
  if (availableQuantity === 0) return 'red'
  if (availableQuantity < 10) return 'orange'
  return 'green'
})

const stockText = computed(() => {
  const availableQuantity = props.product.supplierAvailableQuantity || props.product.quantity || 0
  if (availableQuantity === 0) return 'Rupture de stock'
  if (availableQuantity < 10) return `Plus que ${availableQuantity} en stock`
  return 'En stock'
})

// Formatage du prix
const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

// Navigation vers le détail du produit
const goToProduct = () => {
  router.push(`/customer/product/${props.product.id}`)
}

const handleImageError = (event) => {
  console.error('❌ Erreur de chargement de l\'image du produit:', event.target.src)
  // L'image par défaut sera affichée via le template v-slot:error
}

// Gestion du clic sur le bouton "Ajouter au panier"
const handleAddToCart = () => {
  if (!canAddToCart.value) return
  
  // Si l'utilisateur n'est pas authentifié, afficher le modal
  if (!isAuthenticated.value) {
    emit('showAuthModal')
    return
  }
  
  // Sinon, procéder à l'ajout au panier
  addToCart()
}

// Ajouter au panier
const addToCart = async () => {
  if (!canAddToCart.value) return
  
  loading.value = true
  try {
    const options = {
      variety: 'Standard',
      packaging: props.product.unit || 'kg',
      weight: props.product.weight,
      storageConditions: props.product.storageConditions,
      organic: props.product.organic,
      fresh: props.product.fresh
    }
    
    await cartStore.addToCart(props.product, 1, options)
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
    
    // Si l'erreur est liée à l'authentification, émettre un événement
    if (error.message === 'AUTHENTICATION_REQUIRED') {
      emit('showAuthModal')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.product-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.product-image {
  position: relative;
}

.product-badges {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.product-title {
  line-height: 1.2;
  min-height: 2.4em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-origin {
  color: #666;
  font-size: 0.875rem;
}

.product-info {
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 8px;
}

.price-section {
  margin-top: 8px;
}

.stock-info {
  margin-top: 8px;
}

.v-card-actions {
  margin-top: auto;
  padding-top: 0;
}
</style>