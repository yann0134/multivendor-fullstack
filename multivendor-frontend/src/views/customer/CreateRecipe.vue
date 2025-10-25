<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- Navigation -->
        <RecipeNavigation />
        
        <v-card class="pa-6">
          <div class="d-flex justify-space-between align-center mb-4">
            <v-card-title class="text-h4 pa-0">
              <v-icon class="mr-3" color="primary">mdi-chef-hat</v-icon>
              Créer une nouvelle recette
            </v-card-title>
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
                variant="outlined"
                to="/customer/recipes/my-recipes"
              >
                <v-icon left>mdi-book-open-variant</v-icon>
                Mes recettes
              </v-btn>
            </div>
          </div>
          
          <v-form @submit.prevent="saveRecipe" ref="form">
            <v-row>
              <!-- Informations générales -->
              <v-col cols="12" md="8">
                <v-card variant="outlined" class="pa-4 mb-4">
                  <v-card-title class="text-h6 mb-3">Informations générales</v-card-title>
                  
                  <v-text-field
                    v-model="recipe.title"
                    label="Nom de la recette"
                    :rules="[v => !!v || 'Le nom est requis']"
                    required
                    class="mb-3"
                  />
                  
                  <v-textarea
                    v-model="recipe.description"
                    label="Description"
                    placeholder="Décrivez votre recette..."
                    rows="3"
                    class="mb-3"
                  />
                  
                  <v-textarea
                    v-model="recipe.instructions"
                    label="Instructions de préparation"
                    placeholder="Étape 1: ...&#10;Étape 2: ..."
                    rows="6"
                    :rules="[v => !!v || 'Les instructions sont requises']"
                    required
                    class="mb-3"
                  />
                </v-card>
                
                <!-- Ingrédients -->
                <v-card variant="outlined" class="pa-4 mb-4">
                  <v-card-title class="text-h6 mb-3 d-flex justify-space-between align-center">
                    Ingrédients
                    <v-btn
                      color="primary"
                      variant="outlined"
                      size="small"
                      @click="addIngredient"
                    >
                      <v-icon left>mdi-plus</v-icon>
                      Ajouter
                    </v-btn>
                  </v-card-title>
                  
                  <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="mb-3">
                    <v-card variant="outlined" class="pa-3">
                      <v-row>
                        <v-col cols="12" md="4">
                          <v-select
                            v-model="ingredient.productId"
                            :items="productOptions"
                            item-title="title"
                            item-value="id"
                            label="Produit"
                            :rules="[v => !!v || 'Produit requis']"
                            @update:model-value="onProductSelect(index)"
                          />
                        </v-col>
                        <v-col cols="12" md="2">
                          <v-text-field
                            v-model.number="ingredient.quantity"
                            label="Quantité"
                            type="number"
                            min="1"
                            :rules="[v => !!v && v > 0 || 'Quantité requise']"
                            @input="calculateTotalPrice"
                          />
                        </v-col>
                        <v-col cols="12" md="2">
                          <v-text-field
                            v-model="ingredient.unit"
                            label="Unité"
                            placeholder="kg, g, L, ml..."
                            :rules="[v => !!v || 'Unité requise']"
                          />
                        </v-col>
                        <v-col cols="12" md="3">
                          <v-text-field
                            v-model="ingredient.notes"
                            label="Notes (optionnel)"
                            placeholder="Ex: émincé finement"
                          />
                        </v-col>
                        <v-col cols="12" md="1" class="d-flex align-center">
                          <v-btn
                            icon="mdi-delete"
                            color="error"
                            variant="text"
                            @click="removeIngredient(index)"
                          />
                        </v-col>
                      </v-row>
                    </v-card>
                  </div>
                  
                  <v-alert
                    v-if="recipe.ingredients.length === 0"
                    type="info"
                    variant="tonal"
                    class="mt-3"
                  >
                    Ajoutez au moins un ingrédient à votre recette
                  </v-alert>
                </v-card>
              </v-col>
              
              <!-- Paramètres et résumé -->
              <v-col cols="12" md="4">
                <v-card variant="outlined" class="pa-4 mb-4">
                  <v-card-title class="text-h6 mb-3">Paramètres</v-card-title>
                  
                  <v-text-field
                    v-model.number="recipe.servings"
                    label="Nombre de portions"
                    type="number"
                    min="1"
                    :rules="[v => !!v && v > 0 || 'Nombre de portions requis']"
                    required
                    class="mb-3"
                  />
                  
                  <v-text-field
                    v-model.number="recipe.preparationTime"
                    label="Temps de préparation (min)"
                    type="number"
                    min="1"
                    :rules="[v => !!v && v > 0 || 'Temps requis']"
                    required
                    class="mb-3"
                  />
                  
                  <v-text-field
                    v-model.number="recipe.cookingTime"
                    label="Temps de cuisson (min)"
                    type="number"
                    min="0"
                    :rules="[v => v >= 0 || 'Temps invalide']"
                    required
                    class="mb-3"
                  />
                  
                  <v-select
                    v-model="recipe.difficulty"
                    :items="difficultyOptions"
                    label="Difficulté"
                    :rules="[v => !!v || 'Difficulté requise']"
                    required
                    class="mb-3"
                  />
                  
                  <v-select
                    v-model="recipe.category"
                    :items="categoryOptions"
                    label="Catégorie"
                    :rules="[v => !!v || 'Catégorie requise']"
                    required
                    class="mb-3"
                  />
                  
                  <v-switch
                    v-model="recipe.isPublished"
                    label="Publier la recette"
                    color="primary"
                    class="mb-3"
                  />
                </v-card>
                
                <!-- Résumé du prix -->
                <v-card variant="outlined" class="pa-4">
                  <v-card-title class="text-h6 mb-3">Résumé</v-card-title>
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Prix par portion:</div>
                    <div class="text-h6 text-primary">{{ formatPrice(pricePerServing) }}</div>
                  </div>
                  
                  <div class="mb-3">
                    <div class="text-body-2 text-grey">Prix total ({{ recipe.servings }} portion{{ recipe.servings > 1 ? 's' : '' }}):</div>
                    <div class="text-h5 text-primary">{{ formatPrice(recipe.totalPrice) }}</div>
                  </div>
                  
                  <v-divider class="my-3" />
                  
                  <div class="text-caption text-grey">
                    Temps total: {{ totalTime }} minutes
                  </div>
                </v-card>
              </v-col>
            </v-row>
            
            <!-- Actions -->
            <v-row class="mt-6">
              <v-col cols="12" class="d-flex justify-end gap-3">
                <v-btn
                  variant="outlined"
                  @click="$router.go(-1)"
                >
                  Annuler
                </v-btn>
                <v-btn
                  color="primary"
                  type="submit"
                  :loading="loading"
                  :disabled="recipe.ingredients.length === 0"
                >
                  <v-icon left>mdi-content-save</v-icon>
                  Sauvegarder la recette
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
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
const form = ref(null)
const loading = ref(false)

