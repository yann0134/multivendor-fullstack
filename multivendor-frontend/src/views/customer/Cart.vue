<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mon Panier</h1>
      </v-col>
    </v-row>

    <v-row v-if="cartStore.cartItems.length === 0">
      <v-col cols="12" class="text-center">
        <v-icon size="120" color="grey">mdi-cart-outline</v-icon>
        <h2 class="text-h5 mb-4">Votre panier est vide</h2>
        <p class="text-grey mb-6">Découvrez nos produits et ajoutez-les à votre panier</p>
        <v-btn color="primary" to="/customer/products" size="large">
          Voir les produits
        </v-btn>
      </v-col>
    </v-row>

    <v-row v-else>
      <!-- Liste des articles -->
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title>Articles dans votre panier</v-card-title>
          <v-list>
            <v-list-item
              v-for="item in cartStore.cartItems"
              :key="item.id"
              class="cart-item"
            >
              <template v-slot:prepend>
                <v-img
                  :src="item.product.images?.[0] || '/placeholder.jpg'"
                  width="80"
                  height="80"
                  cover
                />
              </template>
              
              <v-list-item-title>{{ item.product.title }}</v-list-item-title>
              <v-list-item-subtitle>
                Taille: {{ item.size }} | Couleur: {{ item.color || 'Non spécifiée' }}
              </v-list-item-subtitle>
              
              <template v-slot:append>
                <div class="d-flex align-center">
                  <v-btn
                    icon
                    size="small"
                    @click="updateQuantity(item.id, item.quantity - 1)"
                    :disabled="item.quantity <= 1"
                  >
                    <v-icon>mdi-minus</v-icon>
                  </v-btn>
                  
                  <span class="mx-4">{{ item.quantity }}</span>
                  
                  <v-btn
                    icon
                    size="small"
                    @click="updateQuantity(item.id, item.quantity + 1)"
                    :disabled="item.quantity >= item.product.quantity"
                  >
                    <v-icon>mdi-plus</v-icon>
                  </v-btn>
                  
                  <div class="ml-4 text-right">
                    <div class="text-h6">{{ (item.sellingPrice * item.quantity).toFixed(2) }}€</div>
                    <div v-if="item.mrpPrice > item.sellingPrice" class="text-decoration-line-through text-grey">
                      {{ (item.mrpPrice * item.quantity).toFixed(2) }}€
                    </div>
                  </div>
                  
                  <v-btn
                    icon
                    color="error"
                    @click="removeItem(item.id)"
                    class="ml-2"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </div>
              </template>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
      
      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Résumé de la commande</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Sous-total ({{ cartStore.totalItems }} articles)</span>
              <span>{{ cartStore.totalPrice.toFixed(2) }}€</span>
            </div>
            
            <div v-if="cartStore.discount > 0" class="d-flex justify-space-between mb-2 text-success">
              <span>Remise</span>
              <span>-{{ cartStore.discount.toFixed(2) }}€</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Livraison</span>
              <span>Gratuite</span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between text-h6">
              <span>Total</span>
              <span>{{ cartStore.totalPrice.toFixed(2) }}€</span>
            </div>
          </v-card-text>
          
          <v-card-actions>
            <v-btn
              color="primary"
              block
              size="large"
              to="/customer/checkout"
            >
              Passer la commande
            </v-btn>
          </v-card-actions>
        </v-card>
        
        <!-- Code promo -->
        <v-card class="mt-4">
          <v-card-title>Code promo</v-card-title>
          <v-card-text>
            <v-text-field
              v-model="couponCode"
              label="Code promo"
              variant="outlined"
              density="compact"
              @keyup.enter="applyCoupon"
            />
            <v-btn
              color="primary"
              variant="outlined"
              block
              @click="applyCoupon"
              :loading="couponLoading"
            >
              Appliquer
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()
const couponCode = ref('')
const couponLoading = ref(false)

const updateQuantity = async (itemId, newQuantity) => {
  if (newQuantity > 0) {
    await cartStore.updateQuantity(itemId, newQuantity)
  }
}

const removeItem = async (itemId) => {
  await cartStore.removeItem(itemId)
}

const applyCoupon = async () => {
  if (!couponCode.value) return
  
  couponLoading.value = true
  try {
    await cartStore.applyCoupon(couponCode.value)
    couponCode.value = ''
  } catch (error) {
    console.error('Erreur lors de l\'application du coupon:', error)
  } finally {
    couponLoading.value = false
  }
}

onMounted(async () => {
  await cartStore.fetchCart()
})
</script>

<style scoped>
.cart-item {
  border-bottom: 1px solid #e0e0e0;
}

.cart-item:last-child {
  border-bottom: none;
}
</style>
