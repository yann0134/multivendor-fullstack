<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- Navigation -->
        <RecipeNavigation />
        
        <div class="d-flex justify-space-between align-center mb-6">
          <div>
            <h1 class="text-h4 mb-2">
              <v-icon class="mr-3" color="primary">mdi-book-open-variant</v-icon>
              Mes recettes
            </h1>
            <p class="text-grey">Gérez vos recettes personnelles</p>
          </div>
          <div class="d-flex gap-3">
            <v-btn
              color="primary"
              variant="outlined"
              to="/customer/recipes/discover"
            >
              <v-icon left>mdi-compass</v-icon>
              Découvrir
            </v-btn>
            <v-btn
              color="primary"
              size="large"
              to="/customer/recipes/create"
            >
              <v-icon left>mdi-plus</v-icon>
              Nouvelle recette
            </v-btn>
          </div>
        </div>
        
        <!-- Statistiques -->
        <v-row class="mb-6">
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="primary" class="mb-2">mdi-book</v-icon>
              <div class="text-h4">{{ stats.totalRecipes }}</div>
              <div class="text-body-2 text-grey">Total recettes</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="success" class="mb-2">mdi-earth</v-icon>
              <div class="text-h4">{{ publishedCount }}</div>
              <div class="text-body-2 text-grey">Publiées</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="warning" class="mb-2">mdi-lock</v-icon>
              <div class="text-h4">{{ privateCount }}</div>
              <div class="text-body-2 text-grey">Privées</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="info" class="mb-2">mdi-currency-usd</v-icon>
              <div class="text-h4">{{ formatPrice(averagePrice) }}</div>
              <div class="text-body-2 text-grey">Prix moyen</div>
            </v-card>
          </v-col>
        </v-row>
        
        <!-- Filtres -->
        <v-card class="pa-4 mb-4">
          <v-row>
            <v-col cols="12" md="3">
              <v-select
                v-model="filters.category"
                :items="categoryOptions"
                label="Catégorie"
                clearable
                @update:model-value="loadRecipes"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-select
                v-model="filters.difficulty"
                :items="difficultyOptions"
                label="Difficulté"
                clearable
                @update:model-value="loadRecipes"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-select
                v-model="filters.status"
                :items="statusOptions"
                label="Statut"
                clearable
                @update:model-value="loadRecipes"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field
                v-model="searchQuery"
                label="Rechercher"
                prepend-inner-icon="mdi-magnify"
                clearable
                @input="debouncedSearch"
              />
            </v-col>
          </v-row>
        </v-card>
        
        <!-- Liste des recettes -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" />
          <div class="text-h6 mt-4">Chargement des recettes...</div>
        </div>
        
        <div v-else-if="recipes.length === 0" class="text-center py-8">
          <v-icon size="64" color="grey">mdi-book-open-variant</v-icon>
          <h3 class="text-h6 mt-4">Aucune recette trouvée</h3>
          <p class="text-grey mb-4">Commencez par créer votre première recette</p>
          <v-btn color="primary" to="/customer/recipes/create">
            <v-icon left>mdi-plus</v-icon>
            Créer une recette
          </v-btn>
        </div>
        
        <v-row v-else>
          <v-col
            v-for="recipe in recipes"
            :key="recipe.id"
            cols="12"
            md="6"
            lg="4"
          >
            <v-card class="recipe-card" elevation="2">
              <v-card-title class="text-h6">{{ recipe.title }}</v-card-title>
              
              <v-card-text>
                <p class="text-body-2 text-grey mb-3">{{ recipe.description }}</p>
                
                <div class="d-flex flex-wrap gap-2 mb-3">
                  <v-chip size="small" color="primary" variant="tonal">
                    {{ getCategoryLabel(recipe.category) }}
                  </v-chip>
                  <v-chip size="small" :color="getDifficultyColor(recipe.difficulty)" variant="tonal">
                    {{ getDifficultyLabel(recipe.difficulty) }}
                  </v-chip>
                  <v-chip size="small" :color="recipe.isPublished ? 'success' : 'warning'" variant="tonal">
                    {{ recipe.isPublished ? 'Publiée' : 'Privée' }}
                  </v-chip>
                </div>
                
                <div class="d-flex justify-space-between align-center">
                  <div>
                    <div class="text-body-2 text-grey">Prix par portion:</div>
                    <div class="text-h6 text-primary">{{ formatPrice(recipe.totalPrice / recipe.servings) }}</div>
                  </div>
                  <div class="text-right">
                    <div class="text-body-2 text-grey">{{ recipe.servings }} portion{{ recipe.servings > 1 ? 's' : '' }}</div>
                    <div class="text-caption text-grey">{{ totalTime(recipe) }} min</div>
                  </div>
                </div>
              </v-card-text>
              
              <v-card-actions>
                <v-btn
                  variant="text"
                  color="primary"
                  @click="viewRecipe(recipe.id)"
                >
                  <v-icon left>mdi-eye</v-icon>
                  Voir
                </v-btn>
                <v-btn
                  variant="text"
                  color="primary"
                  @click="editRecipe(recipe.id)"
                >
                  <v-icon left>mdi-pencil</v-icon>
                  Modifier
                </v-btn>
                <v-spacer />
                <v-btn
                  :color="recipe.isPublished ? 'warning' : 'success'"
                  variant="text"
                  @click="togglePublish(recipe)"
                >
                  <v-icon left>{{ recipe.isPublished ? 'mdi-eye-off' : 'mdi-eye' }}</v-icon>
                  {{ recipe.isPublished ? 'Dépublier' : 'Publier' }}
                </v-btn>
                <v-btn
                  color="error"
                  variant="text"
                  @click="deleteRecipe(recipe)"
                >
                  <v-icon left>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
        
        <!-- Pagination -->
        <v-pagination
          v-if="totalPages > 1"
          v-model="currentPage"
          :length="totalPages"
          @update:model-value="loadRecipes"
          class="mt-6"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import RecipeNavigation from '@/components/customer/RecipeNavigation.vue'