// Données de la recette
const recipe = ref({
  title: '',
  description: '',
  instructions: '',
  servings: 1,
  preparationTime: null,
  cookingTime: 0,
  difficulty: '',
  category: '',
  isPublished: false,
  totalPrice: 0,
  ingredients: []
})

// Options pour les sélecteurs
const difficultyOptions = [
  { title: 'Facile', value: 'FACILE' },
  { title: 'Moyen', value: 'MOYEN' },
  { title: 'Difficile', value: 'DIFFICILE' }
]

const categoryOptions = [
  { title: 'Entrée', value: 'ENTREE' },
  { title: 'Plat principal', value: 'PLAT' },
  { title: 'Dessert', value: 'DESSERT' },
  { title: 'Boisson', value: 'BOISSON' }
]

const productOptions = ref([])

// Computed
const pricePerServing = computed(() => {
  return recipe.value.servings > 0 ? Math.round(recipe.value.totalPrice / recipe.value.servings) : 0
})

const totalTime = computed(() => {
  return (recipe.value.preparationTime || 0) + (recipe.value.cookingTime || 0)
})

// Méthodes
const loadProducts = async () => {
  try {
    const response = await api.get('/api/products?size=1000')
    productOptions.value = response.data.content.map(product => ({
      id: product.id,
      title: product.title,
      price: product.sellingPrice
    }))
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  }
}

const addIngredient = () => {
  recipe.value.ingredients.push({
    productId: null,
    quantity: 1,
    unit: 'kg',
    notes: ''
  })
}

const removeIngredient = (index) => {
  recipe.value.ingredients.splice(index, 1)
  calculateTotalPrice()
}

const onProductSelect = (index) => {
  const ingredient = recipe.value.ingredients[index];
  if (ingredient.productId) {
    // Vérifier si ce produit n'est pas déjà utilisé dans un autre ingrédient
    const isDuplicate = recipe.value.ingredients.some((otherIngredient, otherIndex) => 
      otherIndex !== index && 
      otherIngredient.productId === ingredient.productId
    );
    
    if (isDuplicate) {
      alert('Ce produit est déjà utilisé dans la recette. Veuillez choisir un autre produit.');
      ingredient.productId = null;
      return;
    }
    
    const product = productOptions.value.find(p => p.id === ingredient.productId);
    ingredient.product = product;
  }
  calculateTotalPrice();
}

const calculateTotalPrice = () => {
  let total = 0
  recipe.value.ingredients.forEach(ingredient => {
    if (ingredient.productId && ingredient.quantity) {
      const product = productOptions.value.find(p => p.id === ingredient.productId)
      if (product) {
        total += ingredient.quantity * product.price
      }
    }
  })
  recipe.value.totalPrice = total
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

const saveRecipe = async () => {
  const { valid } = await form.value.validate()
  if (!valid) return
  
  if (recipe.value.ingredients.length === 0) {
    alert('Veuillez ajouter au moins un ingrédient')
    return
  }
  
  loading.value = true
  try {
    // Préparer les données pour l'envoi
    const recipeData = {
      ...recipe.value,
      ingredients: recipe.value.ingredients.map(ingredient => ({
        productId: ingredient.productId,
        quantity: ingredient.quantity,
        unit: ingredient.unit,
        notes: ingredient.notes
      }))
    }
    
    console.log('Données envoyées:', recipeData)
    await api.post('/api/recipes', recipeData)
    router.push('/customer/my-recipes')
  } catch (error) {
    console.error('Erreur lors de la sauvegarde:', error)
    console.error('Détails de l\'erreur:', error.response?.data)
    alert('Erreur lors de la sauvegarde de la recette: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.v-card {
  border-radius: 12px;
}
</style>
