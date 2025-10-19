<template>
  <v-container v-if="product">
    <v-row>
      <v-col cols="12" md="6">
        <v-img
          :src="product.images?.[0] || '/placeholder.jpg'"
          height="400"
          cover
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'
import { useCartStore } from '@/stores/cart'

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
