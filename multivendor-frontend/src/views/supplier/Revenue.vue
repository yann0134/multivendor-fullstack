<template>
  <v-container>
    <!-- En-tête -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="success" size="large">mdi-currency-usd</v-icon>
            <span>💰 Revenus et Finances</span>
            <v-spacer></v-spacer>
            <v-btn
              color="success"
              variant="outlined"
              @click="refreshRevenue"
              :loading="loading"
            >
              <v-icon left>mdi-refresh</v-icon>
              Actualiser
            </v-btn>
          </v-card-title>
          <v-card-subtitle>
            Suivi détaillé de vos revenus et paiements
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques de revenus -->
    <v-row>
      <v-col cols="12" md="4">
        <v-card class="text-center" color="success" variant="tonal">
          <v-card-text>
            <v-icon size="64" color="success">mdi-wallet</v-icon>
            <div class="text-h3 font-weight-bold mt-2">{{ formatPrice(revenueData.totalRevenue) }}</div>
            <div class="text-h6">Revenus Totaux</div>
            <div class="text-caption mt-2">Basés sur les prix de vente</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card class="text-center" color="green" variant="tonal">
          <v-card-text>
            <v-icon size="64" color="green">mdi-check-circle</v-icon>
            <div class="text-h3 font-weight-bold mt-2">{{ formatPrice(revenueData.receivedAmount) }}</div>
            <div class="text-h6">Montant Reçu</div>
            <div class="text-caption mt-2">Produits livrés et reçus</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card class="text-center" color="orange" variant="tonal">
          <v-card-text>
            <v-icon size="64" color="orange">mdi-clock</v-icon>
            <div class="text-h3 font-weight-bold mt-2">{{ formatPrice(revenueData.pendingAmount) }}</div>
            <div class="text-h6">En Attente</div>
            <div class="text-caption mt-2">En cours de traitement</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Évolution mensuelle -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="blue">mdi-chart-line</v-icon>
            <span>📈 Évolution Mensuelle</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <v-card class="text-center" color="blue" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="blue">mdi-calendar-month</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(revenueData.thisMonthRevenue) }}</div>
                    <div class="text-subtitle-1">Ce Mois</div>
                    <div class="text-caption mt-2">{{ getCurrentMonth() }}</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="6">
                <v-card class="text-center" color="grey" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="grey">mdi-calendar-month-outline</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(revenueData.lastMonthRevenue) }}</div>
                    <div class="text-subtitle-1">Mois Dernier</div>
                    <div class="text-caption mt-2">{{ getLastMonth() }}</div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
            
            <!-- Comparaison -->
            <v-row class="mt-4">
              <v-col cols="12">
                <v-card color="info" variant="tonal">
                  <v-card-text>
                    <div class="d-flex align-center">
                      <v-icon class="mr-3" color="info">mdi-trending-up</v-icon>
                      <div>
                        <div class="text-h6">Évolution</div>
                        <div class="text-body-2">
                          {{ getEvolutionText() }}
                        </div>
                      </div>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Détails des revenus -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="purple">mdi-chart-pie</v-icon>
            <span>📊 Répartition des Revenus</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <v-list>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="green">mdi-check-circle</v-icon>
                    </template>
                    <v-list-item-title>Montant Reçu</v-list-item-title>
                    <v-list-item-subtitle>{{ formatPrice(revenueData.receivedAmount) }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="orange">mdi-clock</v-icon>
                    </template>
                    <v-list-item-title>En Attente</v-list-item-title>
                    <v-list-item-subtitle>{{ formatPrice(revenueData.pendingAmount) }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
              
              <v-col cols="12" md="6">
                <v-list>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="blue">mdi-calendar-month</v-icon>
                    </template>
                    <v-list-item-title>Ce Mois</v-list-item-title>
                    <v-list-item-subtitle>{{ formatPrice(revenueData.thisMonthRevenue) }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon color="grey">mdi-calendar-month-outline</v-icon>
                    </template>
                    <v-list-item-title>Mois Dernier</v-list-item-title>
                    <v-list-item-subtitle>{{ formatPrice(revenueData.lastMonthRevenue) }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Message de chargement -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular
        color="success"
        indeterminate
        size="64"
      ></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const loading = ref(false)
const revenueData = ref({
  totalRevenue: 0,
  receivedAmount: 0,
  pendingAmount: 0,
  thisMonthRevenue: 0,
  lastMonthRevenue: 0
})

const fetchRevenueData = async () => {
  loading.value = true
  try {
    console.log('💰 Récupération des données de revenus...')
    const response = await api.get('/api/products/supplier/dashboard/revenue')
    console.log('💰 Données de revenus reçues:', response.data)
    
    revenueData.value = response.data
    
    console.log('💰 Revenus mis à jour:')
    console.log('  - Revenus totaux:', revenueData.value.totalRevenue)
    console.log('  - Montant reçu:', revenueData.value.receivedAmount)
    console.log('  - Montant en attente:', revenueData.value.pendingAmount)
    
  } catch (error) {
    console.error('❌ Erreur lors du chargement des revenus:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const refreshRevenue = () => {
  fetchRevenueData()
}

const formatPrice = (price) => {
  if (!price) return '0  FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF'
  }).format(price)
}

const getCurrentMonth = () => {
  return new Date().toLocaleDateString('fr-FR', { 
    year: 'numeric', 
    month: 'long' 
  })
}

const getLastMonth = () => {
  const lastMonth = new Date()
  lastMonth.setMonth(lastMonth.getMonth() - 1)
  return lastMonth.toLocaleDateString('fr-FR', { 
    year: 'numeric', 
    month: 'long' 
  })
}

const getEvolutionText = () => {
  const thisMonth = revenueData.value.thisMonthRevenue || 0
  const lastMonth = revenueData.value.lastMonthRevenue || 0
  
  if (lastMonth === 0) {
    return 'Premier mois d\'activité'
  }
  
  const evolution = ((thisMonth - lastMonth) / lastMonth) * 100
  
  if (evolution > 0) {
    return `+${evolution.toFixed(1)}% par rapport au mois dernier`
  } else if (evolution < 0) {
    return `${evolution.toFixed(1)}% par rapport au mois dernier`
  } else {
    return 'Stable par rapport au mois dernier'
  }
}

onMounted(() => {
  fetchRevenueData()
})
</script>

<style scoped>
.v-card {
  transition: transform 0.2s ease-in-out;
}

.v-card:hover {
  transform: translateY(-2px);
}

.text-h3 {
  font-size: 2.5rem !important;
}

.text-h4 {
  font-size: 2rem !important;
}

.text-h6 {
  font-size: 1.25rem !important;
}
</style>
