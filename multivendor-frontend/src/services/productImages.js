import api from './api'

/**
 * Service pour gérer les images des produits
 */

/**
 * Récupère toutes les images d'un produit
 * @param {number} productId - ID du produit
 * @returns {Promise} - Images du produit
 */
export const getProductImages = async (productId) => {
  try {
    console.log('🔍 Appel API pour récupérer les images du produit:', productId)
    const response = await api.get(`/api/products/${productId}/images`)
    console.log('🔍 Réponse API images:', response.data)
    return response.data
  } catch (error) {
    console.error('Erreur lors de la récupération des images du produit:', error)
    throw error
  }
}

/**
 * Ajoute une nouvelle image à un produit
 * @param {number} productId - ID du produit
 * @param {File} imageFile - Fichier image
 * @returns {Promise} - Image ajoutée
 */
export const addProductImage = async (productId, imageFile) => {
  try {
    const formData = new FormData()
    formData.append('image', imageFile)
    
    const response = await api.post(`/api/products/${productId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  } catch (error) {
    console.error('Erreur lors de l\'ajout de l\'image:', error)
    throw error
  }
}

/**
 * Supprime une image d'un produit
 * @param {number} productId - ID du produit
 * @param {number} imageId - ID de l'image
 * @returns {Promise} - Confirmation de suppression
 */
export const deleteProductImage = async (productId, imageId) => {
  try {
    const response = await api.delete(`/api/products/${productId}/images/${imageId}`)
    return response.data
  } catch (error) {
    console.error('Erreur lors de la suppression de l\'image:', error)
    throw error
  }
}

/**
 * Met à jour une image d'un produit
 * @param {number} productId - ID du produit
 * @param {number} imageId - ID de l'image
 * @param {FormData} imageData - Nouvelles données de l'image
 * @returns {Promise} - Image mise à jour
 */
export const updateProductImage = async (productId, imageId, imageData) => {
  try {
    const response = await api.put(`/api/products/${productId}/images/${imageId}`, imageData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  } catch (error) {
    console.error('Erreur lors de la mise à jour de l\'image:', error)
    throw error
  }
}
