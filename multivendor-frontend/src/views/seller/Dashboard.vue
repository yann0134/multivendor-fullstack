<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Tableau de bord Vendeur</h1>
      </v-col>
    </v-row>

    <!-- Statistiques principales -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits</h3>
          <p class="text-h4">{{ stats.totalProducts }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes</h3>
          <p class="text-h4">{{ stats.totalOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-currency-eur</v-icon>
          <h3 class="text-h6">Chiffre d'affaires</h3>
          <p class="text-h4">{{ stats.totalEarnings }} FCFA</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-star</v-icon>
          <h3 class="text-h6">Note moyenne</h3>
          <p class="text-h4">{{ stats.averageRating }}/5</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Graphiques -->
    <v-row class="mt-6">
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title>Ventes des 30 derniers jours</v-card-title>
          <v-card-text>
            <div class="text-center pa-8">
              <v-icon size="64" color="grey">mdi-chart-line</v-icon>
              <p class="text-grey">Graphique des ventes à implémenter</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Produits populaires</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="product in topProducts"
                :key="product.id"
                :title="product.title"
                :subtitle="`${product.sales} ventes`"
              >
                <template v-slot:prepend>
                  <v-avatar>
                    <v-img :src="product.image" />
                  </v-avatar>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Actions rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Actions rapides</h2>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/seller/products')">
          <v-icon size="48" color="primary" class="mb-2">mdi-plus</v-icon>
          <h3 class="text-h6">Ajouter un produit</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/seller/orders')">
          <v-icon size="48" color="success" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Voir les commandes</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/seller/reports')">
          <v-icon size="48" color="info" class="mb-2">mdi-chart-line</v-icon>
          <h3 class="text-h6">Voir les rapports</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/seller/profile')">
          <v-icon size="48" color="warning" class="mb-2">mdi-account-edit</v-icon>
          <h3 class="text-h6">Modifier le profil</h3>
        </v-card>
      </v-col>
    </v-row>

    <!-- Commandes récentes -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Commandes récentes</h2>
        <v-card>
          <v-list>
            <v-list-item
              v-for="order in recentOrders"
              :key="order.id"
              :title="`Commande #${order.orderId}`"
              :subtitle="`${order.totalSellingPrice} FCFA - ${order.orderStatus}`"
              @click="$router.push(`/seller/orders/${order.id}`)"
            >
              <template v-slot:prepend>
                <v-icon>mdi-package-variant</v-icon>
              </template>
              <template v-slot:append>
                <v-chip :color="getStatusColor(order.orderStatus)">
                  {{ order.orderStatus }}
                </v-chip>
              </template>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrderStore } from '@/stores/orders'

const orderStore = useOrderStore()

const stats = ref({
  totalProducts: 0,
  totalOrders: 0,
  totalEarnings: 0,
  averageRating: 0
})

const topProducts = ref([])
const recentOrders = ref([])

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'CONFIRMED': 'blue',
    'SHIPPED': 'purple',
    'DELIVERED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

onMounted(async () => {
  // Charger les données du dashboard vendeur
  await orderStore.fetchSellerOrders()
  recentOrders.value = orderStore.orders.slice(0, 5)
  
  // Calculer les statistiques
  stats.value.totalOrders = orderStore.orders.length
  stats.value.totalEarnings = orderStore.orders.reduce((sum, order) => sum + order.totalSellingPrice, 0)
})
</script>
