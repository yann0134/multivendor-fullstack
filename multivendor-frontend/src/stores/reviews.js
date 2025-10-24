import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProductReviews, createReview, updateReview, deleteReview } from '@/services/reviews'

export const useReviewStore = defineStore('reviews', () => {
  const reviews = ref([])
  const loading = ref(false)

  // Récupérer les avis d'un produit
  const fetchProductReviews = async (productId) => {
    loading.value = true
    try {
      const response = await getProductReviews(productId)
      reviews.value = response.data
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement des avis:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Créer un avis
  const addReview = async (productId, reviewData) => {
    loading.value = true
    try {
      const response = await createReview(productId, reviewData)
      // Recharger les avis du produit
      await fetchProductReviews(productId)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de l\'avis:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Modifier un avis
  const editReview = async (reviewId, reviewData) => {
    loading.value = true
    try {
      const response = await updateReview(reviewId, reviewData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la modification de l\'avis:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Supprimer un avis
  const removeReview = async (reviewId) => {
    loading.value = true
    try {
      await deleteReview(reviewId)
      // Retirer l'avis de la liste locale
      reviews.value = reviews.value.filter(review => review.id !== reviewId)
    } catch (error) {
      console.error('Erreur lors de la suppression de l\'avis:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    reviews,
    loading,
    fetchProductReviews,
    addReview,
    editReview,
    removeReview
  }
})
