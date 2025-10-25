<template>
  <v-container class="py-8">
    <v-row>
      <v-col cols="12">
        <!-- En-tête -->
        <div class="d-flex justify-space-between align-center mb-6">
          <div>
            <h1 class="text-h4 mb-2">
              <v-icon class="mr-3" color="primary">mdi-calendar-clock</v-icon>
              Planning de Repas
            </h1>
            <p class="text-grey">Planifiez vos repas et calculez automatiquement vos courses</p>
          </div>
          <v-btn
            color="primary"
            size="large"
            @click="showCreateDialog = true"
          >
            <v-icon left>mdi-plus</v-icon>
            Nouveau Planning
          </v-btn>
        </div>

        <!-- Statistiques -->
        <v-row class="mb-6">
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="primary" class="mb-2">mdi-calendar</v-icon>
              <div class="text-h4">{{ stats.totalPlans }}</div>
              <div class="text-body-2 text-grey">Plannings créés</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="success" class="mb-2">mdi-food</v-icon>
              <div class="text-h4">{{ stats.totalMeals }}</div>
              <div class="text-body-2 text-grey">Repas planifiés</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="info" class="mb-2">mdi-currency-usd</v-icon>
              <div class="text-h4">{{ formatPrice(stats.totalSpent) }}</div>
              <div class="text-body-2 text-grey">Total dépensé</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="3">
            <v-card class="pa-4 text-center">
              <v-icon size="48" color="warning" class="mb-2">mdi-account-group</v-icon>
              <div class="text-h4">{{ stats.avgServings }}</div>
              <div class="text-body-2 text-grey">Portions moyennes</div>
            </v-card>
          </v-col>
        </v-row>

        <!-- Liste des plannings -->
        <v-card class="pa-4">
          <v-card-title class="text-h5 mb-4">Mes Plannings de Repas</v-card-title>
          
          <v-data-table
            :headers="headers"
            :items="mealPlans"
            :loading="loading"
            :items-per-page="pagination.size"
            :page="pagination.page + 1"
            :items-length="totalElements"
            item-value="id"
            class="elevation-1"
            @update:options="updatePagination"
          >
            <template v-slot:item.name="{ item }">
              <router-link :to="`/customer/meal-plans/${item.id}`" class="text-primary font-weight-bold">
                {{ item.name }}
              </router-link>
            </template>
            <template v-slot:item.totalDays="{ item }">
              {{ item.totalDays }} jour{{ item.totalDays > 1 ? 's' : '' }}
            </template>
            <template v-slot:item.totalPrice="{ item }">
              {{ formatPrice(item.totalPrice) }}
            </template>
            <template v-slot:item.mealType="{ item }">
              <v-chip :color="getMealTypeColor(item.mealType)" label>
                {{ getMealTypeLabel(item.mealType) }}
              </v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon
                variant="text"
                color="info"
                size="small"
                @click="viewMealPlan(item.id)"
              >
                <v-icon>mdi-eye</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                color="success"
                size="small"
                @click="addToCart(item.id)"
              >
                <v-icon>mdi-cart-plus</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                color="red"
                size="small"
                @click="confirmDelete(item.id)"
              >
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </template>
            <template v-slot:no-data>
              <v-alert :value="true" color="info" icon="mdi-information" outlined>
                Vous n'avez pas encore créé de planning de repas.
                <v-btn class="ml-3" color="primary" @click="showCreateDialog = true">Créer un planning</v-btn>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de création -->
    <v-dialog v-model="showCreateDialog" max-width="800" persistent>
      <v-card>
        <v-card-title class="text-h5">
          <v-icon class="mr-3" color="primary">mdi-calendar-plus</v-icon>
          Créer un Planning de Repas
        </v-card-title>

        <v-card-text>
          <v-form ref="form" @submit.prevent="createMealPlan">
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newMealPlan.name"
                  label="Nom du planning"
                  :rules="[v => !!v || 'Le nom est requis']"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-select
                  v-model="newMealPlan.mealType"
                  :items="mealTypes"
                  label="Type de repas"
                  :rules="[v => !!v || 'Le type de repas est requis']"
                  required
                />
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="newMealPlan.description"
                  label="Description"
                  placeholder="Décrivez votre planning..."
                  rows="3"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="newMealPlan.numberOfDays"
                  label="Nombre de jours"
                  type="number"
                  :rules="[v => (v && v > 0) || 'Minimum 1 jour']"
                  required
                  min="1"
                  hint="Le planning commencera à partir d'aujourd'hui"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="newMealPlan.servingsPerMeal"
                  label="Portions par repas"
                  type="number"
                  :rules="[v => (v && v > 0) || 'Minimum 1 portion']"
                  required
                  min="1"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="newMealPlan.minPricePerRecipe"
                  label="Prix minimum par recette (FCFA)"
                  type="number"
                  min="0"
                  hint="Laissez vide pour aucun minimum"
                />
              </v-col>
              <v-col cols="12">
                <v-btn
                  color="success"
                  variant="outlined"
                  block
                  size="large"
                  @click="generateMealPlan"
                  :loading="creating"
                  :disabled="creating"
                >
                  <v-icon left>mdi-auto-fix</v-icon>
                  Générer le Planning
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="cancelCreate">Annuler</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de confirmation de suppression -->
    <v-dialog v-model="deleteDialog" max-width="500">
      <v-card>
        <v-card-title class="headline">Confirmer la suppression</v-card-title>
        <v-card-text>
          Êtes-vous sûr de vouloir supprimer ce planning de repas ? Cette action est irréversible.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="deleteDialog = false">Annuler</v-btn>
          <v-btn color="red" variant="flat" @click="deleteMealPlan">Supprimer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

