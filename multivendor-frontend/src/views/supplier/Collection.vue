<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="success">mdi-truck-check</v-icon>
            <span>✅ Produits Livrés et Reçus</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher par produit"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
          </v-card-title>
          <v-card-subtitle>
            Produits expédiés par vous et acceptés par l'entrepôt avec dates de livraison et réception.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="filteredCollections"
            :loading="loading"
            class="elevation-1"
            item-value="id"
          >
            <template v-slot:item.product="{ item }">
              <div class="d-flex align-center">
                <v-avatar size="40" class="mr-3">
                  <v-img 
                    v-if="item.images && item.images.length > 0" 
                    :src="item.images[0].imageUrl" 
                    alt="Product"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
                <div>
                  <div class="font-weight-medium">{{ item.title || 'N/A' }}</div>
                  <div class="text-caption text-grey-600">{{ item.description?.substring(0, 30) }}...</div>
                </div>
              </div>
            </template>

            <template v-slot:item.quantity="{ item }">
              <QuantityDisplay
                :product="item"
              />
            </template>

            <template v-slot:item.shipmentStatus="{ item }">
              <v-chip 
                :color="getShipmentStatusColor(item.shipmentStatus)" 
                text-color="white"
                small
              >
                <v-icon left size="small">{{ getShipmentStatusIcon(item.shipmentStatus) }}</v-icon>
                {{ getShipmentStatusText(item.shipmentStatus) }}
              </v-chip>
            </template>

            <template v-slot:item.receptionStatus="{ item }">
              <v-chip 
                :color="getReceptionStatusColor(item.receptionStatus)" 
                text-color="white"
                small
              >
                <v-icon left size="small">{{ getReceptionStatusIcon(item.receptionStatus) }}</v-icon>
                {{ getReceptionStatusText(item.receptionStatus) }}
              </v-chip>
            </template>

            <template v-slot:item.supplierPrice="{ item }">
              <div class="font-weight-medium">{{ formatPrice(item.supplierPrice) }}</div>
            </template>

            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>

            <template v-slot:item.deliveryDate="{ item }">
              <div class="d-flex align-center">
                <v-icon color="green" size="small" class="mr-1">mdi-truck-delivery</v-icon>
                <span>{{ formatDate(item.deliveryDate) }}</span>
              </div>
            </template>

            <template v-slot:item.receivedAt="{ item }">
              <div class="d-flex align-center">
                <v-icon color="success" size="small" class="mr-1">mdi-check-circle</v-icon>
                <span>{{ formatDate(item.receivedAt) }}</span>
              </div>
            </template>


            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">
                <div class="text-center">
                  <v-icon size="48" class="mb-2">mdi-truck-check-outline</v-icon>
                  <div>Aucun produit livré et reçu.</div>
                  <div class="text-caption mt-2">Vos produits expédiés et acceptés par l'entrepôt apparaîtront ici.</div>
                </div>
              </v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'
import QuantityDisplay from '@/components/supplier/QuantityDisplay.vue'

const collections = ref([])
const loading = ref(false)
const searchQuery = ref('')

const headers = [
  { title: 'Produit', key: 'product', sortable: true },
  { title: 'Quantité', key: 'quantity', sortable: true },
  { title: 'Statut Envoi', key: 'shipmentStatus', sortable: true },
  { title: 'Statut Réception', key: 'receptionStatus', sortable: true },
  { title: 'Prix Fournisseur', key: 'supplierPrice', sortable: true },
  { title: 'Date Commande', key: 'createdAt', sortable: true },
  { title: 'Date Livraison', key: 'deliveryDate', sortable: true },
  { title: 'Date Réception', key: 'receivedAt', sortable: true }
]

const filteredCollections = computed(() => {
  if (!searchQuery.value) {
    return collections.value
  }
  const query = searchQuery.value.toLowerCase()
  return collections.value.filter(item => 
    item.title?.toLowerCase().includes(query) ||
    item.description?.toLowerCase().includes(query)
  )
})

// Fonctions pour les statuts d'envoi
const getShipmentStatusColor = (status) => {
  const colors = {
    'NOT_SHIPPED': 'orange',
    'SHIPPED': 'green',
    'IN_TRANSIT': 'blue',
    'DELIVERED': 'success'
  }
  return colors[status] || 'grey'
}

const getShipmentStatusText = (status) => {
  const texts = {
    'NOT_SHIPPED': 'Non expédié',
    'SHIPPED': 'Expédié',
    'IN_TRANSIT': 'En transit',
    'DELIVERED': 'Livré'
  }
  return texts[status] || status
}

const getShipmentStatusIcon = (status) => {
  const icons = {
    'NOT_SHIPPED': 'mdi-package-variant',
    'SHIPPED': 'mdi-truck-delivery',
    'IN_TRANSIT': 'mdi-truck',
    'DELIVERED': 'mdi-check-circle'
  }
  return icons[status] || 'mdi-truck'
}

// Fonctions pour les statuts de réception
const getReceptionStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'RECEIVED': 'green',
    'REJECTED': 'red',
    'PROCESSING': 'blue'
  }
  return colors[status] || 'grey'
}

const getReceptionStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'RECEIVED': 'Reçu',
    'REJECTED': 'Rejeté',
    'PROCESSING': 'En traitement'
  }
  return texts[status] || status
}

const getReceptionStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock',
    'RECEIVED': 'mdi-check-circle',
    'REJECTED': 'mdi-close-circle',
    'PROCESSING': 'mdi-cog'
  }
  return icons[status] || 'mdi-package'
}

const formatPrice = (price) => {
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

const fetchCollections = async () => {
  loading.value = true
  try {
    console.log('🔄 Récupération des produits livrés et reçus...')
    const response = await api.get('/api/products/delivered-and-received/supplier')
    console.log('📊 Réponse API:', response)
    console.log('📦 Données reçues:', response.data)
    
    // Les produits sont déjà livrés et reçus
    collections.value = response.data
    
    console.log('📦 Produits livrés et reçus chargés:', collections.value)
    console.log('📦 Nombre de produits:', collections.value.length)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des produits livrés et reçus:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}


onMounted(() => {
  fetchCollections()
})
</script>
