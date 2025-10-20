import api from './api'

export const getAllCategories = () => {
  return api.get('/api/categories').then(r => r.data)
}

export const createCategory = (payload) => {
  return api.post('/api/categories', payload).then(r => r.data)
}

export const createSubCategory = (categoryId, payload) => {
  return api.post(`/api/categories/${categoryId}/subcategories`, payload).then(r => r.data)
}

export const getSubCategoriesByCategoryName = (categoryName) => {
  return api.get(`/api/categories/name/${categoryName}/subcategories`).then(r => r.data)
}