// État
const loading = ref(false)
const creating = ref(false)
const mealPlans = ref([])
const stats = ref({
  totalPlans: 0,
  totalMeals: 0,
  totalSpent: 0,
  avgServings: 0
})
const pagination = ref({
  page: 0,
  size: 10
})
const totalElements = ref(0)
const showCreateDialog = ref(false)
const deleteDialog = ref(false)
const mealPlanToDelete = ref(null)
const autoGenerate = ref(true)
const form = ref(null)

// Nouveau planning
const newMealPlan = ref({
  name: '',
  description: '',
  numberOfDays: 7,
  servingsPerMeal: 1,
  minPricePerRecipe: 0, // 0 pour aucun minimum
  mealType: ''
})

// Types de repas
const mealTypes = [
  { title: 'Petit-déjeuner', value: 'petit-dejeuner' },
  { title: 'Déjeuner', value: 'dejeuner' },
  { title: 'Dîner', value: 'diner' },
  { title: 'Snack', value: 'snack' }
]

// Headers pour la table
const headers = [
  { title: 'Nom', key: 'name' },
  { title: 'Nombre de jours', key: 'totalDays' },
  { title: 'Type', key: 'mealType' },
  { title: 'Portions/repas', key: 'servingsPerMeal' },
  { title: 'Prix total', key: 'totalPrice' },
  { title: 'Actions', key: 'actions', sortable: false }
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
  return new Date(date).toLocaleDateString('fr-FR')
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

// Méthodes
const loadMealPlans = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('jwt_token')
    const response = await api.get('/api/meal-plans/my-plans', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        page: pagination.value.page,
        size: pagination.value.size
      }
    })
    mealPlans.value = response.data.content
    totalElements.value = response.data.totalElements
    
    // Mettre à jour les statistiques après le chargement des plannings
    await loadStats()
  } catch (error) {
    console.error('Erreur lors du chargement des plannings:', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // Calculer les statistiques à partir des plannings
    const totalPlans = mealPlans.value.length
    const totalMeals = mealPlans.value.reduce((sum, plan) => sum + (plan.totalDays * plan.servingsPerMeal), 0)
    const totalSpent = mealPlans.value.reduce((sum, plan) => sum + plan.totalPrice, 0)
    const avgServings = totalPlans > 0 ? Math.round(totalMeals / totalPlans) : 0

    console.log('📊 Calcul des statistiques:')
    console.log('- Plannings:', mealPlans.value.length)
    console.log('- Total plans:', totalPlans)
    console.log('- Total meals:', totalMeals)
    console.log('- Total spent:', totalSpent)
    console.log('- Avg servings:', avgServings)

    stats.value = {
      totalPlans,
      totalMeals,
      totalSpent,
      avgServings
    }
    
    console.log('📊 Statistiques mises à jour:', stats.value)
  } catch (error) {
    console.error('Erreur lors du calcul des statistiques:', error)
  }
}

