<template>
  <v-container fluid>
    <!-- En-tête avec workflow -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-warehouse</v-icon>
            <span>📦 Dashboard Entrepôt - Gestion Stock AgriMarket</span>
          </v-card-title>
          <v-card-subtitle>
            Gérez le stock central et supervisez les réceptions depuis les fournisseurs
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Workflow de gestion stock -->
    <v-row class="mb-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>🔄 Workflow de Gestion Stock</v-card-title>
          <v-card-text>
            <v-stepper v-model="warehouseStep" alt-labels>
              <v-stepper-header>
                <v-stepper-item
                  :complete="warehouseStep > 1"
                  :value="1"
                  color="blue"
                >
                  <v-icon>mdi-truck-delivery</v-icon>
                  <div class="text-caption">Réception</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="warehouseStep > 2"
                  :value="2"
                  color="orange"
                >
                  <v-icon>mdi-package-variant</v-icon>
                  <div class="text-caption">Stockage</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="warehouseStep > 3"
                  :value="3"
                  color="green"
                >
                  <v-icon>mdi-clipboard-list</v-icon>
                  <div class="text-caption">Préparation</div>
                </v-stepper-item>
                <v-divider></v-divider>
                <v-stepper-item
                  :complete="warehouseStep > 4"
                  :value="4"
                  color="purple"
                >
                  <v-icon>mdi-truck</v-icon>
                  <div class="text-caption">Expédition</div>
                </v-stepper-item>
              </v-stepper-header>
            </v-stepper>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques de stock -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="blue" dark>
          <v-icon size="48" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits en Stock</h3>
          <p class="text-h4">{{ stats.totalProducts }}</p>
          <v-btn small color="white" text @click="viewInventory">
            Inventaire
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="orange" dark>
          <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
          <h3 class="text-h6">Réceptions Aujourd'hui</h3>
          <p class="text-h4">{{ stats.todayReceptions }}</p>
          <v-btn small color="white" text @click="viewReceptions">
            Voir
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="green" dark>
          <v-icon size="48" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes à Préparer</h3>
          <p class="text-h4">{{ stats.ordersToPrepare }}</p>
          <v-btn small color="white" text @click="viewOrdersToPrepare">
            Préparer
          </v-btn>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4" color="red" dark>
          <v-icon size="48" class="mb-2">mdi-alert-circle</v-icon>
          <h3 class="text-h6">Alertes Stock</h3>
          <p class="text-h4">{{ stats.stockAlerts }}</p>
          <v-btn small color="white" text @click="viewStockAlerts">
            Voir
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <!-- Actions rapides -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>⚡ Actions de Gestion</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="3">
                <v-btn
                  color="blue"
                  large
                  block
                  @click="$router.push('/warehouse/inventory')"
                >
                  <v-icon left>mdi-package-variant</v-icon>
                  Inventaire
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  color="orange"
                  large
                  block
                  @click="$router.push('/warehouse/receptions')"
                >
                  <v-icon left>mdi-truck-delivery</v-icon>
                  Réceptions
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  color="green"
                  large
                  block
                  @click="$router.push('/warehouse/orders')"
                >
                  <v-icon left>mdi-clipboard-list</v-icon>
                  Commandes
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  color="red"
                  large
                  block
                  @click="$router.push('/warehouse/alerts')"
                >
                  <v-icon left>mdi-alert</v-icon>
                  Alertes
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Produits récemment reçus -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>📦 Produits Récemment Reçus</v-card-title>
          <v-data-table
            :headers="productHeaders"
            :items="recentProducts"
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
                @click="viewProductDetails(item)"
              >
                <v-icon small>mdi-eye</v-icon>
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

const warehouseStep = ref(2) // Étape actuelle du workflow entrepôt

const stats = ref({
  totalProducts: 156,
  todayReceptions: 8,
  ordersToPrepare: 12,
  stockAlerts: 3
})

const productHeaders = [
  { text: 'Image', value: 'image', sortable: false, width: '80px' },
  { text: 'Produit', value: 'name' },
  { text: 'Fournisseur', value: 'supplier' },
  { text: 'Quantité', value: 'quantity' },
  { text: 'Statut', value: 'status' },
  { text: 'Date Réception', value: 'receivedAt' },
  { text: 'Actions', value: 'actions', sortable: false }
]

const recentProducts = ref([
  {
    id: 1,
    name: 'Tomates Bio',
    image: '/images/tomates.jpg',
    supplierName: 'Ferme Bio Yannick',
    supplierLocation: 'Douala, Cameroun',
    quantity: 50,
    status: 'STOCKED',
    receivedAt: '2024-10-19'
  },
  {
    id: 2,
    name: 'Carottes',
    image: '/images/carottes.jpg',
    supplierName: 'Ferme Vert',
    supplierLocation: 'Yaoundé, Cameroun',
    quantity: 30,
    status: 'PROCESSING',
    receivedAt: '2024-10-19'
  },
  {
    id: 3,
    name: 'Bananes Plantain',
    image: '/images/bananes.jpg',
    supplierName: 'Plantation Manga',
    supplierLocation: 'Bafoussam, Cameroun',
    quantity: 25,
    status: 'READY_FOR_SHIPPING',
    receivedAt: '2024-10-18'
  }
])

const getStatusColor = (status) => {
  const colors = {
    'RECEIVED': 'blue',
    'PROCESSING': 'orange',
    'STOCKED': 'green',
    'READY_FOR_SHIPPING': 'purple',
    'SHIPPED': 'grey'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return 'white'
}

const getStatusText = (status) => {
  const texts = {
    'RECEIVED': 'Reçu',
    'PROCESSING': 'En traitement',
    'STOCKED': 'Stocké',
    'READY_FOR_SHIPPING': 'Prêt à expédier',
    'SHIPPED': 'Expédié'
  }
  return texts[status] || status
}

const viewInventory = () => {
  console.log('Voir inventaire')
}

const viewReceptions = () => {
  console.log('Voir réceptions')
}

const viewOrdersToPrepare = () => {
  console.log('Voir commandes à préparer')
}

const viewStockAlerts = () => {
  console.log('Voir alertes stock')
}

const viewProductDetails = (product) => {
  console.log('Voir détails produit:', product)
}

onMounted(() => {
  loadWarehouseStats()
})

const loadWarehouseStats = () => {
  console.log('Chargement des statistiques entrepôt')
}
</script>
