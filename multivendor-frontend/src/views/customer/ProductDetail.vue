<template>
  <v-container v-if="product">
    <v-row>
      <v-col cols="12" md="6">
        <!-- Galerie d'images du produit -->
        <ProductImageGallery
          :images="productImages"
          :product-name="product.title"
        />
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
          <span class="text-h4 text-primary mr-4">{{ product.sellingPrice }}€</span>
          <span v-if="product.mrpPrice > product.sellingPrice" class="text-decoration-line-through text-grey">
            {{ product.mrpPrice }}€
          </span>
          <v-chip v-if="product.discountPercent > 0" color="success" class="ml-2">
            -{{ product.discountPercent }}%
          </v-chip>
        </div>
        
        <p class="text-body-1 mb-4">{{ product.description }}</p>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Couleur</h3>
          <v-chip-group v-model="selectedColor">
            <v-chip
              v-for="color in colors"
              :key="color"
              :value="color"
            >
              {{ color }}
            </v-chip>
          </v-chip-group>
        </div>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Taille</h3>
          <v-chip-group v-model="selectedSize">
            <v-chip
              v-for="size in sizes"
              :key="size"
              :value="size"
            >
              {{ size }}
            </v-chip>
          </v-chip-group>
        </div>
        
        <div class="mb-4">
          <h3 class="text-h6 mb-2">Quantité</h3>
          <v-text-field
            v-model.number="quantity"
            type="number"
            min="1"
            :max="product.quantity"
            variant="outlined"
            style="max-width: 120px"
          />
        </div>
        
        <!-- Informations de stock -->
        <div class="mb-4">
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
        </div>
        
        <div class="d-flex gap-2">
          <v-btn
            color="primary"
            size="large"
            @click="addToCart"
            :loading="loading"
            :disabled="!selectedSize || !selectedColor"
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
            :disabled="!selectedSize || !selectedColor"
          >
            Acheter maintenant
          </v-btn>
        </div>
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
    
    <!-- Caractéristiques du produit -->
    <v-row class="mt-8">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Caractéristiques du produit</h2>
        <v-card>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">📦 Informations de Stock</h3>
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>Quantité disponible</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip 
                        :color="product.supplierAvailableQuantity > 0 ? 'success' : 'error'"
                        size="small"
                      >
                        {{ product.supplierAvailableQuantity || 0 }} unités
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.adminRequestedQuantity > 0">
                    <v-list-item-title>Demande administrateur</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip color="info" size="small">
                        {{ product.adminRequestedQuantity }} unités
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
              </v-col>
              
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">💰 Informations de Prix</h3>
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>Prix de vente</v-list-item-title>
                    <v-list-item-subtitle>{{ product.sellingPrice }}€</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.mrpPrice > product.sellingPrice">
                    <v-list-item-title>Prix MRP</v-list-item-title>
                    <v-list-item-subtitle class="text-decoration-line-through">{{ product.mrpPrice }}€</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item v-if="product.discountPercent > 0">
                    <v-list-item-title>Remise</v-list-item-title>
                    <v-list-item-subtitle>{{ product.discountPercent }}%</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
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
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'
import { useCartStore } from '@/stores/cart'
import ProductImageGallery from '@/components/customer/ProductImageGallery.vue'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()

const product = ref(null)
const loading = ref(false)
const quantity = ref(1)
const selectedColor = ref('')
const selectedSize = ref('')
const reviews = ref([])

const colors = ref(['Rouge', 'Bleu', 'Vert', 'Noir', 'Blanc'])
const sizes = ref(['S', 'M', 'L', 'XL'])

// Images du produit pour la galerie
const productImages = computed(() => {
  if (!product.value?.images) return []
  
  return product.value.images.map((image, index) => ({
    id: image.id || index,
    url: image.imageUrl || image.url || image,
    thumbnailUrl: image.thumbnailUrl || image.imageUrl || image.url || image,
    altText: image.altText || `${product.value.title} - Image ${index + 1}`,
    description: image.description || '',
    width: image.width || 800,
    height: image.height || 600,
    isMain: image.isMain || index === 0
  }))
})

const addToCart = async () => {
  loading.value = true
  try {
    await cartStore.addToCart(product.value, quantity.value, selectedSize.value)
    // Afficher notification de succès
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
  } finally {
    loading.value = false
  }
}

const buyNow = async () => {
  await addToCart()
  router.push('/customer/checkout')
}

onMounted(async () => {
  const productId = route.params.id
  try {
    product.value = await productStore.fetchProductById(productId)
    // Charger les avis
    reviews.value = product.value.reviews || []
  } catch (error) {
    console.error('Erreur lors du chargement du produit:', error)
  }
})
</script>
