<template>
  <div class="home-page">
    <!-- Hero Section Agricole -->
    <section class="hero-section">
      <v-container fluid class="hero-container">
        <v-row align="center" justify="center" class="fill-height">
          <v-col cols="12" md="8" class="text-center">
            <h1 class="hero-title">
              🌱 AgriMarket
            </h1>
            <h2 class="hero-subtitle">
              La plateforme qui connecte fermiers et consommateurs
            </h2>
            <p class="hero-description">
              Découvrez les meilleurs produits agricoles locaux, frais et bio directement des fermiers de votre région
            </p>
            <div class="hero-actions">
              <v-btn 
                color="primary" 
                size="large" 
                class="mr-4"
                @click="goToProducts"
              >
                🛒 Voir les Produits
              </v-btn>
              <v-btn 
                variant="outlined" 
                color="white" 
                size="large"
                @click="goToCategories"
              >
                🏷️ Par Catégorie
              </v-btn>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Catégories Principales -->
    <section class="categories-section">
      <v-container>
        <h2 class="section-title text-center mb-8">🌾 Nos Catégories de Produits</h2>
        <v-row>
          <v-col cols="12" md="6" v-for="category in categories" :key="category.id">
            <v-card 
              class="category-card pa-6" 
              :style="{ borderLeft: `6px solid ${category.color}` }"
              @click="goToCategory(category)"
              hover
            >
              <div class="d-flex align-center">
                <v-icon :color="category.color" size="64" class="mr-6">{{ category.icon }}</v-icon>
                <div>
                  <h3 class="text-h5 mb-2">{{ category.name }}</h3>
                  <p class="text-body-1 text-grey">{{ category.description }}</p>
                  <v-chip :color="category.color" variant="tonal" class="mt-2">
                    {{ getCategoryCount(category) }} produits
                  </v-chip>
                </div>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Produits en Vedette -->
    <section class="featured-products-section">
      <v-container>
        <h2 class="section-title text-center mb-8">⭐ Produits en Vedette</h2>
        <v-row v-if="featuredProducts.length > 0">
          <v-col cols="12" sm="6" md="3" v-for="product in featuredProducts" :key="product.id">
            <ProductCard :product="product" />
          </v-col>
        </v-row>
        <div v-else class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" />
          <p class="mt-4">Chargement des produits...</p>
        </div>
      </v-container>
    </section>

    <!-- Nouveaux Produits -->
    <section class="new-products-section">
      <v-container>
        <h2 class="section-title text-center mb-8">🆕 Nouveaux Produits</h2>
        <v-row v-if="newProducts.length > 0">
          <v-col cols="12" sm="6" md="3" v-for="product in newProducts" :key="product.id">
            <ProductCard :product="product" />
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Fonctionnalités de la Plateforme -->
    <section class="features-section">
      <v-container>
        <h2 class="section-title text-center mb-8">🚀 Pourquoi Choisir AgriMarket ?</h2>
        <v-row>
          <v-col cols="12" md="4" v-for="feature in features" :key="feature.title">
            <v-card class="feature-card pa-6 text-center" hover>
              <v-icon :color="feature.color" size="48" class="mb-4">{{ feature.icon }}</v-icon>
              <h3 class="text-h6 mb-3">{{ feature.title }}</h3>
              <p class="text-body-2 text-grey">{{ feature.description }}</p>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Statistiques -->
    <section class="stats-section">
      <v-container>
        <v-row>
          <v-col cols="12" md="3" v-for="stat in stats" :key="stat.label">
            <div class="text-center">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Call to Action -->
    <section class="cta-section">
      <v-container>
        <v-card color="primary" dark class="pa-8 text-center">
          <h2 class="text-h4 mb-4">🌱 Rejoignez la Révolution Agricole</h2>
          <p class="text-h6 mb-6">Connectez-vous avec les meilleurs producteurs locaux</p>
          <v-btn 
            color="white" 
            size="large"
            @click="goToRegister"
          >
            Commencer Maintenant
          </v-btn>
        </v-card>
      </v-container>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '@/components/customer/ProductCard.vue'

const router = useRouter()

// Données réactives
const categories = ref([])
const featuredProducts = ref([])
const newProducts = ref([])
const loading = ref(false)

// Fonctionnalités de la plateforme
const features = ref([
  {
    icon: 'mdi-leaf',
    title: 'Produits Bio & Locaux',
    description: 'Découvrez des produits frais, bio et locaux directement des fermiers',
    color: 'green'
  },
  {
    icon: 'mdi-truck-delivery',
    title: 'Livraison Rapide',
    description: 'Recevez vos commandes rapidement avec notre réseau de livreurs',
    color: 'blue'
  },
  {
    icon: 'mdi-shield-check',
    title: 'Qualité Garantie',
    description: 'Tous nos produits sont vérifiés et certifiés par nos experts',
    color: 'orange'
  }
])

// Statistiques
const stats = ref([
  { value: '500+', label: 'Fermiers Partenaires' },
  { value: '2000+', label: 'Produits Disponibles' },
  { value: '10K+', label: 'Clients Satisfaits' },
  { value: '50+', label: 'Villes Couvertes' }
])

// Méthodes
const loadCategories = async () => {
  try {
    const response = await fetch('http://localhost:3026/api/categories')
    categories.value = await response.json()
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  }
}

const loadFeaturedProducts = async () => {
  try {
    const response = await fetch('http://localhost:3026/api/products/featured')
    featuredProducts.value = await response.json()
  } catch (error) {
    console.error('Erreur lors du chargement des produits vedette:', error)
  }
}

const loadNewProducts = async () => {
  try {
    const response = await fetch('http://localhost:3026/api/products/new')
    newProducts.value = await response.json()
  } catch (error) {
    console.error('Erreur lors du chargement des nouveaux produits:', error)
  }
}

const getCategoryCount = (category) => {
  // Simuler un nombre de produits par catégorie
  return Math.floor(Math.random() * 50) + 10
}

const goToProducts = () => {
  router.push('/customer/products')
}

const goToCategories = () => {
  router.push('/customer/products')
}

const goToCategory = (category) => {
  router.push(`/customer/products?category=${category.id}`)
}

const goToRegister = () => {
  router.push('/register')
}

// Initialisation
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadCategories(),
      loadFeaturedProducts(),
      loadNewProducts()
    ])
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);
  color: white;
  min-height: 70vh;
  display: flex;
  align-items: center;
}

.hero-container {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.9), rgba(46, 125, 50, 0.9));
}

.hero-title {
  font-size: 4rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.hero-subtitle {
  font-size: 2rem;
  font-weight: 300;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.hero-description {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.8;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  margin-top: 2rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2E7D32;
  margin-bottom: 2rem;
}

.categories-section {
  padding: 4rem 0;
  background-color: #f8f9fa;
}

.category-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.featured-products-section {
  padding: 4rem 0;
}

.new-products-section {
  padding: 4rem 0;
  background-color: #f8f9fa;
}

.features-section {
  padding: 4rem 0;
}

.feature-card {
  height: 100%;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.stats-section {
  padding: 3rem 0;
  background: linear-gradient(135deg, #4CAF50, #2E7D32);
  color: white;
}

.stat-number {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1.2rem;
  opacity: 0.9;
}

.cta-section {
  padding: 4rem 0;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.5rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
}
</style>