const createMealPlan = async () => {
  const { valid } = await form.value.validate()
  if (!valid) return

  creating.value = true
  try {
    const token = localStorage.getItem('jwt_token')
    
    // Création manuelle
    await api.post('/api/meal-plans', newMealPlan.value, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    cancelCreate()
    await loadMealPlans()
    await loadStats()
  } catch (error) {
    console.error('Erreur lors de la création:', error)
    alert('Erreur lors de la création du planning: ' + (error.response?.data?.message || error.message))
  } finally {
    creating.value = false
  }
}


const generateMealPlan = async () => {
  console.log('🔄 Début de la génération du planning')
  console.log('📋 Données du planning:', newMealPlan.value)
  
  if (!form.value) {
    console.error('❌ Form ref non trouvé')
    alert('Erreur: Formulaire non initialisé')
    return
  }

  const { valid } = await form.value.validate()
  console.log('✅ Validation du formulaire:', valid)
  
  if (!valid) {
    console.log('❌ Validation échouée')
    alert('Veuillez remplir tous les champs requis')
    return
  }

  creating.value = true
  console.log('🚀 Envoi de la requête...')
  
  try {
    const token = localStorage.getItem('jwt_token')
    
    if (!token) {
      alert('Vous devez être connecté pour générer un planning')
      return
    }
    
    console.log('🔑 Token JWT trouvé:', token.substring(0, 20) + '...')
    console.log('📤 Données envoyées:', JSON.stringify(newMealPlan.value, null, 2))
    
    // Génération automatique
    const response = await api.post('/api/meal-plans/generate', newMealPlan.value, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    console.log('✅ Planning généré avec succès:', response.data)
    
    cancelCreate()
    await loadMealPlans()
    await loadStats()
    
    // Rediriger vers le planning généré
    router.push(`/customer/meal-plans/${response.data.id}`)
  } catch (error) {
    console.error('❌ Erreur lors de la génération:', error)
    console.error('📊 Détails de l\'erreur:', error.response?.data)
    alert('Erreur lors de la génération du planning: ' + (error.response?.data?.message || error.message))
  } finally {
    creating.value = false
  }
}

const cancelCreate = () => {
  showCreateDialog.value = false
  newMealPlan.value = {
    name: '',
    description: '',
    numberOfDays: 7,
    servingsPerMeal: 1,
    minPricePerRecipe: 0,
    mealType: ''
  }
  autoGenerate.value = true
}

const viewMealPlan = (id) => {
  router.push(`/customer/meal-plans/${id}`)
}

const addToCart = async (id) => {
  try {
    const token = localStorage.getItem('jwt_token')
    await api.post(`/api/meal-plans/${id}/add-to-cart`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    alert('Produits ajoutés au panier avec succès!')
  } catch (error) {
    console.error('Erreur lors de l\'ajout au panier:', error)
    alert('Erreur lors de l\'ajout au panier: ' + (error.response?.data?.message || error.message))
  }
}

const confirmDelete = (id) => {
  mealPlanToDelete.value = id
  deleteDialog.value = true
}

const deleteMealPlan = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    await api.delete(`/api/meal-plans/${mealPlanToDelete.value}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    deleteDialog.value = false
    await loadMealPlans()
    await loadStats()
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
    alert('Erreur lors de la suppression: ' + (error.response?.data?.message || error.message))
  }
}

const updatePagination = async (options) => {
  pagination.value.page = options.page - 1
  pagination.value.size = options.itemsPerPage
  await loadMealPlans()
}

onMounted(async () => {
  await loadMealPlans()
})
</script>

<style scoped>
/* Styles spécifiques si nécessaire */
</style>
