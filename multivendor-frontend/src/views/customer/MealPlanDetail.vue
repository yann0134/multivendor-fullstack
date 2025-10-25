<template>
  <v-container class="py-8" v-if="mealPlan">
    <v-row>
      <v-col cols="12">
        <!-- En-tête -->
        <div class="d-flex justify-space-between align-center mb-6">
          <div>
            <h1 class="text-h4 mb-2">
              <v-icon class="mr-3" color="primary">mdi-calendar-clock</v-icon>
              {{ mealPlan.name }}
            </h1>
            <p class="text-grey">{{ mealPlan.description }}</p>
          </div>
          <div class="d-flex gap-3">
            <v-btn
              color="success"
              size="large"
              @click="addToCart"
              :loading="addingToCart"
            >
              <v-icon left>mdi-cart-plus</v-icon>
              Ajouter au Panier
            </v-btn>
            <v-btn
              color="info"
              variant="outlined"
              size="large"
              @click="printMealPlan"
            >
              <v-icon left>mdi-printer</v-icon>
              Imprimer
            </v-btn>
            <v-btn
              color="primary"
              variant="outlined"
              size="large"
              @click="goBack"
            >
              <v-icon left>mdi-arrow-left</v-icon>
              Retour
            </v-btn>
          </div>
        </div>

        <!-- Informations du planning -->
        <v-row class="mb-6">
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="primary" class="mb-2">mdi-calendar</v-icon>
              <div class="text-h4">{{ mealPlan.totalDays }}</div>
              <div class="text-body-2 text-grey">Jours</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="success" class="mb-2">mdi-food</v-icon>
              <div class="text-h4">{{ mealPlan.items?.length || 0 }}</div>
              <div class="text-body-2 text-grey">Repas</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="info" class="mb-2">mdi-account-group</v-icon>
              <div class="text-h4">{{ mealPlan.servingsPerMeal }}</div>
              <div class="text-body-2 text-grey">Portions/repas</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="warning" class="mb-2">mdi-currency-usd</v-icon>
              <div class="text-h4">{{ formatPrice(mealPlan.totalPrice) }}</div>
              <div class="text-body-2 text-grey">Prix total</div>
            </v-card>
          </v-col>
        </v-row>

        <!-- Onglets -->
        <v-tabs v-model="activeTab" class="mb-6">
          <v-tab value="calendar">
            <v-icon class="mr-2">mdi-calendar</v-icon>
            Calendrier
          </v-tab>
          <v-tab value="products">
            <v-icon class="mr-2">mdi-package-variant</v-icon>
            Produits
          </v-tab>
          <v-tab value="recipes">
            <v-icon class="mr-2">mdi-book-open-variant</v-icon>
            Recettes
          </v-tab>
        </v-tabs>

        <!-- Contenu des onglets -->
        <v-window v-model="activeTab">
          <!-- Onglet Calendrier -->
          <v-window-item value="calendar">
            <v-card class="pa-4">
              <v-card-title class="text-h5 mb-4">Planning par Jour</v-card-title>
              
              <v-row>
                <v-col
                  v-for="(day, index) in calendarDays"
                  :key="index"
                  cols="12"
                  md="6"
                  lg="4"
                >
                  <v-card variant="outlined" class="pa-4">
                    <div class="d-flex justify-space-between align-center mb-3">
                      <h3 class="text-h6">Jour {{ index + 1 }}</h3>
                      <v-chip :color="getMealTypeColor(day.mealType)" label>
                        {{ getMealTypeLabel(day.mealType) }}
                      </v-chip>
                    </div>
                    
                    <div v-if="day.recipeTitle" class="mb-3">
                      <h4 class="text-subtitle-1 mb-2">{{ day.recipeTitle }}</h4>
                      <p class="text-body-2 text-grey mb-2">{{ day.recipeDescription }}</p>
                      
                      <!-- Afficher les ingrédients -->
                      <div v-if="day.firstIngredient || day.secondIngredient" class="mb-2">
                        <div class="text-body-2 text-grey">
                          <v-icon size="14" class="mr-1">mdi-food</v-icon>
                          Ingrédients:
                        </div>
                        <div v-if="day.firstIngredient" class="text-body-2 ml-4">
                          • {{ day.firstIngredient }}
                        </div>
                        <div v-if="day.secondIngredient" class="text-body-2 ml-4">
                          • {{ day.secondIngredient }}
                        </div>
                      </div>
                      
                      <div class="d-flex justify-space-between">
                        <span class="text-body-2">
                          <v-icon size="16" class="mr-1">mdi-account-group</v-icon>
                          {{ day.servings }} portions
                        </span>
                        <span class="text-body-2 font-weight-bold">
                          {{ formatPrice(day.priceForServings) }}
                        </span>
                      </div>
                    </div>
                    
                    <div v-else class="text-center text-grey">
                      <v-icon size="48" class="mb-2">mdi-food-off</v-icon>
                      <p>Aucune recette assignée</p>
                    </div>
                  </v-card>
                </v-col>
              </v-row>
            </v-card>
          </v-window-item>

          <!-- Onglet Produits -->
          <v-window-item value="products">
            <v-card class="pa-4">
              <v-card-title class="text-h5 mb-4">Résumé des Produits</v-card-title>
              
              <div v-if="productSummary.groupedProducts?.length > 0">
                <v-data-table
                  :headers="productHeaders"
                  :items="productSummary.groupedProducts"
                  class="elevation-1"
                >
                  <template v-slot:item.productName="{ item }">
                    <div class="d-flex align-center">
                      <v-icon class="mr-2" color="primary">mdi-package-variant</v-icon>
                      {{ item.productName }}
                    </div>
                  </template>
                  <template v-slot:item.totalQuantity="{ item }">
                    <v-chip color="info" label>
                      {{ item.totalQuantity }}
                    </v-chip>
                  </template>
                  <template v-slot:item.unitPrice="{ item }">
                    {{ formatPrice(item.unitPrice) }}
                  </template>
                  <template v-slot:item.totalPrice="{ item }">
                    <span class="font-weight-bold text-primary">
                      {{ formatPrice(item.totalPrice) }}
                    </span>
                  </template>
                </v-data-table>

                <v-divider class="my-4"></v-divider>

                <div class="d-flex justify-space-between align-center">
                  <div class="text-h6">
                    <v-icon class="mr-2" color="primary">mdi-calculator</v-icon>
                    Total des produits
                  </div>
                  <div class="text-h5 font-weight-bold text-primary">
                    {{ formatPrice(productSummary.totalPrice) }}
                  </div>
                </div>
              </div>

              <v-alert v-else color="info" icon="mdi-information" outlined>
                Aucun produit trouvé pour ce planning.
              </v-alert>
            </v-card>
          </v-window-item>

          <!-- Onglet Recettes -->
          <v-window-item value="recipes">
            <v-card class="pa-4">
              <v-card-title class="text-h5 mb-4">Recettes du Planning</v-card-title>
              
              <v-row>
                <v-col
                  v-for="item in mealPlan.items"
                  :key="item.id"
                  cols="12"
                  md="6"
                  lg="4"
                >
                  <v-card variant="outlined" class="pa-4">
                    <div class="d-flex justify-space-between align-center mb-3">
                      <h4 class="text-subtitle-1">{{ item.recipe.title }}</h4>
                      <v-chip :color="getMealTypeColor(item.mealType)" label>
                        {{ getMealTypeLabel(item.mealType) }}
                      </v-chip>
                    </div>
                    
                    <p class="text-body-2 text-grey mb-3">{{ item.recipe.description }}</p>
                    
                    <div class="mb-3">
                      <div class="d-flex justify-space-between mb-1">
                        <span class="text-body-2">Date:</span>
                        <span class="text-body-2 font-weight-bold">{{ formatDate(item.mealDate) }}</span>
                      </div>
                      <div class="d-flex justify-space-between mb-1">
                        <span class="text-body-2">Portions:</span>
                        <span class="text-body-2 font-weight-bold">{{ item.servings }}</span>
                      </div>
                      <div class="d-flex justify-space-between mb-1">
                        <span class="text-body-2">Temps:</span>
                        <span class="text-body-2 font-weight-bold">
                          {{ item.recipe.preparationTime + item.recipe.cookingTime }} min
                        </span>
                      </div>
                      <div class="d-flex justify-space-between">
                        <span class="text-body-2">Prix:</span>
                        <span class="text-body-2 font-weight-bold text-primary">
                          {{ formatPrice(item.priceForServings) }}
                        </span>
                      </div>
                    </div>

                    <v-btn
                      color="primary"
                      variant="outlined"
                      block
                      @click="viewRecipe(item.recipe.id)"
                    >
                      <v-icon left>mdi-eye</v-icon>
                      Voir la recette
                    </v-btn>
                  </v-card>
                </v-col>
              </v-row>
            </v-card>
          </v-window-item>
        </v-window>
      </v-col>
    </v-row>
  </v-container>

  <!-- Loading -->
  <v-container v-else class="py-8">
    <v-row justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
        <p class="mt-4">Chargement du planning...</p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()

