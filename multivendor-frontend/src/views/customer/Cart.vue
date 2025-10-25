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
          <v-card-actions class="flex-column gap-2">
            <v-btn
              color="primary"
              block
              size="large"
              to="/customer/checkout"
            >
              <v-icon left>mdi-credit-card</v-icon>
              Passer la commande
            </v-btn>
            
            <v-btn
              color="error"
              variant="outlined"
              block
              size="large"
              @click="clearCart"
              :loading="clearing"
            >
              <v-icon left>mdi-delete-sweep</v-icon>
              Vider le panier
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

    <!-- Modal de confirmation pour vider le panier -->
    <v-dialog v-model="showClearConfirmDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5">
          <v-icon class="mr-2" color="warning">mdi-alert-circle</v-icon>
          Confirmer la suppression
        </v-card-title>
        
        <v-card-text>
          <p class="text-body-1 mb-4">
            Êtes-vous sûr de vouloir vider votre panier ?
          </p>
          <p class="text-body-2 text-grey">
            Cette action supprimera tous les articles de votre panier et ne peut pas être annulée.
          </p>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="showClearConfirmDialog = false"
            :disabled="clearing"
          >
            Annuler
          </v-btn>
          <v-btn
            color="error"
            @click="confirmClearCart"
            :loading="clearing"
          >
            <v-icon left>mdi-delete-sweep</v-icon>
            Vider le panier
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
const clearing = ref(false)
const showClearConfirmDialog = ref(false)

const updateQuantity = async (itemId, newQuantity) => {
  if (newQuantity > 0) {
    await cartStore.updateQuantity(itemId, newQuantity)
  }
}

const removeItem = async (itemId) => {
  await cartStore.removeItem(itemId)
}

const clearCart = () => {
  showClearConfirmDialog.value = true
}

const confirmClearCart = async () => {
  clearing.value = true
  try {
    await cartStore.clearCart()
    showClearConfirmDialog.value = false
  } catch (error) {
    console.error('Erreur lors du vidage du panier:', error)
    alert('Erreur lors du vidage du panier: ' + (error.response?.data?.message || error.message))
  } finally {
    clearing.value = false
  }
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
