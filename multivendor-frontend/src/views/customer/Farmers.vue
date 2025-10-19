<template>
  <v-container>
    <!-- En-tête -->
    <div class="text-center mb-8">
      <h1 class="text-h3 mb-4">👨‍🌾 Nos Fermiers Partenaires</h1>
      <p class="text-h6 text-grey">Découvrez les producteurs locaux qui cultivent avec passion</p>
    </div>

    <!-- Filtres -->
    <v-card class="mb-6">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-filter</v-icon>
        Filtres
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="3">
            <v-text-field
              v-model="searchQuery"
              label="Rechercher un fermier"
              prepend-inner-icon="mdi-magnify"
              @input="filterFarmers"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="selectedRegion"
              label="Région"
              :items="regions"
              clearable
              @update:model-value="filterFarmers"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="selectedSpecialty"
              label="Spécialité"
              :items="specialties"
              clearable
              @update:model-value="filterFarmers"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-checkbox
              v-model="organicOnly"
              label="🌱 Bio uniquement"
              @change="filterFarmers"
            />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Liste des fermiers -->
    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" size="64" />
      <p class="mt-4">Chargement des fermiers...</p>
    </div>

    <div v-else-if="filteredFarmers.length > 0">
      <v-row>
        <v-col 
          cols="12" 
          md="6" 
          lg="4" 
          v-for="farmer in filteredFarmers" 
          :key="farmer.id"
        >
          <v-card class="farmer-card" hover>
            <!-- Image du fermier -->
            <v-img
              :src="farmer.image || 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=200&fit=crop'"
              height="200"
              cover
            >
              <div class="farmer-badges">
                <v-chip 
                  v-if="farmer.isOrganic" 
                  color="green" 
                  size="small"
                  class="ma-2"
                >
                  🌱 Bio
                </v-chip>
                <v-chip 
                  v-if="farmer.isLocal" 
                  color="orange" 
                  size="small"
                  class="ma-2"
                >
                  🏠 Local
                </v-chip>
              </div>
            </v-img>

            <v-card-title class="d-flex align-center">
              <v-avatar :color="farmer.color" class="mr-3">
                <v-icon color="white">{{ farmer.icon }}</v-icon>
              </v-avatar>
              <div>
                <h3 class="text-h6">{{ farmer.name }}</h3>
                <p class="text-caption text-grey">{{ farmer.region }}</p>
              </div>
            </v-card-title>

            <v-card-text>
              <p class="text-body-2 mb-3">{{ farmer.description }}</p>
              
              <div class="farmer-info mb-3">
                <div class="d-flex align-center mb-1">
                  <v-icon size="16" class="mr-2">mdi-map-marker</v-icon>
                  <span class="text-caption">{{ farmer.location }}</span>
                </div>
                <div class="d-flex align-center mb-1">
                  <v-icon size="16" class="mr-2">mdi-sprout</v-icon>
                  <span class="text-caption">{{ farmer.specialty }}</span>
                </div>
                <div class="d-flex align-center mb-1">
                  <v-icon size="16" class="mr-2">mdi-star</v-icon>
                  <span class="text-caption">{{ farmer.rating }}/5 ({{ farmer.reviews }} avis)</span>
                </div>
              </div>

              <div class="d-flex align-center justify-space-between">
                <v-chip 
                  :color="farmer.color" 
                  variant="tonal"
                  size="small"
                >
                  {{ farmer.productCount }} produits
                </v-chip>
                <v-btn 
                  color="primary" 
                  size="small"
                  @click="viewFarmer(farmer)"
                >
                  Voir les produits
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>

    <!-- Aucun fermier trouvé -->
    <div v-else class="text-center py-8">
      <v-icon size="64" color="grey">mdi-account-off</v-icon>
      <h3 class="text-h6 mt-4">Aucun fermier trouvé</h3>
      <p class="text-grey">Essayez de modifier vos critères de recherche</p>
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// État local
const farmers = ref([])
const searchQuery = ref('')
const selectedRegion = ref(null)
const selectedSpecialty = ref(null)
const organicOnly = ref(false)
const loading = ref(false)

// Options pour les filtres
const regions = ref([
  'Bouaké', 'Korhogo', 'Yamoussoukro', 'San-Pédro', 'Abidjan'
])

const specialties = ref([
  'Fruits & Légumes', 'Céréales', 'Élevage', 'Apiculture', 'Maraîchage'
])

// État calculé
const filteredFarmers = computed(() => {
  let filtered = farmers.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(farmer => 
      farmer.name.toLowerCase().includes(query) ||
      farmer.description.toLowerCase().includes(query) ||
      farmer.specialty.toLowerCase().includes(query)
    )
  }

  if (selectedRegion.value) {
    filtered = filtered.filter(farmer => farmer.region === selectedRegion.value)
  }

  if (selectedSpecialty.value) {
    filtered = filtered.filter(farmer => farmer.specialty === selectedSpecialty.value)
  }

  if (organicOnly.value) {
    filtered = filtered.filter(farmer => farmer.isOrganic)
  }

  return filtered
})

// Méthodes
const loadFarmers = async () => {
  loading.value = true
  try {
    // Simuler des données de fermiers
    farmers.value = [
      {
        id: 1,
        name: 'Jean-Baptiste Kouassi',
        region: 'Bouaké',
        location: 'Bouaké, Côte d\'Ivoire',
        specialty: 'Fruits & Légumes',
        description: 'Ferme familiale spécialisée dans la production biologique de fruits et légumes frais.',
        image: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=200&fit=crop',
        icon: 'mdi-sprout',
        color: 'green',
        isOrganic: true,
        isLocal: true,
        rating: 4.8,
        reviews: 156,
        productCount: 25
      },
      {
        id: 2,
        name: 'Marie Traoré',
        region: 'Korhogo',
        location: 'Korhogo, Côte d\'Ivoire',
        specialty: 'Élevage',
        description: 'Élevage traditionnel de volailles et bovins avec des méthodes respectueuses de l\'environnement.',
        image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=200&fit=crop',
        icon: 'mdi-cow',
        color: 'orange',
        isOrganic: true,
        isLocal: true,
        rating: 4.6,
        reviews: 89,
        productCount: 18
      },
      {
        id: 3,
        name: 'Amadou Diallo',
        region: 'Yamoussoukro',
        location: 'Yamoussoukro, Côte d\'Ivoire',
        specialty: 'Céréales',
        description: 'Producteur de céréales locales avec des techniques de culture durables.',
        image: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=200&fit=crop',
        icon: 'mdi-grain',
        color: 'brown',
        isOrganic: false,
        isLocal: true,
        rating: 4.4,
        reviews: 67,
        productCount: 12
      }
    ]
  } catch (error) {
    console.error('Erreur lors du chargement des fermiers:', error)
  } finally {
    loading.value = false
  }
}

const filterFarmers = () => {
  // Le filtrage se fait automatiquement via computed
}

const viewFarmer = (farmer) => {
  router.push(`/customer/products?farmer=${farmer.id}`)
}

// Initialisation
onMounted(() => {
  loadFarmers()
})
</script>

<style scoped>
.farmer-card {
  height: 100%;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.farmer-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.farmer-badges {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.farmer-info {
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 12px;
}
</style>
