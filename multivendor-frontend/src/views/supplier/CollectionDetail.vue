<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-btn
              icon
              @click="$router.go(-1)"
              class="mr-3"
            >
              <v-icon>mdi-arrow-left</v-icon>
            </v-btn>
            <v-icon class="mr-3" color="primary">mdi-truck-delivery</v-icon>
            <span>🚚 Détail de la Récupération</span>
          </v-card-title>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="product">
      <!-- Informations du produit -->
      <v-col cols="12" md="8">
        <v-card class="mb-6">
          <v-card-title>📦 Informations du Produit</v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-img
                  v-if="product.images && product.images.length > 0"
                  :src="product.images[0].imageUrl"
                  height="200"
                  class="rounded"
                ></v-img>
                <v-sheet
                  v-else
                  height="200"
                  class="d-flex align-center justify-center rounded"
                  color="grey-lighten-2"
                >
                  <v-icon size="48" color="grey">mdi-image</v-icon>
                </v-sheet>
              </v-col>
              <v-col cols="12" md="8">
                <h2 class="text-h5 mb-2">{{ product.title }}</h2>
                <p class="text-body-1 mb-4">{{ product.description }}</p>
                
                <v-row>
                  <v-col cols="6">
                    <div class="text-caption text-grey-600">Catégorie</div>
                    <div class="font-weight-medium">{{ product.category?.name || 'N/A' }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-caption text-grey-600">Prix Fournisseur</div>
                    <div class="font-weight-medium">{{ formatPrice(product.supplierPrice) }}</div>
                  </v-col>
                </v-row>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Timeline -->
        <CollectionTimeline :product="product" />
      </v-col>

      <!-- Actions et statuts -->
      <v-col cols="12" md="4">
        <v-card class="mb-6">
          <v-card-title>⚡ Actions</v-card-title>
          <v-card-text>
            <v-btn
              v-if="product.collectionStatus === 'READY_FOR_COLLECTION'"
              color="success"
              block
              large
              @click="confirmCollection"
              :loading="processing"
            >
              <v-icon left>mdi-check-circle</v-icon>
              Confirmer Récupération
            </v-btn>
            
            <v-btn
              v-else-if="product.collectionStatus === 'COLLECTED'"
              color="info"
              block
              large
              disabled
            >
              <v-icon left>mdi-truck-check</v-icon>
              Récupéré
            </v-btn>
          </v-card-text>
        </v-card>

        <!-- Informations de récupération -->
        <v-card>
          <v-card-title>📋 Détails de Récupération</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-calendar</v-icon>
                </template>
                <v-list-item-title>Date de Commande</v-list-item-title>
                <v-list-item-subtitle>{{ formatDate(product.createdAt) }}</v-list-item-subtitle>
              </v-list-item>
              
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-truck-delivery</v-icon>
                </template>
                <v-list-item-title>Date de Livraison</v-list-item-title>
                <v-list-item-subtitle>{{ formatDeliveryDate(product.createdAt) }}</v-list-item-subtitle>
              </v-list-item>
              
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-package-variant</v-icon>
                </template>
                <v-list-item-title>Quantité</v-list-item-title>
                <v-list-item-subtitle>
                  <QuantityDisplay :product="product" />
                </v-list-item-subtitle>
              </v-list-item>
              
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-information</v-icon>
                </template>
                <v-list-item-title>Statut</v-list-item-title>
                <v-list-item-subtitle>
                  <v-chip 
                    :color="getCollectionStatusColor(product.collectionStatus)" 
                    size="small"
                  >
                    {{ getCollectionStatusText(product.collectionStatus) }}
                  </v-chip>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Loading state -->
    <v-row v-else-if="loading">
      <v-col cols="12">
        <v-card>
          <v-card-text class="text-center pa-8">
            <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
            <div class="mt-4">Chargement des détails...</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import QuantityDisplay from '@/components/supplier/QuantityDisplay.vue'
import CollectionTimeline from '@/components/supplier/CollectionTimeline.vue'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(false)
const processing = ref(false)

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

const formatDeliveryDate = (orderDate) => {
  if (!orderDate) return 'N/A'
  const date = new Date(orderDate)
  date.setDate(date.getDate() + 3)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const getCollectionStatusColor = (status) => {
  const colors = {
    'READY_FOR_COLLECTION': 'orange',
    'COLLECTED': 'green',
    'PENDING': 'blue',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
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

const fetchProduct = async () => {
  loading.value = true
  try {
    const productId = route.params.id
    console.log('🔄 Récupération du produit:', productId)
    
    // TODO: Remplacer par l'API réelle
    // const response = await api.get(`/api/products/${productId}`)
    // product.value = response.data
    
    // Simulation pour le moment
    product.value = {
      id: productId,
      title: 'Produit de Test',
      description: 'Description du produit',
      collectionStatus: 'READY_FOR_COLLECTION',
      createdAt: new Date().toISOString(),
      supplierPrice: 1000,
      images: []
    }
    
    console.log('📦 Produit chargé:', product.value)
  } catch (error) {
    console.error('❌ Erreur lors du chargement du produit:', error)
  } finally {
    loading.value = false
  }
}

const confirmCollection = async () => {
  processing.value = true
  try {
    console.log('✅ Confirmation de récupération pour le produit:', product.value.id)
    
    // Mettre à jour le statut local
    product.value.collectionStatus = 'COLLECTED'
    
    // TODO: Appel API pour confirmer la récupération
    // const response = await api.put(`/api/products/${product.value.id}/confirm-collection`)
    
    console.log('✅ Récupération confirmée avec succès')
  } catch (error) {
    console.error('❌ Erreur lors de la confirmation de récupération:', error)
  } finally {
    processing.value = false
  }
}

onMounted(() => {
  fetchProduct()
})
</script>
