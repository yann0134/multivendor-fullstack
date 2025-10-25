<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- Debug info -->
        <v-alert v-if="!recipe && !loading" type="info" class="mb-4">
          Chargement de la recette ID: {{ route.params.id }}
        </v-alert>
        
        <v-alert v-if="loading" type="info" class="mb-4">
          <v-progress-circular indeterminate class="mr-2" />
          Chargement en cours...
        </v-alert>
        
        <v-alert v-if="error" type="error" class="mb-4">
          Erreur: {{ error }}
        </v-alert>
        
        <!-- Navigation -->
        <RecipeNavigation />
        
        <!-- Contenu principal de la recette -->
        <div v-if="recipe">
          <!-- En-tête de la recette -->
          <v-card class="pa-6 mb-6">
            <v-row>
              <v-col cols="12" md="8">
                <h1 class="text-h3 mb-3">{{ recipe.title }}</h1>
                <p class="text-h6 text-grey mb-4">{{ recipe.description }}</p>
                
                <div class="d-flex flex-wrap gap-3 mb-4">
                  <v-chip color="primary" variant="tonal">
                    {{ getCategoryLabel(recipe.category) }}
                  </v-chip>
                  <v-chip :color="getDifficultyColor(recipe.difficulty)" variant="tonal">
                    {{ getDifficultyLabel(recipe.difficulty) }}
                  </v-chip>
                  <v-chip color="info" variant="tonal">
                    {{ totalTime }} min
                  </v-chip>
                  <v-chip color="success" variant="tonal">
                    {{ recipe.servings }} portion{{ recipe.servings > 1 ? 's' : '' }}
                  </v-chip>
                </div>
                
                <div class="d-flex align-center mb-4">
                  <v-icon class="mr-2">mdi-account</v-icon>
                  <span class="text-body-1">Par {{ recipe.userFullName }}</span>
                </div>
              </v-col>
              
              <v-col cols="12" md="4">
                <v-card variant="outlined" class="pa-4">
                  <v-card-title class="text-h6 mb-3">Informations</v-card-title>
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Prix par portion:</div>
                    <div class="text-h5 text-primary">{{ formatPrice(pricePerServing) }}</div>
                  </div>
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Prix total:</div>
                    <div class="text-h6 text-primary">{{ formatPrice(recipe.totalPrice) }}</div>
                  </div>
                  
                  <v-divider class="my-3" />
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Temps de préparation:</div>
                    <div class="text-body-1">{{ recipe.preparationTime }} minutes</div>
                  </div>
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Temps de cuisson:</div>
                    <div class="text-body-1">{{ recipe.cookingTime }} minutes</div>
                  </div>
                  
                  <v-divider class="my-3" />
                  
                  <v-text-field
                    v-model.number="servingsToAdd"
                    label="Nombre de portions"
                    type="number"
                    min="1"
                    :max="99"
                    class="mb-3"
                  />
                  
                  <div class="text-h6 text-primary mb-3">
                    Prix pour {{ servingsToAdd }} portion{{ servingsToAdd > 1 ? 's' : '' }}: 
                    {{ formatPrice(calculatedPrice) }}
                  </div>
                  
                  <v-btn
                    color="success"
                    size="large"
                    block
                    @click="addToCart"
                  >
                    <v-icon left>mdi-cart-plus</v-icon>
                    Ajouter au panier
                  </v-btn>
                </v-card>
              </v-col>
            </v-row>
          </v-card>
          
          <v-row>
            <!-- Ingrédients -->
            <v-col cols="12" md="6">
              <v-card class="pa-4">
                <v-card-title class="text-h5 mb-4">
                  <v-icon class="mr-2">mdi-food</v-icon>
                  Ingrédients
                </v-card-title>
                
                <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="mb-3">
                  <v-card variant="outlined" class="pa-3">
                    <div class="d-flex justify-space-between align-center">
                      <div>
                        <div class="text-h6">{{ ingredient.productTitle }}</div>
                        <div class="text-body-2 text-grey">{{ ingredient.quantity }} {{ ingredient.unit }}</div>
                        <div v-if="ingredient.notes" class="text-caption text-grey">{{ ingredient.notes }}</div>
                      </div>
                      <div class="text-right">
                        <div class="text-h6 text-primary">{{ formatPrice(ingredient.productPrice) }}</div>
                        <div class="text-caption text-grey">par {{ ingredient.unit }}</div>
                      </div>
                    </div>
                  </v-card>
                </div>
              </v-card>
            </v-col>
            
            <!-- Instructions -->
            <v-col cols="12" md="6">
              <v-card class="pa-4">
                <v-card-title class="text-h5 mb-4">
                  <v-icon class="mr-2">mdi-format-list-numbered</v-icon>
                  Instructions
                </v-card-title>
                
                <div class="text-body-1" style="white-space: pre-line;">{{ recipe.instructions }}</div>
              </v-card>
            </v-col>
          </v-row>
        </div>
        
        <!-- États de chargement et d'erreur -->
        <div v-else-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" />
          <div class="text-h6 mt-4">Chargement de la recette...</div>
        </div>
        
        <div v-else class="text-center py-8">
          <v-icon size="64" color="grey">mdi-alert-circle</v-icon>
          <h3 class="text-h6 mt-4">Recette non trouvée</h3>
          <p class="text-grey">Cette recette n'existe pas ou a été supprimée</p>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'
