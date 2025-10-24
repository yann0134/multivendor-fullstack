import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])
  const loading = ref(false)
  const cart = ref(null)

  const totalItems = computed(() => 
    cartItems.value.length // Nombre de produits différents, pas d'unités
  )

  const totalUnits = computed(() => 
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0) // Total des unités
  )

  const totalPrice = computed(() => 
    cartItems.value.reduce((sum, item) => {
      const unitPrice = item.product?.sellingPrice || 0
      return sum + (unitPrice * item.quantity)
    }, 0)
  )

  const totalMrpPrice = computed(() => 
    cartItems.value.reduce((sum, item) => {
      const unitMrpPrice = item.product?.mrpPrice || 0
      return sum + (unitMrpPrice * item.quantity)
    }, 0)
  )

  const discount = computed(() => 
    totalMrpPrice.value - totalPrice.value
  )

  // Récupérer le panier
  const fetchCart = async () => {
    try {
      // Vérifier si l'utilisateur est authentifié
      const token = localStorage.getItem('jwt_token')
      if (!token) {
        console.warn('Utilisateur non authentifié, panier vide')
        cartItems.value = []
        cart.value = null
        return
      }

      console.log('🛒 Récupération du panier...')
      const response = await api.get('/api/cart')
      cart.value = response.data
      cartItems.value = response.data.cartItems || []
      console.log('✅ Panier récupéré:', cartItems.value.length, 'articles')
    } catch (error) {
      console.error('Erreur lors de la récupération du panier:', error)
      if (error.response?.status === 401) {
        console.warn('Session expirée, panier vidé')
        cartItems.value = []
        cart.value = null
      }
    }
  }

  // Ajouter un produit au panier avec options spécifiques
  const addToCart = async (product, quantity = 1, options = {}) => {
    console.log('addToCart called with:', product, quantity, options)
    
    // Vérifier si l'utilisateur est authentifié
    const token = localStorage.getItem('jwt_token')
    if (!token || token === 'null' || token.trim() === '') {
      // L'utilisateur n'est pas authentifié, retourner une erreur spéciale
      throw new Error('AUTHENTICATION_REQUIRED')
    }
    
    loading.value = true
    try {
      // Préparer les données selon l'API backend
      const cartData = {
        productId: product.id,
        quantity,
        size: options.packaging || product.unit || 'kg'
      }

      console.log('🛒 Ajout au panier:', cartData)
      console.log('🔑 Token JWT:', token ? 'Présent' : 'Absent')
      console.log('🌐 URL complète:', `${api.defaults.baseURL}/api/cart/add`)
      console.log('📡 Méthode HTTP: PUT')

      // Le backend utilise PUT /api/cart/add (avec le préfixe /api)
      // Ajouter un timestamp pour éviter le cache
      const timestamp = Date.now()
      const response = await api.put(`/api/cart/add?t=${timestamp}`, cartData)
      
      await fetchCart()
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'ajout au panier:', error)
      if (error.response?.status === 401) {
        throw new Error('Session expirée. Veuillez vous reconnecter.')
      } else if (error.response?.status === 404) {
        throw new Error('Service panier non disponible.')
      }
      throw error
    } finally {
      loading.value = false
    }
  }

  // Debounce pour éviter les appels API trop fréquents
  let updateTimeout = null

  // Mettre à jour la quantité
  const updateQuantity = async (itemId, quantity) => {
    try {
      // Vérifier si la quantité a vraiment changé
      const currentItem = cartItems.value.find(item => item.id === itemId)
      if (currentItem && currentItem.quantity === quantity) {
        return // Pas de changement, pas besoin de mettre à jour
      }
      
      console.log('🔄 Mise à jour quantité:', itemId, quantity)
      
      // Mise à jour optimiste immédiate de l'interface (position stable)
      const itemIndex = cartItems.value.findIndex(item => item.id === itemId)
      if (itemIndex !== -1) {
        cartItems.value[itemIndex].quantity = quantity
        // Recalculer les prix localement
        const item = cartItems.value[itemIndex]
        item.sellingPrice = quantity * (item.product?.sellingPrice || 0)
        item.mrpPrice = quantity * (item.product?.mrpPrice || 0)
      }
      
      // Annuler le timeout précédent
      if (updateTimeout) {
        clearTimeout(updateTimeout)
      }
      
      // Mise à jour sur le serveur avec debounce (500ms)
      updateTimeout = setTimeout(() => {
        api.put(`/api/cart/item/${itemId}`, { quantity }).catch(error => {
          console.error('Erreur serveur (non bloquante):', error)
          // En cas d'erreur, recharger pour restaurer l'état
          fetchCart()
        })
      }, 500)
      
      console.log('✅ Quantité mise à jour - position stable maintenue')
    } catch (error) {
      console.error('Erreur lors de la mise à jour:', error)
      throw error
    }
  }

  // Supprimer un article
  const removeItem = async (itemId) => {
    try {
      console.log('🗑️ Suppression article:', itemId)
      
      // Suppression optimiste immédiate de l'interface (position stable)
      const itemIndex = cartItems.value.findIndex(item => item.id === itemId)
      if (itemIndex !== -1) {
        cartItems.value.splice(itemIndex, 1)
      }
      
      // Suppression sur le serveur
      await api.delete(`/api/cart/item/${itemId}`)
      
      console.log('✅ Article supprimé sans rechargement du panier')
    } catch (error) {
      console.error('Erreur lors de la suppression:', error)
      // En cas d'erreur, recharger le panier pour restaurer l'état
      await fetchCart()
      throw error
    }
  }

  // Vider le panier
  const clearCart = async () => {
    try {
      console.log('🧹 Vidage du panier')
      await api.delete('/api/cart')
      cartItems.value = []
      cart.value = null
    } catch (error) {
      console.error('Erreur lors du vidage du panier:', error)
      throw error
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
    totalUnits,
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
