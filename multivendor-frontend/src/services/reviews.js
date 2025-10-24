import api from './api'

// Service pour la gestion des avis et commentaires
export const getProductReviews = (productId) => {
  return api.get(`/api/products/${productId}/reviews`)
}

export const createReview = (productId, reviewData) => {
  return api.post(`/api/products/${productId}/reviews`, reviewData)
}

export const updateReview = (reviewId, reviewData) => {
  return api.patch(`/api/reviews/${reviewId}`, reviewData)
}

export const deleteReview = (reviewId) => {
  return api.delete(`/api/reviews/${reviewId}`)
}
