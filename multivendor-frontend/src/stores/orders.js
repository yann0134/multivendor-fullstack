import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

export const useOrderStore = defineStore('orders', () => {
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)

  // Créer une commande (utilise maintenant l'endpoint warehouse)
  const createOrder = async (shippingAddress, paymentMethod) => {
    // Vérifier si une commande est déjà en cours de création
    if (loading.value) {
      console.warn('⚠️ Une commande est déjà en cours de création')
      throw new Error('Une commande est déjà en cours de création. Veuillez patienter.')
    }
    
    loading.value = true
    try {
      const authStore = useAuthStore()
      
      // Vérifier que l'utilisateur est connecté
      if (!authStore.user || !authStore.user.id) {
        throw new Error('Utilisateur non connecté. Veuillez vous connecter pour créer une commande.')
      }
      
      const userId = authStore.user.id // Utiliser l'ID de l'utilisateur connecté
      
      console.log('🛒 Création de commande pour l\'utilisateur:', userId, authStore.user.fullName)
      console.log('📧 Email utilisateur:', authStore.user.email)
      console.log('🆔 ID utilisateur détaillé:', {
        id: authStore.user.id,
        fullName: authStore.user.fullName,
        email: authStore.user.email
      })
      
      // Utiliser le nouvel endpoint warehouse au lieu de l'ancien
      const response = await api.post('/api/warehouse/orders', {
        userId: userId,
        shippingAddress: shippingAddress
      })
      
      console.log('✅ Commande créée avec succès:', response.data)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la commande:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Créer une commande pour l'entrepôt (nouvel endpoint sans paiement)
  const createWarehouseOrder = async (userId, shippingAddress) => {
    loading.value = true
    try {
      // Vérifier que l'userId est fourni
      if (!userId) {
        throw new Error('ID utilisateur requis pour créer une commande')
      }
      
      console.log('🏭 Création de commande entrepôt pour l\'utilisateur:', userId)
      
      const response = await api.post('/api/warehouse/orders', {
        userId,
        shippingAddress
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la commande entrepôt:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Récupérer l'historique des commandes utilisateur
  const fetchUserOrders = async () => {
    try {
      const response = await api.get('/api/orders/user')
      orders.value = response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des commandes:', error)
    }
  }

  // Récupérer les commandes d'un vendeur
  const fetchSellerOrders = async () => {
    try {
      const response = await api.get('/seller/orders')
      orders.value = response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des commandes vendeur:', error)
    }
  }

  // Récupérer une commande par ID
  const fetchOrderById = async (orderId) => {
    try {
      const response = await api.get(`/api/orders/${orderId}`)
      currentOrder.value = response.data
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération de la commande:', error)
      throw error
    }
  }

  // Récupérer un article de commande par ID
  const fetchOrderItemById = async (orderItemId) => {
    try {
      const response = await api.get(`/api/orders/item/${orderItemId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération de l\'article:', error)
      throw error
    }
  }

  // Annuler une commande
  const cancelOrder = async (orderId) => {
    try {
      const response = await api.put(`/api/orders/${orderId}/cancel`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'annulation:', error)
      throw error
    }
  }

  // Mettre à jour le statut d'une commande (vendeur)
  const updateOrderStatus = async (orderId, status) => {
    try {
      const response = await api.put(`/seller/orders/${orderId}/status`, { status })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du statut:', error)
      throw error
    }
  }

  // Mettre à jour le statut d'un article de commande (vendeur)
  const updateOrderItemStatus = async (orderItemId, status) => {
    try {
      const response = await api.put(`/seller/orders/item/${orderItemId}/status`, { status })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du statut de l\'article:', error)
      throw error
    }
  }

  // Supprimer une commande individuelle
  const deleteOrder = async (orderId) => {
    loading.value = true
    try {
      console.log('🗑️ Suppression de la commande:', orderId)
      const response = await api.delete(`/api/orders/${orderId}`)
      
      // Retirer la commande de la liste locale
      orders.value = orders.value.filter(order => order.id !== orderId)
      
      console.log('✅ Commande supprimée:', response.data.message)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la suppression de la commande:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Supprimer toutes les commandes de l'utilisateur
  const deleteAllOrders = async () => {
    loading.value = true
    try {
      console.log('🗑️ Suppression de toutes les commandes')
      const response = await api.delete('/api/orders/user/all')
      
      // Vider la liste locale
      orders.value = []
      
      console.log('✅ Toutes les commandes supprimées:', response.data.message)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la suppression des commandes:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    orders,
    currentOrder,
    loading,
    createOrder,
    createWarehouseOrder,
    fetchUserOrders,
    fetchSellerOrders,
    fetchOrderById,
    fetchOrderItemById,
    cancelOrder,
    updateOrderStatus,
    updateOrderItemStatus,
    deleteOrder,
    deleteAllOrders
  }
})
