<template>
  <v-container>
    <!-- En-tête avec message de bienvenue -->
    <v-row>
      <v-col cols="12">
        <v-card color="primary" dark class="pa-6 mb-6">
          <h1 class="text-h4 mb-2">🌱 Bienvenue sur AgriMarket</h1>
          <p class="text-h6">Découvrez les meilleurs produits agricoles locaux, frais et bio</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Catégories principales -->
    <v-row>
      <v-col cols="12">
        <h2 class="text-h5 mb-4">🏷️ Nos Catégories</h2>
      </v-col>
      <v-col cols="12" md="6" v-for="category in categories" :key="category.id">
        <v-card 
          class="category-card pa-4" 
          :style="{ borderLeft: `4px solid ${category.color}` }"
          @click="goToCategory(category)"
        >
          <div class="d-flex align-center">
            <v-icon :color="category.color" size="48" class="mr-4">{{ category.icon }}</v-icon>
            <div>
              <h3 class="text-h6">{{ category.name }}</h3>
              <p class="text-body-2 text-grey">{{ category.description }}</p>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Produits en vedette -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">⭐ Produits en Vedette</h2>
        <v-row>
          <v-col cols="12" sm="6" md="3" v-for="product in featuredProducts" :key="product.id">
            <ProductCard :product="product" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- Nouveaux produits -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">🆕 Nouveaux Produits</h2>
        <v-row>
          <v-col cols="12" sm="6" md="3" v-for="product in newProducts" :key="product.id">
            <ProductCard :product="product" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">📊 Mes Statistiques</h2>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits vus</h3>
          <p class="text-h4">{{ stats.productsViewed }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-cart</v-icon>
          <h3 class="text-h6">Articles en panier</h3>
          <p class="text-h4">{{ cartStore.totalItems }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes</h3>
          <p class="text-h4">{{ stats.totalOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-star</v-icon>
          <h3 class="text-h6">Avis donnés</h3>
          <p class="text-h4">{{ stats.reviewsGiven }}</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Actions rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Actions rapides</h2>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/customer/products')">
          <v-icon size="48" color="primary" class="mb-2">mdi-magnify</v-icon>
          <h3 class="text-h6">Rechercher des produits</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/customer/cart')">
          <v-icon size="48" color="success" class="mb-2">mdi-cart</v-icon>
          <h3 class="text-h6">Voir mon panier</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/customer/orders')">
          <v-icon size="48" color="info" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Mes commandes</h3>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4" @click="$router.push('/customer/profile')">
          <v-icon size="48" color="warning" class="mb-2">mdi-account-edit</v-icon>
          <h3 class="text-h6">Mon profil</h3>
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
              :subtitle="`${order.totalSellingPrice}€ - ${order.orderStatus}`"
              @click="$router.push(`/customer/order/${order.id}`)"
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
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useOrderStore } from '@/stores/orders'
import { useProductStore } from '@/stores/products'
import ProductCard from '@/components/customer/ProductCard.vue'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()
const productStore = useProductStore()

const categories = ref([])
const featuredProducts = ref([])
const newProducts = ref([])

const stats = ref({
  productsViewed: 0,
  totalOrders: 0,
  reviewsGiven: 0
})

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

const goToCategory = (category) => {
  router.push(`/customer/products?category=${category.id}`)
}

onMounted(async () => {
  try {
    // Charger les catégories
    const categoriesResponse = await fetch('http://localhost:3026/api/categories')
    categories.value = await categoriesResponse.json()
    
    // Charger les produits en vedette
    const featuredResponse = await fetch('http://localhost:3026/api/products/featured')
    featuredProducts.value = await featuredResponse.json()
    
    // Charger les nouveaux produits
    const newResponse = await fetch('http://localhost:3026/api/products/new')
    newProducts.value = await newResponse.json()
    
    // Charger les données du dashboard
    await orderStore.fetchUserOrders()
    recentOrders.value = orderStore.orders.slice(0, 5)
    
    // Calculer les statistiques
    stats.value.totalOrders = orderStore.orders.length
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error)
  }
})
</script>

<style scoped>
.category-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>
