<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- Navigation -->
        <RecipeNavigation />
        
        <!-- En-tête principal -->
        <v-card class="pa-8 mb-6 text-center" color="primary" dark>
          <v-icon size="80" class="mb-4">mdi-chef-hat</v-icon>
          <h1 class="text-h3 mb-4">Recettes de Cuisine</h1>
          <p class="text-h6 mb-6">
            Découvrez, créez et partagez vos recettes avec la communauté
          </p>
          <div class="d-flex justify-center gap-4">
            <v-btn
              color="white"
              size="large"
              to="/customer/recipes/discover"
            >
              <v-icon left>mdi-compass</v-icon>
              Découvrir des recettes
            </v-btn>
            <v-btn
              color="white"
              variant="outlined"
              size="large"
              to="/customer/recipes/create"
            >
              <v-icon left>mdi-plus</v-icon>
              Créer une recette
            </v-btn>
          </div>
        </v-card>
        
        <!-- Statistiques rapides -->
        <v-row class="mb-6">
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="primary" class="mb-2">mdi-book</v-icon>
              <div class="text-h4">{{ stats.totalRecipes }}</div>
              <div class="text-body-2 text-grey">Recettes disponibles</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="success" class="mb-2">mdi-account-group</v-icon>
              <div class="text-h4">{{ stats.totalUsers }}</div>
              <div class="text-body-2 text-grey">Créateurs</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="info" class="mb-2">mdi-clock</v-icon>
              <div class="text-h4">{{ stats.averageTime }} min</div>
              <div class="text-body-2 text-grey">Temps moyen</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="warning" class="mb-2">mdi-currency-usd</v-icon>
              <div class="text-h4">{{ formatPrice(stats.averagePrice) }}</div>
              <div class="text-body-2 text-grey">Prix moyen</div>
            </v-card>
          </v-col>
        </v-row>
        
        <!-- Actions rapides -->
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="pa-6" color="grey-lighten-5">
              <v-card-title class="text-h5 mb-4">
                <v-icon class="mr-2" color="primary">mdi-compass</v-icon>
                Découvrir des recettes
              </v-card-title>
              <p class="text-body-1 mb-4">
                Explorez les recettes partagées par la communauté. 
                Trouvez des idées culinaires pour tous les goûts et tous les budgets.
              </p>
              <v-btn
                color="primary"
                size="large"
                to="/customer/recipes/discover"
              >
                <v-icon left>mdi-compass</v-icon>
                Explorer maintenant
              </v-btn>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="6">
            <v-card class="pa-6" color="grey-lighten-5">
              <v-card-title class="text-h5 mb-4">
                <v-icon class="mr-2" color="success">mdi-plus</v-icon>
                Créer une recette
              </v-card-title>
              <p class="text-body-1 mb-4">
                Partagez vos recettes préférées avec la communauté. 
                Utilisez les produits disponibles sur notre plateforme.
              </p>
              <v-btn
                color="success"
                size="large"
                to="/customer/recipes/create"
              >
                <v-icon left>mdi-plus</v-icon>
                Créer maintenant
              </v-btn>
            </v-card>
          </v-col>
        </v-row>
        
        <!-- Recettes récentes -->
        <v-card class="pa-6 mt-6" v-if="recentRecipes.length > 0">
          <v-card-title class="text-h5 mb-4">
            <v-icon class="mr-2" color="primary">mdi-clock-outline</v-icon>
            Recettes récentes
          </v-card-title>
          
          <v-row>
            <v-col
              v-for="recipe in recentRecipes"
              :key="recipe.id"
              cols="12"
              md="4"
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
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import RecipeNavigation from '@/components/customer/RecipeNavigation.vue'

const router = useRouter()

// État
const stats = ref({
  totalRecipes: 0,
  totalUsers: 0,
  averageTime: 0,
  averagePrice: 0
})

const recentRecipes = ref([])

// Méthodes
const loadStats = async () => {
  try {
    // Charger les statistiques (vous pouvez implémenter un endpoint dédié)
    const response = await api.get('/api/recipes/public?size=1')
    stats.value.totalRecipes = response.data.totalElements || 0
    stats.value.totalUsers = 0 // À implémenter
    stats.value.averageTime = 30 // À calculer
    stats.value.averagePrice = 5000 // À calculer
  } catch (error) {
    console.error('Erreur lors du chargement des statistiques:', error)
  }
}

const loadRecentRecipes = async () => {
  try {
    const response = await api.get('/api/recipes/public?size=3&sortBy=recent')
    recentRecipes.value = response.data.content || []
  } catch (error) {
    console.error('Erreur lors du chargement des recettes récentes:', error)
  }
}

const viewRecipe = (id) => {
  router.push(`/customer/recipes/${id}`)
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

const totalTime = (recipe) => {
  return (recipe.preparationTime || 0) + (recipe.cookingTime || 0)
}

onMounted(() => {
  loadStats()
  loadRecentRecipes()
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
