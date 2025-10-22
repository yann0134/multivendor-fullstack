<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-truck-delivery</v-icon>
            <span>🚚 Récupération des Produits</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher par produit ou ID"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
          </v-card-title>
          <v-card-subtitle>
            Produits prêts à être récupérés par l'entrepôt.
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

            <template v-slot:item.collectionStatus="{ item }">
              <v-chip 
                :color="getCollectionStatusColor(item.collectionStatus)" 
                :text-color="getCollectionStatusTextColor(item.collectionStatus)"
                small
              >
                <v-icon left size="small">{{ getCollectionStatusIcon(item.collectionStatus) }}</v-icon>
                {{ getCollectionStatusText(item.collectionStatus) }}
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
                <span>{{ item.createdAt ? new Date(new Date(item.createdAt).getTime() + 3 * 24 * 60 * 60 * 1000).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' }) : 'N/A' }}</span>
              </div>
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <v-btn
                  v-if="item.collectionStatus === 'READY_FOR_COLLECTION'"
                  color="success"
                  size="small"
                  @click="confirmCollection(item.id)"
                  :loading="processingCollections.includes(item.id)"
                >
                  <v-icon left>mdi-check-circle</v-icon>
                  Confirmer Récupération
                </v-btn>
                
                <v-btn
                  v-if="item.collectionStatus === 'COLLECTED'"
                  color="info"
                  size="small"
                  disabled
                >
                  <v-icon left>mdi-truck-check</v-icon>
                  Récupéré
                </v-btn>
              </div>
            </template>

            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">
                <div class="text-center">
                  <v-icon size="48" class="mb-2">mdi-truck-delivery-outline</v-icon>
                  <div>Aucun produit prêt pour la récupération.</div>
                  <div class="text-caption mt-2">Vos produits approuvés prêts à être récupérés apparaîtront ici.</div>
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
const processingCollections = ref([])

const headers = [
  { title: 'Produit', key: 'product', sortable: true },
  { title: 'Quantité', key: 'quantity', sortable: true },
  { title: 'Statut Récupération', key: 'collectionStatus', sortable: true },
  { title: 'Prix Fournisseur', key: 'supplierPrice', sortable: true },
  { title: 'Date Commande', key: 'createdAt', sortable: true },
  { title: 'Date Livraison', key: 'deliveryDate', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
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

// Fonctions pour les statuts de récupération
const getCollectionStatusColor = (status) => {
  const colors = {
    'READY_FOR_COLLECTION': 'orange',
    'COLLECTED': 'green',
    'PENDING': 'blue',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const getCollectionStatusTextColor = (status) => {
  return 'white'
}

const getCollectionStatusText = (status) => {
  const texts = {
    'READY_FOR_COLLECTION': 'Prêt pour récupération',
    'COLLECTED': 'Récupéré',
    'PENDING': 'En attente',
    'CANCELLED': 'Annulé'
  }
  return texts[status] || status
}

const getCollectionStatusIcon = (status) => {
  const icons = {
    'READY_FOR_COLLECTION': 'mdi-truck-delivery',
    'COLLECTED': 'mdi-check-circle',
    'PENDING': 'mdi-clock',
    'CANCELLED': 'mdi-close-circle'
  }
  return icons[status] || 'mdi-truck'
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF'
  }).format(price)
}

const formatDate = (date) => {
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
    console.log('🔄 Récupération des produits prêts pour la collecte...')
    const response = await api.get('/api/products/status/APPROVED/supplier')
    console.log('📊 Réponse API:', response)
    console.log('📦 Données reçues:', response.data)
    
    // Filtrer les produits prêts pour la récupération
    const readyForCollection = response.data.content || response.data
    collections.value = readyForCollection.map(product => ({
      ...product,
      collectionStatus: 'READY_FOR_COLLECTION' // Statut par défaut
    }))
    
    console.log('📦 Produits prêts pour récupération chargés:', collections.value)
    console.log('📦 Nombre de produits:', collections.value.length)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des produits:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const confirmCollection = async (productId) => {
  processingCollections.value.push(productId)
  
  try {
    console.log('✅ Confirmation de récupération pour le produit:', productId)
    
    // Mettre à jour le statut local
    const index = collections.value.findIndex(item => item.id === productId)
    if (index !== -1) {
      collections.value[index].collectionStatus = 'COLLECTED'
    }
    
    // TODO: Appel API pour confirmer la récupération
    // const response = await api.put(`/api/products/${productId}/confirm-collection`)
    
    console.log('✅ Récupération confirmée avec succès')
  } catch (error) {
    console.error('❌ Erreur lors de la confirmation de récupération:', error)
  } finally {
    const index = processingCollections.value.indexOf(productId)
    if (index > -1) {
      processingCollections.value.splice(index, 1)
    }
  }
}

onMounted(() => {
  fetchCollections()
})
</script>
