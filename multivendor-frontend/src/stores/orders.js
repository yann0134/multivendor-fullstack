import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export const useOrderStore = defineStore('orders', () => {
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)

  // Créer une commande
  const createOrder = async (shippingAddress, paymentMethod) => {
    loading.value = true
    try {
      const response = await api.post('/api/orders', shippingAddress, {
        params: { paymentMethod }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la commande:', error)
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

  return {
    orders,
    currentOrder,
    loading,
    createOrder,
    fetchUserOrders,
    fetchSellerOrders,
    fetchOrderById,
    fetchOrderItemById,
    cancelOrder,
    updateOrderStatus,
    updateOrderItemStatus
  }
})
