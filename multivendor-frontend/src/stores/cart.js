import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])
  const loading = ref(false)
  const cart = ref(null)

  const totalItems = computed(() => 
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  )

  const totalPrice = computed(() => 
    cartItems.value.reduce((sum, item) => sum + (item.sellingPrice * item.quantity), 0)
  )

  const totalMrpPrice = computed(() => 
    cartItems.value.reduce((sum, item) => sum + (item.mrpPrice * item.quantity), 0)
  )

  const discount = computed(() => 
    totalMrpPrice.value - totalPrice.value
  )

  // Récupérer le panier
  const fetchCart = async () => {
    try {
      const response = await api.get('/cart')
      cart.value = response.data
      cartItems.value = response.data.cartItems || []
    } catch (error) {
      console.error('Erreur lors de la récupération du panier:', error)
    }
  }

  // Ajouter un produit au panier
  const addToCart = async (product, quantity = 1, size = 'M') => {
    loading.value = true
    try {
      const response = await api.post('/cart/add', {
        productId: product.id,
        quantity,
        size
      })
      
      await fetchCart()
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'ajout au panier:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Mettre à jour la quantité
  const updateQuantity = async (itemId, quantity) => {
    try {
      await api.put(`/cart/item/${itemId}`, { quantity })
      await fetchCart()
    } catch (error) {
      console.error('Erreur lors de la mise à jour:', error)
    }
  }

  // Supprimer un article
  const removeItem = async (itemId) => {
    try {
      await api.delete(`/cart/item/${itemId}`)
      await fetchCart()
    } catch (error) {
      console.error('Erreur lors de la suppression:', error)
    }
  }

  // Vider le panier
  const clearCart = async () => {
    try {
      await api.delete('/cart')
      cartItems.value = []
      cart.value = null
    } catch (error) {
      console.error('Erreur lors du vidage du panier:', error)
    }
  }

  // Appliquer un coupon
  const applyCoupon = async (couponCode) => {
    try {
      const response = await api.post('/cart/coupon', { couponCode })
      await fetchCart()
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'application du coupon:', error)
      throw error
    }
  }

  return {
    cartItems,
    cart,
    loading,
    totalItems,
    totalPrice,
    totalMrpPrice,
    discount,
    fetchCart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart,
    applyCoupon
  }
})
