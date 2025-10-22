<template>
  <v-container fluid>
    <!-- En-tête avec workflow -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-sprout</v-icon>
            <span>🌾 Dashboard Fournisseur - Workflow AgriMarket</span>
          </v-card-title>
          <v-card-subtitle>
            Présentez vos produits agricoles avec images et suivez le processus de validation
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Workflow visuel -->
    <v-row class="mb-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>🔄 Votre Workflow de Production</v-card-title>
          <v-card-text>
            <v-stepper v-model="currentStep" alt-labels>
              <v-stepper-header>
                <v-stepper-item
                  :complete="currentStep > 1"
                  :value="1"
                  color="primary"
                >
                  <v-icon>mdi-plus-circle</v-icon>
                  <div class="text-caption">Ajouter Produit</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="currentStep > 2"
                  :value="2"
                  color="orange"
                >
                  <v-icon>mdi-image-multiple</v-icon>
                  <div class="text-caption">Upload Images</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="currentStep > 3"
                  :value="3"
                  color="blue"
                >
                  <v-icon>mdi-check-circle</v-icon>
                  <div class="text-caption">Validation Admin</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="currentStep > 4"
                  :value="4"
                  color="green"
                >
                  <v-icon>mdi-truck-delivery</v-icon>
                  <div class="text-caption">Récupération</div>
                </v-stepper-item>
              </v-stepper-header>
            </v-stepper>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques adaptées au workflow -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="primary" dark>
          <v-icon size="48" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Mes Produits</h3>
          <p class="text-h4">{{ stats.totalProducts }}</p>
          <v-btn small color="white" text @click="$router.push('/supplier/products')">
            Gérer
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="orange" dark>
          <v-icon size="48" class="mb-2">mdi-clock-outline</v-icon>
          <h3 class="text-h6">En Validation</h3>
          <p class="text-h4">{{ stats.pendingValidation }}</p>
          <v-btn small color="white" text @click="viewPendingProducts">
            Voir
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="green" dark>
          <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Validés</h3>
          <p class="text-h4">{{ stats.validatedProducts }}</p>
          <v-btn small color="white" text @click="viewValidatedProducts">
            Voir
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="blue" dark>
          <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
          <h3 class="text-h6">Récupérations</h3>
          <p class="text-h4">{{ stats.pendingCollection }}</p>
          <v-btn small color="white" text @click="$router.push('/supplier/collection')">
            Voir
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <!-- Actions rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>⚡ Actions Rapides</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-btn
                  color="primary"
                  large
                  block
                  @click="$router.push('/supplier/products/add')"
                >
                  <v-icon left>mdi-plus</v-icon>
                  Ajouter un Produit
                </v-btn>
              </v-col>
              <v-col cols="12" md="4">
                <v-btn
                  color="orange"
                  large
                  block
                  @click="$router.push('/supplier/products/images')"
                >
                  <v-icon left>mdi-image-multiple</v-icon>
                  Gérer les Images
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  color="info"
                  large
                  block
                  @click="$router.push('/supplier/orders')"
                >
                  <v-icon left>mdi-clipboard-list</v-icon>
                  Mes Commandes
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  color="blue"
                  large
                  block
                  @click="$router.push('/supplier/collection')"
                >
                  <v-icon left>mdi-truck-delivery</v-icon>
                  Récupérations
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Produits récents -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>📦 Mes Produits Récents</v-card-title>
          <v-data-table
            :headers="productHeaders"
            :items="recentProducts"
            :items-per-page="5"
          >
            <template v-slot:item.image="{ item }">
              <v-avatar size="40" rounded>
                <v-img :src="item.image" :alt="item.name"></v-img>
              </v-avatar>
            </template>
            <template v-slot:item.status="{ item }">
              <v-chip
                :color="getStatusColor(item.status)"
                small
                :text-color="getStatusTextColor(item.status)"
              >
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                small
                color="primary"
                text
                @click="editProduct(item)"
              >
                <v-icon small>mdi-pencil</v-icon>
              </v-btn>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const currentStep = ref(2) // Étape actuelle du workflow

const stats = ref({
  totalProducts: 12,
  pendingValidation: 3,
  validatedProducts: 8,
  pendingCollection: 2
})

const productHeaders = [
  { text: 'Image', value: 'image', sortable: false, width: '60px' },
  { text: 'Nom', value: 'name' },
  { text: 'Catégorie', value: 'category' },
  { text: 'Prix', value: 'price' },
  { text: 'Stock', value: 'stock' },
  { text: 'Statut', value: 'status' },
  { text: 'Actions', value: 'actions', sortable: false }
]

const recentProducts = ref([
  {
    id: 1,
    name: 'Tomates Bio',
    image: '/images/tomates.jpg',
    category: 'Légumes',
    price: '800 FCFA/kg',
    stock: 50,
    status: 'PENDING_VALIDATION'
  },
  {
    id: 2,
    name: 'Carottes',
    image: '/images/carottes.jpg',
    category: 'Légumes',
    price: '600 FCFA/kg',
    stock: 30,
    status: 'VALIDATED'
  },
  {
    id: 3,
    name: 'Bananes Plantain',
    image: '/images/bananes.jpg',
    category: 'Fruits',
    price: '2000 FCFA/régime',
    stock: 25,
    status: 'PENDING_COLLECTION'
  }
])

const getStatusColor = (status) => {
  const colors = {
    'PENDING_VALIDATION': 'orange',
    'VALIDATED': 'green',
    'PENDING_COLLECTION': 'blue',
    'REJECTED': 'red'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return 'white'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING_VALIDATION': 'En validation',
    'VALIDATED': 'Validé',
    'PENDING_COLLECTION': 'À récupérer',
    'REJECTED': 'Rejeté'
  }
  return texts[status] || status
}

const viewPendingProducts = () => {
  // Filtrer et afficher les produits en attente de validation
  console.log('Voir produits en validation')
}

const viewValidatedProducts = () => {
  // Filtrer et afficher les produits validés
  console.log('Voir produits validés')
}

const viewCollections = () => {
  // Afficher les récupérations en attente
  console.log('Voir récupérations')
}

const editProduct = (product) => {
  // Éditer le produit
  console.log('Éditer produit:', product)
}

onMounted(() => {
  // Charger les statistiques du fournisseur
  loadSupplierStats()
})

const loadSupplierStats = () => {
  // Simuler le chargement des statistiques
  console.log('Chargement des statistiques fournisseur')
}
</script>