// État
const mealPlan = ref(null)
const productSummary = ref({})
const activeTab = ref('calendar')
const addingToCart = ref(false)

// Headers pour le tableau des produits
const productHeaders = [
  { title: 'Produit', key: 'productName' },
  { title: 'Quantité totale', key: 'totalQuantity' },
  { title: 'Prix unitaire', key: 'unitPrice' },
  { title: 'Prix total', key: 'totalPrice' }
]

// Computed properties
const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getMealTypeColor = (type) => {
  const colors = {
    'petit-dejeuner': 'orange',
    'dejeuner': 'green',
    'diner': 'blue',
    'snack': 'purple'
  }
  return colors[type] || 'grey'
}

const getMealTypeLabel = (type) => {
  const labels = {
    'petit-dejeuner': 'Petit-déjeuner',
    'dejeuner': 'Déjeuner',
    'diner': 'Dîner',
    'snack': 'Snack'
  }
  return labels[type] || type
}

const calendarDays = computed(() => {
  if (!mealPlan.value?.items) return []
  
  return mealPlan.value.items.map(item => ({
    date: item.mealDate,
    mealType: item.mealType,
    recipeTitle: item.recipeTitle,
    recipeDescription: item.recipeDescription,
    firstIngredient: item.firstIngredient,
    secondIngredient: item.secondIngredient,
    servings: item.servings,
    priceForServings: item.priceForServings
  }))
})

