import api from './api'

/**
 * Service pour la gestion des commandes d'entrepôt
 */
export const warehouseOrdersService = {
  /**
   * Récupérer toutes les commandes pour l'entrepôt
   */
  async getAllOrders() {
    try {
      const response = await api.get('/api/warehouse/orders')
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des commandes:', error)
      throw error
    }
  },

  /**
   * Récupérer les commandes par statut
   */
  async getOrdersByStatus(status) {
    try {
      const response = await api.get(`/api/warehouse/orders?status=${status}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des commandes par statut:', error)
      throw error
    }
  },

  /**
   * Récupérer les commandes par client
   */
  async getOrdersByCustomer(customerId) {
    try {
      const response = await api.get(`/api/warehouse/orders/customer/${customerId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des commandes du client:', error)
      throw error
    }
  },

  /**
   * Mettre à jour le statut d'une commande
   */
  async updateOrderStatus(orderId, status) {
    try {
      const response = await api.put(`/api/warehouse/orders/${orderId}/status`, {
        status: status
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du statut:', error)
      throw error
    }
  },

  /**
   * Assigner un livreur à une commande
   */
  async assignDeliveryPerson(orderId, deliveryPersonId, notes = '') {
    try {
      const response = await api.put(`/api/warehouse/orders/${orderId}/assign-delivery`, {
        deliveryPersonId: deliveryPersonId,
        deliveryNotes: notes
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'assignation du livreur:', error)
      throw error
    }
  },

  /**
   * Récupérer les détails d'une commande
   */
  async getOrderDetails(orderId) {
    try {
      const response = await api.get(`/api/warehouse/orders/${orderId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des détails de la commande:', error)
      throw error
    }
  },

  /**
   * Récupérer les statistiques des commandes
   */
  async getOrderStats() {
    try {
      const response = await api.get('/api/warehouse/orders/stats')
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des statistiques:', error)
      throw error
    }
  },

  /**
   * Rechercher des commandes
   */
  async searchOrders(query) {
    try {
      const response = await api.get(`/api/warehouse/orders/search?q=${encodeURIComponent(query)}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la recherche:', error)
      throw error
    }
  },

  /**
   * Marquer une commande comme prête
   */
  async markOrderAsReady(orderId) {
    try {
      const response = await api.put(`/api/warehouse/orders/${orderId}/ready`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du marquage de la commande comme prête:', error)
      throw error
    }
  }
}

/**
 * Service pour la gestion des livreurs
 */
export const deliveryService = {
  /**
   * Récupérer tous les livreurs disponibles
   */
  async getAvailableDeliveryPersons() {
    try {
      const response = await api.get('/api/warehouse/delivery-persons')
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des livreurs:', error)
      throw error
    }
  },

  /**
   * Récupérer tous les livreurs
   */
  async getAllDeliveryPersons() {
    try {
      const response = await api.get('/api/delivery/person')
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des livreurs:', error)
      throw error
    }
  },

  /**
   * Récupérer un livreur par ID
   */
  async getDeliveryPersonById(id) {
    try {
      const response = await api.get(`/api/delivery/person/${id}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement du livreur:', error)
      throw error
    }
  },

  /**
   * Créer un nouveau livreur
   */
  async createDeliveryPerson(deliveryPersonData) {
    try {
      const response = await api.post('/api/delivery/person/create', deliveryPersonData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création du livreur:', error)
      throw error
    }
  },

  /**
   * Mettre à jour un livreur
   */
  async updateDeliveryPerson(id, deliveryPersonData) {
    try {
      const response = await api.put(`/api/delivery/person/${id}`, deliveryPersonData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du livreur:', error)
      throw error
    }
  },

  /**
   * Supprimer un livreur
   */
  async deleteDeliveryPerson(id) {
    try {
      const response = await api.delete(`/api/delivery/person/${id}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la suppression du livreur:', error)
      throw error
    }
  }
}

export default {
  warehouseOrdersService,
  deliveryService
}
