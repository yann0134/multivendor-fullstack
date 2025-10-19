import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('products', () => {
  const products = ref([])
  const categories = ref([])
  const subCategories = ref([])
  const loading = ref(false)
  const currentProduct = ref(null)
  const searchQuery = ref('')
  const selectedCategory = ref(null)
  const selectedSubCategory = ref(null)
  const filters = ref({
    type: null, // ANIMAL ou VEGETAL
    organic: false,
    local: false,
    priceRange: [0, 10000],
    sortBy: 'createdAt',
    sortOrder: 'desc'
  })

  // Actions
  const fetchProducts = async (params = {}) => {
    loading.value = true
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12,
        sortBy: params.sortBy || filters.value.sortBy,
        sortDir: params.sortDir || filters.value.sortOrder
      })

      // Ajouter les filtres
      if (filters.value.type) {
        queryParams.append('type', filters.value.type)
      }
      if (filters.value.organic) {
        queryParams.append('organic', 'true')
      }
      if (filters.value.local) {
        queryParams.append('local', 'true')
      }

      const response = await fetch(`http://localhost:3026/api/products?${queryParams}`)
      const data = await response.json()
      
      if (params.append) {
        products.value = [...products.value, ...data.content]
      } else {
        products.value = data.content
      }
      
      return data
    } catch (error) {
      console.error('Erreur lors du chargement des produits:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchProductById = async (id) => {
    loading.value = true
    try {
      const response = await fetch(`http://localhost:3026/api/products/${id}`)
      if (response.ok) {
        currentProduct.value = await response.json()
        return currentProduct.value
      } else {
        throw new Error('Produit non trouvé')
      }
    } catch (error) {
      console.error('Erreur lors du chargement du produit:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchCategories = async () => {
    try {
      const response = await fetch('http://localhost:3026/api/categories')
      categories.value = await response.json()
      return categories.value
    } catch (error) {
      console.error('Erreur lors du chargement des catégories:', error)
      throw error
    }
  }

  const fetchSubCategories = async (categoryId) => {
    try {
      const response = await fetch(`http://localhost:3026/api/categories/${categoryId}/subcategories`)
      subCategories.value = await response.json()
      return subCategories.value
    } catch (error) {
      console.error('Erreur lors du chargement des sous-catégories:', error)
      throw error
    }
  }

  const searchProducts = async (query, params = {}) => {
    loading.value = true
    try {
      const queryParams = new URLSearchParams({
        query,
        page: params.page || 0,
        size: params.size || 12
      })

      const response = await fetch(`http://localhost:3026/api/products/search?${queryParams}`)
      const data = await response.json()
      
      products.value = data.content
      searchQuery.value = query
      
      return data
    } catch (error) {
      console.error('Erreur lors de la recherche:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchProductsByCategory = async (categoryId, params = {}) => {
    loading.value = true
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12
      })

      const response = await fetch(`http://localhost:3026/api/products/category/${categoryId}?${queryParams}`)
      const data = await response.json()
      
      products.value = data.content
      selectedCategory.value = categoryId
      
      return data
    } catch (error) {
      console.error('Erreur lors du chargement des produits par catégorie:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchProductsBySubCategory = async (subCategoryId, params = {}) => {
    loading.value = true
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12
      })

      const response = await fetch(`http://localhost:3026/api/products/subcategory/${subCategoryId}?${queryParams}`)
      const data = await response.json()
      
      products.value = data.content
      selectedSubCategory.value = subCategoryId
      
      return data
    } catch (error) {
      console.error('Erreur lors du chargement des produits par sous-catégorie:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchFeaturedProducts = async () => {
    try {
      const response = await fetch('http://localhost:3026/api/products/featured')
      return await response.json()
    } catch (error) {
      console.error('Erreur lors du chargement des produits en vedette:', error)
      throw error
    }
  }

  const fetchNewProducts = async () => {
    try {
      const response = await fetch('http://localhost:3026/api/products/new')
      return await response.json()
    } catch (error) {
      console.error('Erreur lors du chargement des nouveaux produits:', error)
      throw error
    }
  }

  const setFilters = (newFilters) => {
    filters.value = { ...filters.value, ...newFilters }
  }

  const clearFilters = () => {
    filters.value = {
      type: null,
      organic: false,
      local: false,
      priceRange: [0, 10000],
      sortBy: 'createdAt',
      sortOrder: 'desc'
    }
    selectedCategory.value = null
    selectedSubCategory.value = null
    searchQuery.value = ''
  }

  const clearProducts = () => {
    products.value = []
  }

  // Getters
  const getProductsByType = (type) => {
    return products.value.filter(product => product.category?.type === type)
  }

  const getOrganicProducts = () => {
    return products.value.filter(product => product.isOrganic)
  }

  const getLocalProducts = () => {
    return products.value.filter(product => product.isLocal)
  }

  const getAvailableProducts = () => {
    return products.value.filter(product => product.quantity > 0)
  }

  return {
    // State
    products,
    categories,
    subCategories,
    loading,
    currentProduct,
    searchQuery,
    selectedCategory,
    selectedSubCategory,
    filters,
    
    // Actions
    fetchProducts,
    fetchProductById,
    fetchCategories,
    fetchSubCategories,
    searchProducts,
    fetchProductsByCategory,
    fetchProductsBySubCategory,
    fetchFeaturedProducts,
    fetchNewProducts,
    setFilters,
    clearFilters,
    clearProducts,
    
    // Getters
    getProductsByType,
    getOrganicProducts,
    getLocalProducts,
    getAvailableProducts
  }
})