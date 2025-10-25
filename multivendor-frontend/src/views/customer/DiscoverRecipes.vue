<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- Navigation -->
        <RecipeNavigation />
        
        <div class="d-flex justify-space-between align-center mb-6">
          <div>
            <h1 class="text-h4 mb-2">
              <v-icon class="mr-3" color="primary">mdi-compass</v-icon>
              Découvrir des recettes
            </h1>
            <p class="text-grey">Explorez les recettes partagées par la communauté</p>
          </div>
          <div class="d-flex gap-3">
            <v-btn
              color="primary"
              variant="outlined"
              to="/customer/recipes/my-recipes"
            >
              <v-icon left>mdi-book-open-variant</v-icon>
              Mes recettes
            </v-btn>
            <v-btn
              color="primary"
              to="/customer/recipes/create"
            >
              <v-icon left>mdi-plus</v-icon>
              Créer une recette
            </v-btn>
          </div>
        </div>
        
        <!-- Filtres et recherche -->
        <v-card class="pa-4 mb-6">
          <v-row>
            <v-col cols="12" md="3">
              <v-text-field
                v-model="searchQuery"
                label="Rechercher une recette"
                prepend-inner-icon="mdi-magnify"
                clearable
                @input="debouncedSearch"
              />
            </v-col>
            <v-col cols="12" md="2">
              <v-select
                v-model="filters.category"
                :items="categoryOptions"
                label="Catégorie"
                clearable
                @update:model-value="loadRecipes"
              />
            </v-col>
            <v-col cols="12" md="2">
              <v-select
                v-model="filters.difficulty"
                :items="difficultyOptions"
                label="Difficulté"
                clearable
                @update:model-value="loadRecipes"
              />
            </v-col>
            <v-col cols="12" md="2">
              <v-text-field
                v-model.number="filters.maxPrice"
                label="Prix max (FCFA)"
                type="number"
                clearable
                @input="debouncedLoadRecipes"
              />
            </v-col>
            <v-col cols="12" md="2">
              <v-text-field
                v-model.number="filters.maxTime"
                label="Temps max (min)"
                type="number"
                clearable
                @input="debouncedLoadRecipes"
              />
            </v-col>
            <v-col cols="12" md="1">
              <v-select
                v-model="sortBy"
                :items="sortOptions"
                label="Trier"
                @update:model-value="loadRecipes"
              />
            </v-col>
          </v-row>
        </v-card>
        
        <!-- Statistiques -->
        <v-row class="mb-6">
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="primary" class="mb-2">mdi-book</v-icon>
              <div class="text-h4">{{ totalRecipes }}</div>
              <div class="text-body-2 text-grey">Recettes disponibles</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="success" class="mb-2">mdi-account-group</v-icon>
              <div class="text-h4">{{ totalUsers }}</div>
              <div class="text-body-2 text-grey">Créateurs</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="info" class="mb-2">mdi-clock</v-icon>
              <div class="text-h4">{{ averageTime }} min</div>
              <div class="text-body-2 text-grey">Temps moyen</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="warning" class="mb-2">mdi-currency-usd</v-icon>
              <div class="text-h4">{{ formatPrice(averagePrice) }}</div>
              <div class="text-body-2 text-grey">Prix moyen</div>
            </v-card>
          </v-col>
        </v-row>
        
        <!-- Liste des recettes -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" />
          <div class="text-h6 mt-4">Chargement des recettes...</div>
        </div>
        
        <div v-else-if="recipes.length === 0" class="text-center py-8">
          <v-icon size="64" color="grey">mdi-compass</v-icon>
          <h3 class="text-h6 mt-4">Aucune recette trouvée</h3>
          <p class="text-grey">Essayez de modifier vos critères de recherche</p>
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
              
              <v-card-subtitle class="d-flex align-center">
                <v-icon size="16" class="mr-1">mdi-account</v-icon>
                {{ recipe.userFullName }}
              </v-card-subtitle>
              
              <v-card-text>
                <p class="text-body-2 text-grey mb-3">{{ recipe.description }}</p>
                
                <div class="d-flex flex-wrap gap-2 mb-3">
                  <v-chip size="small" color="primary" variant="tonal">
                    {{ getCategoryLabel(recipe.category) }}
                  </v-chip>
                  <v-chip size="small" :color="getDifficultyColor(recipe.difficulty)" variant="tonal">
                    {{ getDifficultyLabel(recipe.difficulty) }}
                  </v-chip>
                  <v-chip size="small" color="info" variant="tonal">
                    {{ totalTime(recipe) }} min
                  </v-chip>
                </div>
                
                <div class="d-flex justify-space-between align-center">
                  <div>
                    <div class="text-body-2 text-grey">Prix par portion:</div>
                    <div class="text-h6 text-primary">{{ formatPrice(recipe.totalPrice / recipe.servings) }}</div>
                  </div>
                  <div class="text-right">
                    <div class="text-body-2 text-grey">{{ recipe.servings }} portion{{ recipe.servings > 1 ? 's' : '' }}</div>
                    <div class="text-caption text-grey">{{ recipe.ingredients.length }} ingrédient{{ recipe.ingredients.length > 1 ? 's' : '' }}</div>
                  </div>
                </div>
              </v-card-text>
              
              <v-card-actions>
                <v-btn
                  color="primary"
                  variant="flat"
                  @click="viewRecipe(recipe.id)"
                >
                  <v-icon left>mdi-eye</v-icon>
                  Voir la recette
                </v-btn>
                <v-btn
                  color="success"
                  variant="outlined"
                  @click="addToCart(recipe)"
                >
                  <v-icon left>mdi-cart-plus</v-icon>
                  Ajouter au panier
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
    
    <!-- Dialog pour ajouter au panier -->
    <v-dialog v-model="cartDialog" max-width="500">
      <v-card>
        <v-card-title>Ajouter au panier</v-card-title>
        <v-card-text>
          <v-text-field
            v-model.number="servingsToAdd"
            label="Nombre de portions"
            type="number"
            min="1"
            :max="99"
            class="mb-3"
          />
          <div class="text-h6 text-primary">
            Prix total: {{ formatPrice(selectedRecipe ? (selectedRecipe.totalPrice * servingsToAdd / selectedRecipe.servings) : 0) }}
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="cartDialog = false">Annuler</v-btn>
          <v-btn color="primary" @click="confirmAddToCart">Ajouter au panier</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
