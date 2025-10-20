<template>
  <v-container fluid>
    <!-- En-tête avec workflow -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-shield-check</v-icon>
            <span>⚙️ Dashboard Admin - Validation AgriMarket</span>
          </v-card-title>
          <v-card-subtitle>
            Validez les produits des fournisseurs et supervisez le workflow complet
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Workflow de validation -->
    <v-row class="mb-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>🔄 Workflow de Validation</v-card-title>
          <v-card-text>
            <v-stepper v-model="validationStep" alt-labels>
              <v-stepper-header>
                <v-stepper-item
                  :complete="validationStep > 1"
                  :value="1"
                  color="orange"
                >
                  <v-icon>mdi-clock-outline</v-icon>
                  <div class="text-caption">En Attente</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="validationStep > 2"
                  :value="2"
                  color="blue"
                >
                  <v-icon>mdi-eye</v-icon>
                  <div class="text-caption">En Révision</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="validationStep > 3"
                  :value="3"
                  color="green"
                >
                  <v-icon>mdi-check-circle</v-icon>
                  <div class="text-caption">Approuvé</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="validationStep > 4"
                  :value="4"
                  color="purple"
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

    <!-- Statistiques de validation -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="orange" dark>
          <v-icon size="48" class="mb-2">mdi-clock-outline</v-icon>
          <h3 class="text-h6">En Attente</h3>
          <p class="text-h4">{{ stats.pendingValidation }}</p>
          <v-btn small color="white" text @click="viewPendingProducts">
            Valider
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="blue" dark>
          <v-icon size="48" class="mb-2">mdi-eye</v-icon>
          <h3 class="text-h6">En Révision</h3>
          <p class="text-h4">{{ stats.inReview }}</p>
          <v-btn small color="white" text @click="viewInReviewProducts">
            Continuer
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="green" dark>
          <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Approuvés</h3>
          <p class="text-h4">{{ stats.approved }}</p>
          <v-btn small color="white" text @click="viewApprovedProducts">
            Voir
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="red" dark>
          <v-icon size="48" class="mb-2">mdi-close-circle</v-icon>
          <h3 class="text-h6">Rejetés</h3>
          <p class="text-h4">{{ stats.rejected }}</p>
          <v-btn small color="white" text @click="viewRejectedProducts">
            Voir
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <!-- Actions rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>⚡ Actions de Validation</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-btn
                  color="orange"
                  large
                  block
                  @click="$router.push('/admin/validation')"
                >
                  <v-icon left>mdi-clipboard-check</v-icon>
                  Valider les Produits
                </v-btn>
              </v-col>
              <v-col cols="12" md="4">
                <v-btn
                  color="blue"
                  large
                  block
                  @click="$router.push('/admin/fournisseurs')"
                >
                  <v-icon left>mdi-account-group</v-icon>
                  Gérer Fournisseurs
                </v-btn>
              </v-col>
              <v-col cols="12" md="4">
                <v-btn
                  color="purple"
                  large
                  block
                  @click="$router.push('/admin/analytics')"
                >
                  <v-icon left>mdi-chart-line</v-icon>
                  Analytics
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Produits en attente de validation -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>📦 Produits en Attente de Validation</v-card-title>
          <v-data-table
            :headers="productHeaders"
            :items="pendingProducts"
            :items-per-page="5"
          >
            <template v-slot:item.image="{ item }">
              <v-avatar size="60" rounded>
                <v-img :src="item.image" :alt="item.name"></v-img>
              </v-avatar>
            </template>
            <template v-slot:item.supplier="{ item }">
              <div>
                <div class="font-weight-medium">{{ item.supplierName }}</div>
                <div class="text-caption">{{ item.supplierLocation }}</div>
              </div>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                small
                color="primary"
                @click="reviewProduct(item)"
              >
                <v-icon small left>mdi-eye</v-icon>
                Réviser
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

const validationStep = ref(1) // Étape actuelle de validation

const stats = ref({
  pendingValidation: 8,
  inReview: 3,
  approved: 45,
  rejected: 2
})

const productHeaders = [
  { text: 'Image', value: 'image', sortable: false, width: '80px' },
  { text: 'Produit', value: 'name' },
  { text: 'Fournisseur', value: 'supplier' },
  { text: 'Prix', value: 'price' },
  { text: 'Stock', value: 'stock' },
  { text: 'Date', value: 'submittedAt' },
  { text: 'Actions', value: 'actions', sortable: false }
]

const pendingProducts = ref([
  {
    id: 1,
    name: 'Tomates Bio',
    image: '/images/tomates.jpg',
    supplierName: 'Ferme Bio Yannick',
    supplierLocation: 'Douala, Cameroun',
    price: '800 FCFA/kg',
    stock: 50,
    submittedAt: '2024-10-19',
    status: 'PENDING_VALIDATION'
  },
  {
    id: 2,
    name: 'Carottes',
    image: '/images/carottes.jpg',
    supplierName: 'Ferme Vert',
    supplierLocation: 'Yaoundé, Cameroun',
    price: '600 FCFA/kg',
    stock: 30,
    submittedAt: '2024-10-19',
    status: 'PENDING_VALIDATION'
  },
  {
    id: 3,
    name: 'Bananes Plantain',
    image: '/images/bananes.jpg',
    supplierName: 'Plantation Manga',
    supplierLocation: 'Bafoussam, Cameroun',
    price: '2000 FCFA/régime',
    stock: 25,
    submittedAt: '2024-10-18',
    status: 'PENDING_VALIDATION'
  }
])

const viewPendingProducts = () => {
  console.log('Voir produits en attente de validation')
}

const viewInReviewProducts = () => {
  console.log('Voir produits en révision')
}

const viewApprovedProducts = () => {
  console.log('Voir produits approuvés')
}

const viewRejectedProducts = () => {
  console.log('Voir produits rejetés')
}

const reviewProduct = (product) => {
  console.log('Réviser produit:', product)
  // Rediriger vers la page de révision détaillée
}

onMounted(() => {
  loadAdminStats()
})

const loadAdminStats = () => {
  console.log('Chargement des statistiques admin')
}
</script>
