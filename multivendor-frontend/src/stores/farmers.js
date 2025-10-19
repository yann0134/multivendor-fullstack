import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useFarmersStore = defineStore('farmers', () => {
  // État
  const farmers = ref([])
  const selectedFarmer = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const getFarmersByRegion = computed(() => {
    return (region) => farmers.value.filter(farmer => farmer.region === region)
  })

  const getFarmersBySpecialty = computed(() => {
    return (specialty) => farmers.value.filter(farmer => farmer.specialty === specialty)
  })

  const getOrganicFarmers = computed(() => {
    return farmers.value.filter(farmer => farmer.isOrganic)
  })

  const getLocalFarmers = computed(() => {
    return farmers.value.filter(farmer => farmer.isLocal)
  })

  const getFarmerById = computed(() => {
    return (id) => farmers.value.find(farmer => farmer.id === id)
  })

  const getTopRatedFarmers = computed(() => {
    return farmers.value
      .filter(farmer => farmer.rating >= 4.0)
      .sort((a, b) => b.rating - a.rating)
  })

  // Actions
  const fetchFarmers = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('http://localhost:3026/api/farmers')
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const data = await response.json()
      farmers.value = data
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors du chargement des fermiers:', err)
      
      // Données de démonstration en cas d'erreur
      farmers.value = [
        {
          id: 1,
          name: 'Jean-Baptiste Kouassi',
          region: 'Bouaké',
          location: 'Bouaké, Côte d\'Ivoire',
          specialty: 'Fruits & Légumes',
          description: 'Ferme familiale spécialisée dans la production biologique de fruits et légumes frais.',
          image: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=200&fit=crop',
          icon: 'mdi-sprout',
          color: 'green',
          isOrganic: true,
          isLocal: true,
          rating: 4.8,
          reviews: 156,
          productCount: 25,
          contact: '+225 07 12 34 56 78',
          email: 'jean.kouassi@example.com'
        },
        {
          id: 2,
          name: 'Marie Traoré',
          region: 'Korhogo',
          location: 'Korhogo, Côte d\'Ivoire',
          specialty: 'Élevage',
          description: 'Élevage traditionnel de volailles et bovins avec des méthodes respectueuses de l\'environnement.',
          image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=200&fit=crop',
          icon: 'mdi-cow',
          color: 'orange',
          isOrganic: true,
          isLocal: true,
          rating: 4.6,
          reviews: 89,
          productCount: 18,
          contact: '+225 07 23 45 67 89',
          email: 'marie.traore@example.com'
        },
        {
          id: 3,
          name: 'Amadou Diallo',
          region: 'Yamoussoukro',
          location: 'Yamoussoukro, Côte d\'Ivoire',
          specialty: 'Céréales',
          description: 'Producteur de céréales locales avec des techniques de culture durables.',
          image: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=200&fit=crop',
          icon: 'mdi-grain',
          color: 'brown',
          isOrganic: false,
          isLocal: true,
          rating: 4.4,
          reviews: 67,
          productCount: 12,
          contact: '+225 07 34 56 78 90',
          email: 'amadou.diallo@example.com'
        },
        {
          id: 4,
          name: 'Fatou Ouattara',
          region: 'San-Pédro',
          location: 'San-Pédro, Côte d\'Ivoire',
          specialty: 'Apiculture',
          description: 'Apicultrice passionnée produisant du miel et des produits de la ruche de qualité.',
          image: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=400&h=200&fit=crop',
          icon: 'mdi-bee',
          color: 'yellow',
          isOrganic: true,
          isLocal: true,
          rating: 4.9,
          reviews: 203,
          productCount: 8,
          contact: '+225 07 45 67 89 01',
          email: 'fatou.ouattara@example.com'
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchFarmerById = async (farmerId) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`http://localhost:3026/api/farmers/${farmerId}`)
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const farmer = await response.json()
      selectedFarmer.value = farmer
      return farmer
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors du chargement du fermier:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const searchFarmers = async (query) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`http://localhost:3026/api/farmers/search?q=${encodeURIComponent(query)}`)
      
      if (!response.ok) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }
      
      const data = await response.json()
      return data
    } catch (err) {
      error.value = err.message
      console.error('Erreur lors de la recherche de fermiers:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const filterFarmers = (filters) => {
    let filtered = farmers.value

    if (filters.region) {
      filtered = filtered.filter(farmer => farmer.region === filters.region)
    }

    if (filters.specialty) {
      filtered = filtered.filter(farmer => farmer.specialty === filters.specialty)
    }

    if (filters.organicOnly) {
      filtered = filtered.filter(farmer => farmer.isOrganic)
    }

    if (filters.localOnly) {
      filtered = filtered.filter(farmer => farmer.isLocal)
    }

    if (filters.minRating) {
      filtered = filtered.filter(farmer => farmer.rating >= filters.minRating)
    }

    if (filters.searchQuery) {
      const query = filters.searchQuery.toLowerCase()
      filtered = filtered.filter(farmer => 
        farmer.name.toLowerCase().includes(query) ||
        farmer.description.toLowerCase().includes(query) ||
        farmer.specialty.toLowerCase().includes(query)
      )
    }

    return filtered
  }

  const setSelectedFarmer = (farmer) => {
    selectedFarmer.value = farmer
  }

  const clearSelectedFarmer = () => {
    selectedFarmer.value = null
  }

  const clearError = () => {
    error.value = null
  }

  const reset = () => {
    farmers.value = []
    selectedFarmer.value = null
    loading.value = false
    error.value = null
  }

  return {
    // État
    farmers,
    selectedFarmer,
    loading,
    error,
    
    // Getters
    getFarmersByRegion,
    getFarmersBySpecialty,
    getOrganicFarmers,
    getLocalFarmers,
    getFarmerById,
    getTopRatedFarmers,
    
    // Actions
    fetchFarmers,
    fetchFarmerById,
    searchFarmers,
    filterFarmers,
    setSelectedFarmer,
    clearSelectedFarmer,
    clearError,
    reset
  }
})
