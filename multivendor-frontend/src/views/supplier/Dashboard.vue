<template>
  <v-container>
    <!-- En-tête du tableau de bord -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary" size="large">mdi-view-dashboard</v-icon>
            <span>📊 Tableau de Bord</span>
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              variant="outlined"
              @click="refreshDashboard"
              :loading="loading"
            >
              <v-icon left>mdi-refresh</v-icon>
              Actualiser
            </v-btn>
          </v-card-title>
          <v-card-subtitle>
            Vue d'ensemble de vos produits, revenus et performances
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques générales -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center" color="blue" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="blue">mdi-package-variant</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.totalProducts }}</div>
            <div class="text-subtitle-1">Total Produits</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center" color="green" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="green">mdi-check-circle</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.approvedProducts }}</div>
            <div class="text-subtitle-1">Approuvés</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center" color="orange" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="orange">mdi-clock</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.pendingProducts }}</div>
            <div class="text-subtitle-1">En Attente</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center" color="red" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="red">mdi-close-circle</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.rejectedProducts }}</div>
            <div class="text-subtitle-1">Rejetés</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques de livraison -->
    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card class="text-center" color="purple" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="purple">mdi-truck</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.shippedProducts }}</div>
            <div class="text-subtitle-1">Expédiés</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card class="text-center" color="success" variant="tonal">
          <v-card-text>
            <v-icon size="48" color="success">mdi-truck-check</v-icon>
            <div class="text-h4 font-weight-bold mt-2">{{ stats.deliveredProducts }}</div>
            <div class="text-subtitle-1">Livrés</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Revenus et finances -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="success">mdi-currency-usd</v-icon>
            <span>💰 Revenus et Finances</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-card class="text-center" color="success" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="success">mdi-wallet</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(stats.totalRevenue) }}</div>
                    <div class="text-subtitle-1">Revenus Totaux</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="4">
                <v-card class="text-center" color="green" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="green">mdi-check-circle</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(stats.receivedAmount) }}</div>
                    <div class="text-subtitle-1">Montant Reçu</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="4">
                <v-card class="text-center" color="orange" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="orange">mdi-clock</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(stats.pendingAmount) }}</div>
                    <div class="text-subtitle-1">En Attente</div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
            
            <!-- Évolution mensuelle -->
            <v-row class="mt-4">
              <v-col cols="12" md="6">
                <v-card class="text-center" color="blue" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="blue">mdi-calendar-month</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(stats.thisMonthRevenue) }}</div>
                    <div class="text-subtitle-1">Ce Mois</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="6">
                <v-card class="text-center" color="grey" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="grey">mdi-calendar-month-outline</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ formatPrice(stats.lastMonthRevenue) }}</div>
                    <div class="text-subtitle-1">Mois Dernier</div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Performance et analytics -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="purple">mdi-chart-line</v-icon>
            <span>📈 Performance et Analytics</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="3">
                <v-card class="text-center" color="purple" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="purple">mdi-percent</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ stats.completionRate.toFixed(1) }}%</div>
                    <div class="text-subtitle-1">Taux de Complétion</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="3">
                <v-card class="text-center" color="blue" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="blue">mdi-clock-outline</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ stats.averageProcessingTime.toFixed(1) }}h</div>
                    <div class="text-subtitle-1">Temps Moyen</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="3">
                <v-card class="text-center" color="green" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="green">mdi-package-variant-closed</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ stats.completedOrders }}</div>
                    <div class="text-subtitle-1">Commandes Complétées</div>
                  </v-card-text>
                </v-card>
              </v-col>
              
              <v-col cols="12" md="3">
                <v-card class="text-center" color="orange" variant="tonal">
                  <v-card-text>
                    <v-icon size="48" color="orange">mdi-star</v-icon>
                    <div class="text-h4 font-weight-bold mt-2">{{ stats.topSellingProduct }}</div>
                    <div class="text-subtitle-1">Top Produit</div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Informations temporelles -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="info">mdi-information</v-icon>
            <span>ℹ️ Informations</span>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <v-list>
                  <v-list-item>
                    <v-list-item-title>Dernière Activité</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDate(stats.lastActivity) }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>Total Commandes</v-list-item-title>
                    <v-list-item-subtitle>{{ stats.totalOrders }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
              
              <v-col cols="12" md="6">
                <v-list>
                  <v-list-item>
                    <v-list-item-title>Produit le Plus Vendu</v-list-item-title>
                    <v-list-item-subtitle>{{ stats.topSellingProduct }} ({{ stats.topSellingQuantity }} unités)</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>Compte Créé</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDate(stats.accountCreated) }}</v-list-item-subtitle>
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
        color="primary"
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
const stats = ref({
  totalProducts: 0,
  approvedProducts: 0,
  pendingProducts: 0,
  rejectedProducts: 0,
  shippedProducts: 0,
  deliveredProducts: 0,
  totalRevenue: 0,
  receivedAmount: 0,
  pendingAmount: 0,
  thisMonthRevenue: 0,
  lastMonthRevenue: 0,
  averageProcessingTime: 0,
  totalOrders: 0,
  completedOrders: 0,
  completionRate: 0,
  lastActivity: null,
  accountCreated: null,
  topSellingProduct: 'Aucun',
  topSellingQuantity: 0
})

const fetchDashboardStats = async () => {
  loading.value = true
  try {
    console.log('📊 Récupération des statistiques du tableau de bord...')
    const response = await api.get('/api/products/supplier/dashboard/stats')
    console.log('📊 Statistiques reçues:', response.data)
    
    stats.value = response.data
    
    console.log('📊 Tableau de bord mis à jour:')
    console.log('  - Total produits:', stats.value.totalProducts)
    console.log('  - Produits approuvés:', stats.value.approvedProducts)
    console.log('  - Revenus totaux:', stats.value.totalRevenue)
    console.log('  - Montant reçu:', stats.value.receivedAmount)
    
  } catch (error) {
    console.error('❌ Erreur lors du chargement du tableau de bord:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const refreshDashboard = () => {
  fetchDashboardStats()
}

const formatPrice = (price) => {
  if (!price) return '0  FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF'
  }).format(price)
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchDashboardStats()
})
</script>

<style scoped>
.v-card {
  transition: transform 0.2s ease-in-out;
}

.v-card:hover {
  transform: translateY(-2px);
}

.text-h4 {
  font-size: 2rem !important;
}

.text-subtitle-1 {
  font-size: 0.875rem !important;
}
</style>