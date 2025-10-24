import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'
import { getDefaultProductImage } from '@/utils/defaultImages'

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
    organic: false,
    local: false,
    priceRange: [0, 100000],
    sortBy: 'createdAt',
    sortOrder: 'desc'
  })

  // Utiliser la fonction importée pour obtenir une image par défaut

  // Fonction pour traiter les produits et ajouter des images par défaut
  const processProducts = (productsList) => {
    return productsList.map(product => ({
      ...product,
      images: product.images && product.images.length > 0 
        ? product.images 
        : [getDefaultProductImage(product)]
    }))
  }

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
      if (filters.value.organic) {
        queryParams.append('organic', 'true')
      }
      if (filters.value.local) {
        queryParams.append('local', 'true')
      }
      if (filters.value.category) {
        queryParams.append('category', filters.value.category)
      }
      if (filters.value.subCategory) {
        queryParams.append('subCategory', filters.value.subCategory)
      }
      
      // Ajouter le filtre par prix
      if (filters.value.priceRange && filters.value.priceRange.length === 2) {
        queryParams.append('minPrice', filters.value.priceRange[0])
        queryParams.append('maxPrice', filters.value.priceRange[1])
      }

      // Filtrer uniquement les produits reçus par l'entrepôt
      queryParams.append('status', 'RECEIVED_BY_WAREHOUSE')

      const response = await api.get(`/api/products?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      const processedProducts = processProducts(data.content || [])
      
      if (params.append) {
        products.value = [...products.value, ...processedProducts]
      } else {
        products.value = processedProducts
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
      const response = await api.get(`/api/products/${id}`)
      const product = response.data
      
      // Ajouter une image par défaut si nécessaire
      if (!product.images || product.images.length === 0) {
        product.images = [getDefaultProductImage(product)]
      }
      
      currentProduct.value = product
      return product
    } catch (error) {
      console.error('Erreur lors du chargement du produit:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchCategories = async () => {
    try {
      const response = await api.get('/api/categories')
      categories.value = response.data
      return categories.value
    } catch (error) {
      console.error('Erreur lors du chargement des catégories:', error)
      throw error
    }
  }

  const fetchSubCategories = async (categoryId) => {
    try {
      const response = await api.get(`/api/categories/${categoryId}/subcategories`)
      subCategories.value = response.data
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
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/search?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      const processedProducts = processProducts(data.content || [])
      products.value = processedProducts
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
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/category/${categoryId}?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      const processedProducts = processProducts(data.content || [])
      products.value = processedProducts
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
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/subcategory/${subCategoryId}?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      const processedProducts = processProducts(data.content || [])
      products.value = processedProducts
      selectedSubCategory.value = subCategoryId
      
      return data
    } catch (error) {
      console.error('Erreur lors du chargement des produits par sous-catégorie:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchProductsByType = async (type, params = {}) => {
    loading.value = true
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/type/${type}?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      const processedProducts = processProducts(data.content || [])
      products.value = processedProducts
      
      return data
    } catch (error) {
      console.error('Erreur lors du chargement des produits par type:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchFeaturedProducts = async () => {
    try {
      const response = await api.get('/api/products/featured')
      const data = response.data
      
      // Traiter les produits avec images par défaut
      return processProducts(data || [])
    } catch (error) {
      console.error('Erreur lors du chargement des produits en vedette:', error)
      throw error
    }
  }

  const fetchNewProducts = async () => {
    try {
      const response = await api.get('/api/products/new')
      const data = response.data
      
      // Traiter les produits avec images par défaut
      return processProducts(data || [])
    } catch (error) {
      console.error('Erreur lors du chargement des nouveaux produits:', error)
      throw error
    }
  }

  const fetchOrganicProducts = async (params = {}) => {
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/organic?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      return processProducts(data.content || [])
    } catch (error) {
      console.error('Erreur lors du chargement des produits bio:', error)
      throw error
    }
  }

  const fetchLocalProducts = async (params = {}) => {
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 12,
        status: 'RECEIVED_BY_WAREHOUSE' // Filtrer les produits reçus
      })

      const response = await api.get(`/api/products/local?${queryParams}`)
      const data = response.data
      
      // Traiter les produits avec images par défaut
      return processProducts(data.content || [])
    } catch (error) {
      console.error('Erreur lors du chargement des produits locaux:', error)
      throw error
    }
  }

  const fetchPriceRange = async () => {
    try {
      const response = await api.get('/api/products/price-range')
      return response.data
    } catch (error) {
      console.error('Erreur lors du chargement de la gamme de prix:', error)
      // Retourner des valeurs par défaut en cas d'erreur
      return { minPrice: 0, maxPrice: 100000 }
    }
  }

  const setFilters = (newFilters) => {
    filters.value = { ...filters.value, ...newFilters }
  }

  const clearFilters = () => {
    filters.value = {
      organic: false,
      local: false,
      priceRange: [0, 100000],
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
    return products.value.filter(product => 
      (product.supplierAvailableQuantity || product.quantity || 0) > 0
    )
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
    fetchProductsByType,
    fetchFeaturedProducts,
    fetchNewProducts,
    fetchOrganicProducts,
    fetchLocalProducts,
    fetchPriceRange,
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