import RecipeNavigation from '@/components/customer/RecipeNavigation.vue'

const route = useRoute()

// État
const loading = ref(false)
const recipe = ref(null)
const servingsToAdd = ref(1)
const error = ref(null)

// Computed
const pricePerServing = computed(() => {
  return recipe.value ? Math.round(recipe.value.totalPrice / recipe.value.servings) : 0
})

const totalTime = computed(() => {
  return recipe.value ? (recipe.value.preparationTime || 0) + (recipe.value.cookingTime || 0) : 0
})

const calculatedPrice = computed(() => {
  if (!recipe.value) return 0
  return Math.round((recipe.value.totalPrice * servingsToAdd.value) / recipe.value.servings)
})

// Méthodes
const loadRecipe = async () => {
  loading.value = true
  error.value = null
  try {
    console.log('Chargement de la recette ID:', route.params.id)
    const response = await api.get(`/api/recipes/${route.params.id}`)
    console.log('Réponse reçue:', response.data)
    recipe.value = response.data
    servingsToAdd.value = recipe.value.servings
  } catch (err) {
    console.error('Erreur lors du chargement de la recette:', err)
    error.value = err.response?.data?.message || err.message || 'Erreur inconnue'
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  if (!recipe.value) return
  
  try {
    const recipeData = {
      recipeId: recipe.value.id,
      servings: servingsToAdd.value,
      ingredients: recipe.value.ingredients.map(ingredient => ({
        productId: ingredient.productId,
        quantity: Math.round((ingredient.quantity * servingsToAdd.value) / recipe.value.servings),
        unit: ingredient.unit
      }))
    }
    
    await api.post('/api/cart/add-recipe', recipeData)
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
  const options = [
    { title: 'Entrée', value: 'ENTREE' },
    { title: 'Plat principal', value: 'PLAT' },
    { title: 'Dessert', value: 'DESSERT' },
    { title: 'Boisson', value: 'BOISSON' }
  ]
  const option = options.find(opt => opt.value === category)
  return option ? option.title : category
}

const getDifficultyLabel = (difficulty) => {
  const options = [
    { title: 'Facile', value: 'FACILE' },
    { title: 'Moyen', value: 'MOYEN' },
    { title: 'Difficile', value: 'DIFFICILE' }
  ]
  const option = options.find(opt => opt.value === difficulty)
  return option ? option.title : difficulty
}

const getDifficultyColor = (difficulty) => {
  const colors = { FACILE: 'success', MOYEN: 'warning', DIFFICILE: 'error' }
  return colors[difficulty] || 'grey'
}

onMounted(() => {
  loadRecipe()
})
</script>

<style scoped>
/* Styles spécifiques si nécessaire */
</style>