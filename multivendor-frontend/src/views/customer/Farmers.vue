<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">👨‍🌾 Nos Agriculteurs</h1>
        <p class="text-body-1 text-grey mb-6">
          Découvrez les producteurs locaux qui fournissent nos produits frais et de qualité
        </p>
      </v-col>
    </v-row>

    <!-- Filtres -->
    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <v-text-field
          v-model="searchQuery"
          label="Rechercher un agriculteur..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          @input="searchFarmers"
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-select
          v-model="selectedType"
          label="Type de production"
          :items="typeOptions"
          variant="outlined"
          @update:model-value="filterFarmers"
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-select
          v-model="sortBy"
          label="Trier par"
          :items="sortOptions"
          variant="outlined"
          @update:model-value="filterFarmers"
        />
      </v-col>
    </v-row>

    <!-- Liste des agriculteurs -->
    <v-row v-if="loading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64" />
        <p class="mt-4">Chargement des agriculteurs...</p>
      </v-col>
    </v-row>

    <v-row v-else-if="farmers.length === 0">
      <v-col cols="12" class="text-center">
        <v-icon size="120" color="grey">mdi-account-group-outline</v-icon>
        <h2 class="text-h5 mb-4">Aucun agriculteur trouvé</h2>
        <p class="text-grey mb-6">Essayez de modifier vos critères de recherche</p>
        <v-btn color="primary" @click="clearFilters">
          Effacer les filtres
        </v-btn>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col 
        v-for="farmer in farmers" 
        :key="farmer.id"
        cols="12" 
        sm="6" 
        md="4"
      >
        <v-card class="farmer-card">
          <v-img
            :src="farmer.profileImage || getDefaultFarmerImage()"
            height="200"
            cover
          >
            <div class="farmer-badges">
              <v-chip 
                v-if="farmer.isVerified" 
                color="green" 
                size="small" 
                class="ma-2"
              >
                ✓ Vérifié
              </v-chip>
              <v-chip 
                v-if="farmer.isOrganic" 
                color="green" 
                size="small" 
                class="ma-2"
              >
                🌱 Bio
              </v-chip>
            </div>
          </v-img>

          <v-card-title>{{ farmer.name }}</v-card-title>
          
          <v-card-subtitle>
            📍 {{ farmer.location }}
          </v-card-subtitle>

          <v-card-text>
            <p class="text-body-2 mb-3">{{ farmer.description }}</p>
            
            <div class="farmer-stats">
              <div class="d-flex align-center mb-2">
                <v-icon size="16" class="mr-2">mdi-package-variant</v-icon>
                <span class="text-caption">{{ farmer.productCount }} produits</span>
              </div>
              
              <div class="d-flex align-center mb-2">
                <v-icon size="16" class="mr-2">mdi-star</v-icon>
                <span class="text-caption">{{ farmer.rating || 'Nouveau' }} ({{ farmer.reviewCount || 0 }} avis)</span>
              </div>
              
              <div class="d-flex align-center mb-2">
                <v-icon size="16" class="mr-2">mdi-calendar</v-icon>
                <span class="text-caption">Membre depuis {{ formatDate(farmer.joinDate) }}</span>
              </div>
            </div>

            <div v-if="farmer.specialties && farmer.specialties.length > 0" class="mt-3">
              <h4 class="text-subtitle-2 mb-2">Spécialités</h4>
              <div class="d-flex flex-wrap gap-1">
                <v-chip 
                  v-for="specialty in farmer.specialties" 
                  :key="specialty"
                  size="small"
                  color="primary"
                  variant="outlined"
                >
                  {{ specialty }}
                </v-chip>
              </div>
            </div>
          </v-card-text>

          <v-card-actions>
            <v-btn 
              color="primary" 
              variant="outlined"
              @click="viewFarmerProducts(farmer)"
            >
              Voir ses produits
            </v-btn>
            <v-spacer />
            <v-btn 
              icon
              @click="toggleFavorite(farmer)"
            >
              <v-icon :color="farmer.isFavorite ? 'red' : 'grey'">
                {{ farmer.isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}
              </v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Pagination -->
    <v-row v-if="totalPages > 1" class="mt-6">
      <v-col cols="12" class="text-center">
        <v-pagination
          v-model="currentPage"
          :length="totalPages"
          @update:model-value="loadPage"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'

const router = useRouter()
const productStore = useProductStore()

const farmers = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref(null)
const sortBy = ref('name')
const currentPage = ref(1)
const totalPages = ref(1)

const typeOptions = ref([
  { title: 'Tous les types', value: null },
  { title: '🌱 Production Végétale', value: 'VEGETAL' },
  { title: '🐄 Production Animale', value: 'ANIMAL' }
])

const sortOptions = ref([
  { title: 'Nom A-Z', value: 'name' },
  { title: 'Note décroissante', value: 'rating' },
  { title: 'Plus récent', value: 'joinDate' },
  { title: 'Plus de produits', value: 'productCount' }
])

const getDefaultFarmerImage = () => {
  return 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=300&fit=crop'
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long'
  })
}

const searchFarmers = () => {
  // Implémenter la recherche
  filterFarmers()
}

const filterFarmers = () => {
  // Implémenter le filtrage
  loadFarmers()
}

const loadFarmers = async () => {
  loading.value = true
  try {
    // Simuler le chargement des agriculteurs
    // En réalité, vous appelleriez une API
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Données simulées
    farmers.value = [
      {
        id: 1,
        name: 'Jean Dupont',
        location: 'Bouaké, Côte d\'Ivoire',
        description: 'Agriculteur passionné spécialisé dans la culture de légumes bio.',
        profileImage: null,
        isVerified: true,
        isOrganic: true,
        productCount: 15,
        rating: 4.8,
        reviewCount: 23,
        joinDate: '2023-01-15',
        specialties: ['Tomates', 'Carottes', 'Salades'],
        isFavorite: false
      },
      {
        id: 2,
        name: 'Marie Koné',
        location: 'Yamoussoukro, Côte d\'Ivoire',
        description: 'Productrice de fruits tropicaux et d\'épices locales.',
        profileImage: null,
        isVerified: true,
        isOrganic: false,
        productCount: 8,
        rating: 4.5,
        reviewCount: 12,
        joinDate: '2023-03-20',
        specialties: ['Mangues', 'Ananas', 'Gingembre'],
        isFavorite: true
      }
    ]
  } catch (error) {
    console.error('Erreur lors du chargement des agriculteurs:', error)
  } finally {
    loading.value = false
  }
}

const viewFarmerProducts = (farmer) => {
  router.push(`/customer/products?farmer=${farmer.id}`)
}

const toggleFavorite = (farmer) => {
  farmer.isFavorite = !farmer.isFavorite
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedType.value = null
  sortBy.value = 'name'
  loadFarmers()
}

const loadPage = (page) => {
  currentPage.value = page
  loadFarmers()
}

onMounted(() => {
  loadFarmers()
})
</script>

<style scoped>
.farmer-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.farmer-badges {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.farmer-stats {
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 8px;
}
</style>