const router = useRouter()

// État
const loading = ref(false)
const recipes = ref([])
const stats = ref({ totalRecipes: 0, publishedRecipes: 0 })
const currentPage = ref(1)
const totalPages = ref(0)
const searchQuery = ref('')

// Filtres
const filters = ref({
  category: null,
  difficulty: null,
  status: null
})

// Options
const categoryOptions = [
  { title: 'Entrée', value: 'ENTREE' },
  { title: 'Plat principal', value: 'PLAT' },
  { title: 'Dessert', value: 'DESSERT' },
  { title: 'Boisson', value: 'BOISSON' }
]

const difficultyOptions = [
  { title: 'Facile', value: 'FACILE' },
  { title: 'Moyen', value: 'MOYEN' },
  { title: 'Difficile', value: 'DIFFICILE' }
]

const statusOptions = [
  { title: 'Publiées', value: 'published' },
  { title: 'Privées', value: 'private' }
]

// Computed
const publishedCount = computed(() => {
  return recipes.value.filter(recipe => recipe.isPublished).length
})

const privateCount = computed(() => {
  return recipes.value.filter(recipe => !recipe.isPublished).length
})

const averagePrice = computed(() => {
  if (recipes.value.length === 0) return 0
  const total = recipes.value.reduce((sum, recipe) => sum + (recipe.totalPrice / recipe.servings), 0)
  return Math.round(total / recipes.value.length)
})

// Méthodes
const loadRecipes = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams({
      page: currentPage.value - 1,
      size: 12
    })
    
    const response = await api.get(`/api/recipes/my-recipes?${params}`)
    recipes.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Erreur lors du chargement des recettes:', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await api.get('/api/recipes/stats')
    stats.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des statistiques:', error)
  }
}

const searchRecipes = async () => {
  if (!searchQuery.value.trim()) {
    loadRecipes()
    return
  }
  
  loading.value = true
  try {
    const params = new URLSearchParams({
      query: searchQuery.value,
      page: currentPage.value - 1,
      size: 12
    })
    
    const response = await api.get(`/api/recipes/search?${params}`)
    recipes.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Erreur lors de la recherche:', error)
  } finally {
    loading.value = false
  }
}

const debouncedSearch = debounce(searchRecipes, 500)

const viewRecipe = (id) => {
  router.push(`/customer/recipes/${id}`)
}

const editRecipe = (id) => {
  router.push(`/customer/recipes/${id}/edit`)
}

const togglePublish = async (recipe) => {
  try {
    await api.put(`/api/recipes/${recipe.id}/publish`)
    recipe.isPublished = !recipe.isPublished
  } catch (error) {
    console.error('Erreur lors de la publication:', error)
    alert('Erreur lors de la publication de la recette')
  }
}

const deleteRecipe = async (recipe) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette recette ?')) return
  
  try {
    await api.delete(`/api/recipes/${recipe.id}`)
    await loadRecipes()
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
    alert('Erreur lors de la suppression de la recette')
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

const getCategoryLabel = (category) => {
  const option = categoryOptions.find(opt => opt.value === category)
  return option ? option.title : category
}

const getDifficultyLabel = (difficulty) => {
  const option = difficultyOptions.find(opt => opt.value === difficulty)
  return option ? option.title : difficulty
}

const getDifficultyColor = (difficulty) => {
  const colors = { FACILE: 'success', MOYEN: 'warning', DIFFICILE: 'error' }
  return colors[difficulty] || 'grey'
}

const totalTime = (recipe) => {
  return (recipe.preparationTime || 0) + (recipe.cookingTime || 0)
}

// Fonction debounce
function debounce(func, wait) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

onMounted(() => {
  loadRecipes()
  loadStats()
})
</script>

<style scoped>
.recipe-card {
  height: 100%;
  transition: transform 0.2s;
}

.recipe-card:hover {
  transform: translateY(-2px);
}
</style>
