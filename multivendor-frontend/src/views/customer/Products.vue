<template>
  <v-container>
    <!-- En-tête avec recherche -->
    <v-row>
      <v-col cols="12">
        <v-card color="primary" dark class="pa-4 mb-6">
          <h1 class="text-h4 mb-4">🛒 Catalogue des Produits Agricoles</h1>
          <v-text-field
            v-model="searchQuery"
            label="Rechercher des produits..."
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            hide-details
            @keyup.enter="handleSearch"
            class="search-field"
          >
            <template #append>
              <v-btn 
                color="white" 
                variant="text" 
                @click="handleSearch"
                :loading="productStore.loading"
              >
                Rechercher
              </v-btn>
            </template>
          </v-text-field>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <!-- Filtres -->
      <v-col cols="12" md="3">
        <v-card class="filters-card">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-filter</v-icon>
            Filtres
            <v-spacer />
            <v-btn 
              size="small" 
              variant="text" 
              @click="clearFilters"
              v-if="hasActiveFilters"
            >
              Effacer
            </v-btn>
          </v-card-title>
          
          <v-card-text>
            <!-- Type de produit -->
            <v-select
              v-model="filters.type"
              label="Type de produit"
              :items="typeOptions"
              clearable
              @update:model-value="applyFilters"
              class="mb-4"
            />
            
            <!-- Catégorie -->
            <v-select
              v-model="filters.category"
              label="Catégorie"
              :items="categoryOptions"
              clearable
              @update:model-value="applyFilters"
              class="mb-4"
            />
            
            <!-- Sous-catégorie -->
            <v-select
              v-model="filters.subCategory"
              label="Sous-catégorie"
              :items="subCategoryOptions"
              clearable
              @update:model-value="applyFilters"
              class="mb-4"
              :disabled="!filters.category"
            />
            
            <!-- Produits spéciaux -->
            <v-checkbox
              v-model="filters.organic"
              label="🌱 Produits Bio"
              @change="applyFilters"
              class="mb-2"
            />
            
            <v-checkbox
              v-model="filters.local"
              label="🏠 Produits Locaux"
              @change="applyFilters"
              class="mb-4"
            />
            
            <!-- Prix -->
            <div class="mb-4">
              <label class="text-subtitle-2 mb-2 d-block">Prix (FCFA)</label>
              <v-range-slider
                v-model="priceRange"
                :min="0"
                :max="10000"
                :step="100"
                thumb-label
                @update:model-value="updatePriceFilter"
                class="mt-2"
              />
              <div class="d-flex justify-space-between text-caption text-grey">
                <span>{{ formatPrice(priceRange[0]) }}</span>
                <span>{{ formatPrice(priceRange[1]) }}</span>
              </div>
            </div>
            
            <!-- Tri -->
            <v-select
              v-model="filters.sortBy"
              label="Trier par"
              :items="sortOptions"
              @update:model-value="applyFilters"
            />
          </v-card-text>
        </v-card>
      </v-col>
      
      <!-- Liste des produits -->
      <v-col cols="12" md="9">
        <!-- En-tête des résultats -->
        <div class="d-flex justify-space-between align-center mb-4">
          <div>
            <h2 class="text-h6">
              {{ getResultsText() }}
            </h2>
            <p class="text-caption text-grey">
              {{ productStore.products.length }} produit(s) trouvé(s)
            </p>
          </div>
          
          <v-btn-toggle
            v-model="viewMode"
            mandatory
            class="view-toggle"
          >
            <v-btn value="grid" size="small">
              <v-icon>mdi-view-grid</v-icon>
            </v-btn>
            <v-btn value="list" size="small">
              <v-icon>mdi-view-list</v-icon>
            </v-btn>
          </v-btn-toggle>
        </div>

        <!-- Loading -->
        <div v-if="productStore.loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" />
          <p class="mt-4">Chargement des produits...</p>
        </div>

        <!-- Produits -->
        <div v-else-if="productStore.products.length > 0">
          <v-row>
            <v-col
              v-for="product in productStore.products"
              :key="product.id"
              :cols="viewMode === 'list' ? 12 : 12"
              :sm="viewMode === 'list' ? 12 : 6"
              :md="viewMode === 'list' ? 12 : 4"
            >
              <ProductCard
                :product="product"
                :view-mode="viewMode"
              />
            </v-col>
          </v-row>
          
          <!-- Pagination -->
          <v-pagination
            v-if="totalPages > 1"
            v-model="currentPage"
            :length="totalPages"
            @update:model-value="loadPage"
            class="mt-6"
          />
        </div>

        <!-- Aucun produit trouvé -->
        <div v-else class="text-center py-8">
          <v-icon size="64" color="grey">mdi-package-variant-closed</v-icon>
          <h3 class="text-h6 mt-4">Aucun produit trouvé</h3>
          <p class="text-grey">Essayez de modifier vos critères de recherche</p>
          <v-btn 
            color="primary" 
            class="mt-4" 
            @click="clearFilters"
          >
            Effacer les filtres
          </v-btn>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'
