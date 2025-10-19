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
    >
      <!-- Badges pour produits spéciaux -->
      <div class="product-badges">
        <v-chip 
          v-if="product.isOrganic" 
          color="green" 
          size="small" 
          class="ma-2"
        >
          🌱 Bio
        </v-chip>
        <v-chip 
          v-if="product.isLocal" 
          color="orange" 
          size="small" 
          class="ma-2"
        >
          🏠 Local
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
        color="primary" 
        variant="flat" 
        block
        :disabled="!canAddToCart"
        @click.stop="addToCart"
      >
        <v-icon left>mdi-cart-plus</v-icon>
        {{ canAddToCart ? 'Ajouter au panier' : 'Indisponible' }}
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)

// Image par défaut si pas d'image
const productImage = computed(() => {
  if (props.product.images && props.product.images.length > 0) {
    return props.product.images[0]
  }
  
  // Image par défaut selon le type de produit
  if (props.product.category?.type === 'ANIMAL') {
    return 'https://images.unsplash.com/photo-1548550023-8bdb78b265c3?w=400&h=300&fit=crop'
  } else {
    return 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop'
  }
})

// Gestion du stock
const canAddToCart = computed(() => {
  return props.product.quantity > 0 && props.product.isActive?.()
})

const stockColor = computed(() => {
  if (props.product.quantity === 0) return 'red'
  if (props.product.quantity < 10) return 'orange'
  return 'green'
})

const stockText = computed(() => {
  if (props.product.quantity === 0) return 'Rupture de stock'
  if (props.product.quantity < 10) return `Plus que ${props.product.quantity} en stock`
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

// Ajouter au panier
const addToCart = async () => {
  if (!canAddToCart.value) return
  
  loading.value = true
  try {
    await cartStore.addToCart({
      productId: props.product.id,
      quantity: 1,
      price: props.product.sellingPrice
    })
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
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