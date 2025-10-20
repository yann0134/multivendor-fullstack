import api from './api'

// Service pour la gestion des produits
export const getAllProducts = (params = {}) => {
  return api.get('/api/products', { params })
}

export const getProductById = (id) => {
  return api.get(`/api/products/${id}`)
}

export const createProduct = (productData) => {
  return api.post('/api/products', productData)
}

export const updateProduct = (id, productData) => {
  return api.put(`/api/products/${id}`, productData)
}

export const deleteProduct = (id) => {
  return api.delete(`/api/products/${id}`)
}

// Endpoints d'administration pour la validation des produits
export const getPendingProducts = (params = {}) => {
  return api.get('/api/products/pending', { params })
}

export const getApprovedProducts = (params = {}) => {
  return api.get('/api/products/approved', { params })
}

export const getRejectedProducts = (params = {}) => {
  return api.get('/api/products/rejected', { params })
}

export const getProductsByStatus = (status, params = {}) => {
  return api.get(`/api/products/status/${status}`, { params })
}

export const approveProduct = (productId) => {
  return api.put(`/api/products/${productId}/approve`)
}

export const rejectProduct = (productId, rejectionReason) => {
  return api.put(`/api/products/${productId}/reject`, rejectionReason)
}

export const suspendProduct = (productId) => {
  return api.put(`/api/products/${productId}/suspend`)
}

// Recherche et filtres
export const searchProducts = (query, params = {}) => {
  return api.get('/api/products/search', { 
    params: { query, ...params } 
  })
}

export const getProductsByCategory = (categoryId, params = {}) => {
  return api.get(`/api/products/category/${categoryId}`, { params })
}

export const getProductsByType = (type, params = {}) => {
  return api.get(`/api/products/type/${type}`, { params })
}

export const getOrganicProducts = (params = {}) => {
  return api.get('/api/products/organic', { params })
}

export const getLocalProducts = (params = {}) => {
  return api.get('/api/products/local', { params })
}

export const getFeaturedProducts = () => {
  return api.get('/api/products/featured')
}

export const getNewProducts = () => {
  return api.get('/api/products/new')
}

// Endpoints spécifiques aux fournisseurs
export const getProductsByStatusAndSupplierEmail = (status, params = {}) => {
  return api.get(`/api/products/status/${status}/supplier`, { params })
}

export const getSupplierProducts = (params = {}) => {
  return api.get('/api/products/supplier', { params })
}

export const getSupplierProduct = (productId) => {
  return api.get(`/api/products/${productId}`)
}

export const updateSupplierProduct = (productId, productData) => {
  return api.put(`/api/products/${productId}`, productData)
}

export const deleteSupplierProduct = (productId) => {
  return api.delete(`/api/products/${productId}`)
}
