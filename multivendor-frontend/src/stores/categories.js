import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCategoriesStore = defineStore('categories', () => {
  // État
  const categories = ref([])
  const subCategories = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const getCategoriesByType = computed(() => {
    return (type) => categories.value.filter(cat => cat.type === type)
  })

  const getVegetalCategories = computed(() => {
    return categories.value.filter(cat => cat.type === 'VEGETAL')
  })

  const getAnimalCategories = computed(() => {
    return categories.value.filter(cat => cat.type === 'ANIMAL')
  })

  const getCategoryById = computed(() => {
    return (id) => categories.value.find(cat => cat.id === id)
  })

  const getSubCategoriesByCategory = computed(() => {
    return (categoryId) => subCategories.value.filter(sub => sub.categoryId === categoryId)
  })

  // Actions
  const fetchCategories = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('http://localhost:3026/api/categories')
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const data = await response.json()
      categories.value = data
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors du chargement des catégories:', err)
      
      // Données de démonstration en cas d'erreur
      categories.value = [
        {
          id: 1,
          name: 'Produits Végétaux',
          description: 'Fruits, légumes et produits végétaux frais',
          type: 'VEGETAL',
          icon: 'mdi-sprout',
          color: 'green',
          active: true
        },
        {
          id: 2,
          name: 'Produits Animaux',
          description: 'Viandes, œufs et produits d\'origine animale',
          type: 'ANIMAL',
          icon: 'mdi-cow',
          color: 'orange',
          active: true
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchSubCategories = async (categoryId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`http://localhost:3026/api/categories/${categoryId}/subcategories`)
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const data = await response.json()
      subCategories.value = data
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors du chargement des sous-catégories:', err)
      
      // Données de démonstration en cas d'erreur
      subCategories.value = [
        {
          id: 1,
          name: 'Fruits',
          description: 'Fruits frais de saison',
          categoryId: categoryId,
          icon: 'mdi-apple',
          color: 'green',
          active: true
        },
        {
          id: 2,
          name: 'Légumes',
          description: 'Légumes frais et bio',
          categoryId: categoryId,
          icon: 'mdi-carrot',
          color: 'orange',
          active: true
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const createCategory = async (categoryData) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('http://localhost:3026/api/categories', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoryData)
      })
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const newCategory = await response.json()
      categories.value.push(newCategory)
      return newCategory
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors de la création de la catégorie:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCategory = async (categoryId, categoryData) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`http://localhost:3026/api/categories/${categoryId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoryData)
      })
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const updatedCategory = await response.json()
      const index = categories.value.findIndex(cat => cat.id === categoryId)
      if (index !== -1) {
        categories.value[index] = updatedCategory
      }
      return updatedCategory
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors de la mise à jour de la catégorie:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteCategory = async (categoryId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`http://localhost:3026/api/categories/${categoryId}`, {
        method: 'DELETE'
      })
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      categories.value = categories.value.filter(cat => cat.id !== categoryId)
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors de la suppression de la catégorie:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  const reset = () => {
    categories.value = []
    subCategories.value = []
    loading.value = false
    error.value = null
  }

  return {
    // État
    categories,
    subCategories,
    loading,
    error,
    
    // Getters
    getCategoriesByType,
    getVegetalCategories,
    getAnimalCategories,
    getCategoryById,
    getSubCategoriesByCategory,
    
    // Actions
    fetchCategories,
    fetchSubCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    clearError,
    reset
  }
})
