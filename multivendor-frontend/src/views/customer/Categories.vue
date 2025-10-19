<template>
  <v-container>
    <!-- En-tête -->
    <div class="text-center mb-8">
      <h1 class="text-h3 mb-4">🌾 Nos Catégories de Produits Agricoles</h1>
      <p class="text-h6 text-grey">Découvrez nos produits organisés par type et catégorie</p>
    </div>

    <!-- Filtres -->
    <v-card class="mb-6">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-filter</v-icon>
        Filtres
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-select
              v-model="selectedType"
              label="Type de produit"
              :items="typeOptions"
              clearable
              @update:model-value="filterCategories"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchQuery"
              label="Rechercher une catégorie"
              prepend-inner-icon="mdi-magnify"
              @input="filterCategories"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-btn 
              color="primary" 
              @click="clearFilters"
              :disabled="!hasActiveFilters"
            >
              Effacer les filtres
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Catégories principales -->
    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" size="64" />
      <p class="mt-4">Chargement des catégories...</p>
    </div>

    <div v-else-if="filteredCategories.length > 0">
      <v-row>
        <v-col 
          cols="12" 
          md="6" 
          v-for="category in filteredCategories" 
          :key="category.id"
        >
          <v-card 
            class="category-card pa-6" 
            :style="{ borderLeft: `6px solid ${category.color}` }"
            @click="goToCategory(category)"
            hover
          >
            <div class="d-flex align-center">
              <v-icon :color="category.color" size="64" class="mr-6">{{ category.icon }}</v-icon>
              <div class="flex-grow-1">
                <h3 class="text-h5 mb-2">{{ category.name }}</h3>
                <p class="text-body-1 text-grey mb-3">{{ category.description }}</p>
                <div class="d-flex align-center">
                  <v-chip :color="category.color" variant="tonal" class="mr-2">
                    {{ getCategoryCount(category) }} produits
                  </v-chip>
                  <v-chip 
                    :color="category.type === 'VEGETAL' ? 'green' : 'orange'" 
                    variant="outlined"
                    size="small"
                  >
                    {{ category.type === 'VEGETAL' ? '🌱 Végétal' : '🐄 Animal' }}
                  </v-chip>
                </div>
              </div>
              <v-icon color="grey">mdi-chevron-right</v-icon>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </div>

    <!-- Aucune catégorie trouvée -->
    <div v-else class="text-center py-8">
      <v-icon size="64" color="grey">mdi-tag-off</v-icon>
      <h3 class="text-h6 mt-4">Aucune catégorie trouvée</h3>
      <p class="text-grey">Essayez de modifier vos critères de recherche</p>
    </div>

    <!-- Sous-catégories (si une catégorie est sélectionnée) -->
    <div v-if="selectedCategory && subCategories.length > 0" class="mt-8">
      <h2 class="text-h5 mb-4">Sous-catégories de {{ selectedCategory.name }}</h2>
      <v-row>
        <v-col 
          cols="12" 
          sm="6" 
          md="4" 
          v-for="subCategory in subCategories" 
          :key="subCategory.id"
        >
          <v-card 
            class="subcategory-card pa-4" 
            @click="goToSubCategory(subCategory)"
            hover
          >
            <div class="text-center">
              <v-icon :color="selectedCategory.color" size="48" class="mb-3">{{ subCategory.icon }}</v-icon>
              <h4 class="text-h6 mb-2">{{ subCategory.name }}</h4>
              <p class="text-caption text-grey">{{ subCategory.description }}</p>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// État local
const categories = ref([])
const subCategories = ref([])
const selectedCategory = ref(null)
const selectedType = ref(null)
const searchQuery = ref('')
const loading = ref(false)

// Options pour les filtres
const typeOptions = [
  { title: '🌱 Produits Végétaux', value: 'VEGETAL' },
  { title: '🐄 Produits Animaux', value: 'ANIMAL' }
]

// État calculé
const filteredCategories = computed(() => {
  let filtered = categories.value

  if (selectedType.value) {
    filtered = filtered.filter(cat => cat.type === selectedType.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(cat => 
      cat.name.toLowerCase().includes(query) ||
      cat.description.toLowerCase().includes(query)
    )
  }

  return filtered
})

const hasActiveFilters = computed(() => {
  return selectedType.value || searchQuery.value
})

// Méthodes
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await fetch('http://localhost:3026/api/categories')
    categories.value = await response.json()
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  } finally {
    loading.value = false
  }
}

const loadSubCategories = async (categoryId) => {
  try {
    const response = await fetch(`http://localhost:3026/api/categories/${categoryId}/subcategories`)
    subCategories.value = await response.json()
  } catch (error) {
    console.error('Erreur lors du chargement des sous-catégories:', error)
  }
}

const getCategoryCount = (category) => {
  // Simuler un nombre de produits par catégorie
  return Math.floor(Math.random() * 50) + 10
}

const goToCategory = (category) => {
  selectedCategory.value = category
  loadSubCategories(category.id)
  router.push(`/customer/products?category=${category.id}`)
}

const goToSubCategory = (subCategory) => {
  router.push(`/customer/products?subcategory=${subCategory.id}`)
}

const filterCategories = () => {
  // Le filtrage se fait automatiquement via computed
}

const clearFilters = () => {
  selectedType.value = null
  searchQuery.value = ''
  selectedCategory.value = null
  subCategories.value = []
}

// Watchers
watch(selectedCategory, (newCategory) => {
  if (newCategory) {
    loadSubCategories(newCategory.id)
  } else {
    subCategories.value = []
  }
})

// Initialisation
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.subcategory-card {
  cursor: pointer;
  transition: transform 0.3s ease;
  height: 100%;
}

.subcategory-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
</style>