// Méthodes
const loadMealPlan = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const response = await api.get(`/api/meal-plans/${route.params.id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    mealPlan.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement du planning:', error)
    alert('Erreur lors du chargement du planning: ' + (error.response?.data?.message || error.message))
  }
}

const loadProductSummary = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const response = await api.get(`/api/meal-plans/${route.params.id}/products`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    productSummary.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement du résumé des produits:', error)
  }
}

const addToCart = async () => {
  addingToCart.value = true
  try {
    const token = localStorage.getItem('jwt_token')
    await api.post(`/api/meal-plans/${route.params.id}/add-to-cart`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    alert('Produits ajoutés au panier avec succès!')
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
    alert('Erreur lors de l\'ajout au panier: ' + (error.response?.data?.message || error.message))
  } finally {
    addingToCart.value = false
  }
}

const viewRecipe = (recipeId) => {
  router.push(`/customer/recipes/${recipeId}`)
}

const printMealPlan = () => {
  // Créer une nouvelle fenêtre pour l'impression
  const printWindow = window.open('', '_blank')
  
  // Contenu HTML pour l'impression
  const printContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>Planning de Repas - ${mealPlan.value.name}</title>
      <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }
        .info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }
        .info-card { border: 1px solid #ddd; padding: 15px; text-align: center; }
        .day-section { margin-bottom: 30px; page-break-inside: avoid; }
        .day-header { background-color: #f5f5f5; padding: 10px; font-weight: bold; border-left: 4px solid #2196F3; }
        .recipe-info { padding: 15px; border: 1px solid #ddd; margin-top: 10px; }
        .ingredients-list { margin-top: 10px; }
        .ingredient-item { padding: 5px 0; border-bottom: 1px solid #eee; }
        .products-section { margin-top: 30px; page-break-before: always; }
        .product-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        .product-table th, .product-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        .product-table th { background-color: #f5f5f5; }
        .total-price { font-weight: bold; font-size: 1.2em; margin-top: 20px; text-align: right; }
        @media print { body { margin: 0; } }
      </style>
    </head>
    <body>
      <div class="header">
        <h1>📅 Planning de Repas</h1>
        <h2>${mealPlan.value.name}</h2>
        <p>${mealPlan.value.description}</p>
      </div>

      <div class="info-grid">
        <div class="info-card">
          <h3>📊 Informations</h3>
          <p><strong>Durée:</strong> ${mealPlan.value.totalDays} jour${mealPlan.value.totalDays > 1 ? 's' : ''}</p>
          <p><strong>Portions par repas:</strong> ${mealPlan.value.servingsPerMeal}</p>
          <p><strong>Type de repas:</strong> ${getMealTypeLabel(mealPlan.value.mealType)}</p>
        </div>
        <div class="info-card">
          <h3>💰 Prix</h3>
          <p><strong>Prix total:</strong> ${formatPrice(mealPlan.value.totalPrice)}</p>
          <p><strong>Prix par repas:</strong> ${formatPrice(mealPlan.value.totalPrice / mealPlan.value.items.length)}</p>
        </div>
        <div class="info-card">
          <h3>📅 Période</h3>
          <p><strong>Début:</strong> ${formatDate(mealPlan.value.startDate)}</p>
          <p><strong>Fin:</strong> ${formatDate(mealPlan.value.endDate)}</p>
        </div>
      </div>

      <h2>🍽️ Planning des Repas</h2>
      ${mealPlan.value.items.map((item, index) => `
        <div class="day-section">
          <div class="day-header">
            Jour ${index + 1} - ${formatDate(item.mealDate)} - ${getMealTypeLabel(item.mealType)}
          </div>
          <div class="recipe-info">
            <h3>${item.recipeTitle}</h3>
            <p><strong>Description:</strong> ${item.recipeDescription}</p>
            <p><strong>Portions:</strong> ${item.servings}</p>
            <p><strong>Prix pour ${item.servings} portions:</strong> ${formatPrice(item.priceForServings)}</p>
            ${item.firstIngredient || item.secondIngredient ? `
              <div class="ingredients-list">
                <h4>Ingrédients principaux:</h4>
                ${item.firstIngredient ? `<div class="ingredient-item">• ${item.firstIngredient}</div>` : ''}
                ${item.secondIngredient ? `<div class="ingredient-item">• ${item.secondIngredient}</div>` : ''}
              </div>
            ` : ''}
          </div>
        </div>
      `).join('')}

      ${productSummary.value.groupedProducts && productSummary.value.groupedProducts.length > 0 ? `
        <div class="products-section">
          <h2>🛒 Liste des Produits à Acheter</h2>
          <table class="product-table">
            <thead>
              <tr>
                <th>Produit</th>
                <th>Quantité totale</th>
                <th>Prix unitaire</th>
                <th>Prix total</th>
              </tr>
            </thead>
            <tbody>
              ${productSummary.value.groupedProducts.map(product => `
                <tr>
                  <td>${product.productName}</td>
                  <td>${product.totalQuantity}</td>
                  <td>${formatPrice(product.unitPrice)}</td>
                  <td>${formatPrice(product.totalPrice)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
          <div class="total-price">
            <strong>Total à payer: ${formatPrice(productSummary.value.totalPrice)}</strong>
          </div>
        </div>
      ` : ''}

      <div style="margin-top: 50px; text-align: center; color: #666; font-size: 0.9em;">
        <p>Planning généré le ${new Date().toLocaleDateString('fr-FR')} à ${new Date().toLocaleTimeString('fr-FR')}</p>
      </div>
    </body>
    </html>
  `
  
  // Écrire le contenu et lancer l'impression
  printWindow.document.write(printContent)
  printWindow.document.close()
  
  // Attendre que le contenu soit chargé puis lancer l'impression
  printWindow.onload = () => {
    printWindow.focus()
    printWindow.print()
    printWindow.close()
  }
}

const goBack = () => {
  router.push('/customer/meal-plans')
}

onMounted(async () => {
  await loadMealPlan()
  await loadProductSummary()
})
</script>

<style scoped>
/* Styles spécifiques si nécessaire */
</style>
