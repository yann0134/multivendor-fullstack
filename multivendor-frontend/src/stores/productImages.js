import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export const useProductImageStore = defineStore('productImages', () => {
  const images = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Upload d'une image
  const uploadImage = async (productId, file, altText = '', description = '') => {
    loading.value = true
    error.value = null
    
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('altText', altText)
      formData.append('description', description)

      const response = await api.post(`/product-images/upload/${productId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'upload'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Upload de plusieurs images
  const uploadMultipleImages = async (productId, formData) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.post(`/product-images/upload-multiple/${productId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'upload multiple'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Obtenir les images d'un produit
  const getProductImages = async (productId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.get(`/product-images/product/${productId}`)
      images.value = response.data.images || []
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement des images'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Obtenir l'image principale d'un produit
  const getMainImage = async (productId) => {
    try {
      const response = await api.get(`/product-images/product/${productId}/main`)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement de l\'image principale'
      throw err
    }
  }

  // Définir une image comme principale
  const setMainImage = async (imageId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.put(`/product-images/${imageId}/set-main`)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la définition de l\'image principale'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Réorganiser les images
  const reorderImages = async (productId, imageIds) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.put(`/product-images/reorder/${productId}`, imageIds)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la réorganisation'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Mettre à jour les métadonnées d'une image
  const updateImageMetadata = async (imageId, altText, description) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.put(`/product-images/${imageId}/metadata`, null, {
        params: { altText, description }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la mise à jour'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Supprimer une image
  const deleteImage = async (imageId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.delete(`/product-images/${imageId}`)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Supprimer toutes les images d'un produit
  const deleteAllImages = async (productId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await api.delete(`/product-images/product/${productId}/all`)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Obtenir l'URL complète d'une image
  const getImageUrl = (image) => {
    if (!image) return '/placeholder-image.jpg'
    return image.imageUrl || '/placeholder-image.jpg'
  }

  // Obtenir l'URL de la miniature
  const getThumbnailUrl = (image) => {
    if (!image) return '/placeholder-image.jpg'
    return image.thumbnailPath || image.imageUrl || '/placeholder-image.jpg'
  }

  // Valider un fichier image
  const validateImageFile = (file) => {
    if (!file) return false
    
    // Vérifier le type
    if (!file.type.startsWith('image/')) return false
    
    // Vérifier la taille (5MB max)
    if (file.size > 5 * 1024 * 1024) return false
    
    // Vérifier l'extension
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.webp']
    const extension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
    
    return allowedExtensions.includes(extension)
  }

  // Réinitialiser l'état
  const resetState = () => {
    images.value = []
    loading.value = false
    error.value = null
  }

  return {
    // État
    images,
    loading,
    error,
    
    // Actions
    uploadImage,
    uploadMultipleImages,
    getProductImages,
    getMainImage,
    setMainImage,
    reorderImages,
    updateImageMetadata,
    deleteImage,
    deleteAllImages,
    getImageUrl,
    getThumbnailUrl,
    validateImageFile,
    resetState
  }
})