import ProductCard from '@/components/customer/ProductCard.vue'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()

// État local
const searchQuery = ref('')
const currentPage = ref(0)
const totalPages = ref(0)
const viewMode = ref('grid')

// Filtres
const filters = ref({
  type: null,
  category: null,
  subCategory: null,
  organic: false,
  local: false,
  sortBy: 'createdAt'
})

const priceRange = ref([0, 10000])

// Options pour les sélecteurs
const typeOptions = ref([
  { title: '🌱 Produits Végétaux', value: 'VEGETAL' },
  { title: '🐄 Produits Animaux', value: 'ANIMAL' }
])

const categoryOptions = ref([])
const subCategoryOptions = ref([])

const sortOptions = ref([
  { title: 'Nouveautés', value: 'createdAt' },
  { title: 'Prix croissant', value: 'sellingPrice' },
  { title: 'Prix décroissant', value: 'sellingPrice' },
  { title: 'Nom', value: 'title' },
  { title: 'Populaire', value: 'numRatings' }
])

// Computed
const hasActiveFilters = computed(() => {
  return filters.value.type || 
         filters.value.category || 
         filters.value.subCategory || 
         filters.value.organic || 
         filters.value.local ||
         priceRange.value[0] > 0 || 
         priceRange.value[1] < 10000
})

// Méthodes
const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

const getResultsText = () => {
  if (searchQuery.value) {
    return `Résultats pour "${searchQuery.value}"`
  }
  if (filters.value.category) {
    const category = categoryOptions.value.find(c => c.value === filters.value.category)
    return category ? category.title : 'Produits'
  }
  if (filters.value.type) {
    const type = typeOptions.value.find(t => t.value === filters.value.type)
    return type ? type.title : 'Produits'
  }
  return 'Tous les produits'
}

const loadCategories = async () => {
  try {
    await productStore.fetchCategories()
    categoryOptions.value = productStore.categories.map(cat => ({
      title: cat.name,
      value: cat.id
    }))
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  }
}

const loadSubCategories = async (categoryId) => {
  if (!categoryId) {
    subCategoryOptions.value = []
    return
  }
  
  try {
    await productStore.fetchSubCategories(categoryId)
    subCategoryOptions.value = productStore.subCategories.map(sub => ({
      title: sub.name,
      value: sub.id
    }))
  } catch (error) {
    console.error('Erreur lors du chargement des sous-catégories:', error)
  }
}

const loadProducts = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: 12,
      sortBy: filters.value.sortBy,
      sortDir: 'desc'
    }

    // Appliquer les filtres
    if (filters.value.type) {
      const data = await productStore.fetchProductsByType(filters.value.type, params)
      totalPages.value = data.totalPages
    } else if (filters.value.category) {
      const data = await productStore.fetchProductsByCategory(filters.value.category, params)
      totalPages.value = data.totalPages
    } else if (filters.value.subCategory) {
      const data = await productStore.fetchProductsBySubCategory(filters.value.subCategory, params)
      totalPages.value = data.totalPages
    } else {
      const data = await productStore.fetchProducts(params)
      totalPages.value = data.totalPages
    }
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  }
}

const handleSearch = async () => {
  if (searchQuery.value.trim()) {
    try {
      await productStore.searchProducts(searchQuery.value.trim())
    } catch (error) {
      console.error('Erreur lors de la recherche:', error)
    }
  } else {
    await loadProducts()
  }
}

const applyFilters = async () => {
  currentPage.value = 0
  await loadProducts()
}

const updatePriceFilter = () => {
  // Implémenter le filtre par prix si nécessaire
  applyFilters()
}

const clearFilters = () => {
  filters.value = {
    type: null,
    category: null,
    subCategory: null,
    organic: false,
    local: false,
    sortBy: 'createdAt'
  }
  priceRange.value = [0, 10000]
  searchQuery.value = ''
  currentPage.value = 0
  loadProducts()
}

const loadPage = (page) => {
  currentPage.value = page - 1
  loadProducts()
}

// Watchers
watch(() => filters.value.category, (newCategory) => {
  filters.value.subCategory = null
  loadSubCategories(newCategory)
  applyFilters()
})

// Initialisation
onMounted(async () => {
  await loadCategories()
  
  // Vérifier les paramètres de l'URL
  if (route.query.category) {
    filters.value.category = parseInt(route.query.category)
    await loadSubCategories(filters.value.category)
  }
  
  await loadProducts()
})
</script>

<style scoped>
.filters-card {
  position: sticky;
  top: 20px;
}

.search-field {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.view-toggle {
  border-radius: 8px;
}

.category-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>