const currentPage = ref(1)
const totalPages = ref(0)
const searchQuery = ref('')
const sortBy = ref('recent')

// Filtres
const filters = ref({
  category: null,
  difficulty: null,
  maxPrice: null,
  maxTime: null
})

// Dialog panier
const cartDialog = ref(false)
const selectedRecipe = ref(null)
const servingsToAdd = ref(1)

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

const sortOptions = [
  { title: 'Récentes', value: 'recent' },
  { title: 'Populaires', value: 'popular' },
  { title: 'Prix croissant', value: 'price' }
]

// Computed
const totalRecipes = computed(() => recipes.value.length)
const totalUsers = computed(() => {
  const users = new Set(recipes.value.map(recipe => recipe.userFullName))
  return users.size
})

const averageTime = computed(() => {
  if (recipes.value.length === 0) return 0
  const total = recipes.value.reduce((sum, recipe) => sum + totalTime(recipe), 0)
  return Math.round(total / recipes.value.length)
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
      size: 12,
      sortBy: sortBy.value
    })
    
    // Ajouter les filtres
    if (filters.value.category) params.append('category', filters.value.category)
    if (filters.value.difficulty) params.append('difficulty', filters.value.difficulty)
    if (filters.value.maxPrice) params.append('maxPrice', filters.value.maxPrice)
    if (filters.value.maxTime) params.append('maxTime', filters.value.maxTime)
    
    const response = await api.get(`/api/recipes/filter?${params}`)
    recipes.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Erreur lors du chargement des recettes:', error)
  } finally {
    loading.value = false
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
const debouncedLoadRecipes = debounce(loadRecipes, 500)

const viewRecipe = (id) => {
  router.push(`/customer/recipes/${id}`)
}

const addToCart = (recipe) => {
  selectedRecipe.value = recipe
  servingsToAdd.value = recipe.servings
  cartDialog.value = true
}

const confirmAddToCart = async () => {
  if (!selectedRecipe.value) return
  
  try {
    // Ici, vous devrez implémenter la logique pour ajouter la recette au panier
    // Cela implique d'ajouter tous les ingrédients de la recette au panier
    // avec les quantités ajustées selon le nombre de portions
    
    const recipeData = {
      recipeId: selectedRecipe.value.id,
      servings: servingsToAdd.value,
      ingredients: selectedRecipe.value.ingredients.map(ingredient => ({
        productId: ingredient.productId,
        quantity: Math.round((ingredient.quantity * servingsToAdd.value) / selectedRecipe.value.servings),
        unit: ingredient.unit
      }))
    }
    
    await api.post('/api/cart/add-recipe', recipeData)
    cartDialog.value = false
    alert('Recette ajoutée au panier avec succès !')
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
    alert('Erreur lors de l\'ajout au panier')
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
