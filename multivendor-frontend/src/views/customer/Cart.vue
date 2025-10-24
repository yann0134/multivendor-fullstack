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
          <v-card-text>
            <CartItem
              v-for="item in cartStore.cartItems"
              :key="`cart-item-${item.id}`"
              :item="item"
              @update-quantity="(newQuantity) => updateQuantity(item.id, newQuantity)"
              @remove-item="() => removeItem(item.id)"
            />
          </v-card-text>
        </v-card>
      </v-col>
      
      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <CartSummary
          :total-items="cartStore.totalItems"
          :total-units="cartStore.totalUnits"
          :total-price="cartStore.totalPrice"
        />
        
        <v-card class="mt-4">
          <v-card-actions>
            <v-btn
              color="primary"
              block
              size="large"
              to="/customer/checkout"
            >
              <v-icon left>mdi-credit-card</v-icon>
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
import CartItem from '@/components/customer/CartItem.vue'
import CartSummary from '@/components/customer/CartSummary.vue'

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

// Fonction de formatage des quantités
const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
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
