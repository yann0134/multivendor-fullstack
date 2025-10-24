<template>
  <v-container v-if="order">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <v-btn 
            icon 
            @click="$router.back()"
            class="mr-4"
          >
            <v-icon>mdi-arrow-left</v-icon>
          </v-btn>
          <div>
            <h1 class="text-h4">Commande #{{ order.orderId }}</h1>
            <p class="text-grey">Passée le {{ formatDate(order.orderDate) }}</p>
          </div>
        </div>
      </v-col>
    </v-row>

    <v-row>
      <!-- Détails de la commande -->
      <v-col cols="12" md="8">
        <v-card class="mb-6">
          <v-card-title>
            <v-icon class="mr-2">mdi-package-variant</v-icon>
            Articles commandés
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="item in order.orderItems"
                :key="item.id"
                class="order-item"
              >
                <template v-slot:prepend>
                  <v-img
                    :src="item.product.images?.[0] || getDefaultProductImage()"
                    width="80"
                    height="80"
                    cover
                    class="rounded"
                  />
                </template>
                
                <v-list-item-title>{{ item.product.title }}</v-list-item-title>
                <v-list-item-subtitle>
                  Quantité: {{ item.quantity }} | Taille: {{ item.size }} | Couleur: {{ item.color }}
                </v-list-item-subtitle>
                
                <template v-slot:append>
                  <div class="text-right">
                    <div class="text-h6">{{ (item.sellingPrice * item.quantity).toFixed(2) }} FCFA</div>
                    <div v-if="item.mrpPrice > item.sellingPrice" class="text-decoration-line-through text-grey">
                      {{ (item.mrpPrice * item.quantity).toFixed(2) }} FCFA
                    </div>
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- Statut de la commande -->
        <v-card class="mb-6">
          <v-card-title>
            <v-icon class="mr-2">mdi-timeline</v-icon>
            Suivi de la commande
          </v-card-title>
          <v-card-text>
            <v-timeline density="compact">
              <v-timeline-item
                v-for="(status, index) in orderStatuses"
                :key="index"
                :color="getStatusColor(status)"
                :icon="getStatusIcon(status)"
                :dot-color="getStatusColor(status)"
              >
                <template v-slot:opposite>
                  <span class="text-caption">{{ getStatusDate(status) }}</span>
                </template>
                <div>
                  <div class="text-subtitle-2">{{ getStatusTitle(status) }}</div>
                  <div class="text-caption text-grey">{{ getStatusDescription(status) }}</div>
                </div>
              </v-timeline-item>
            </v-timeline>
          </v-card-text>
        </v-card>

        <!-- Actions -->
        <v-card v-if="canCancelOrder">
          <v-card-title>Actions</v-card-title>
          <v-card-text>
            <v-btn 
              color="error" 
              variant="outlined"
              @click="cancelOrder"
              :loading="cancelling"
            >
              <v-icon left>mdi-cancel</v-icon>
              Annuler la commande
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <v-card sticky>
          <v-card-title>Résumé de la commande</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Sous-total ({{ order.totalItem }} articles)</span>
              <span>{{ order.totalSellingPrice.toFixed(2) }} FCFA</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Livraison</span>
              <span>Gratuite</span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between text-h6">
              <span>Total</span>
              <span>{{ order.totalSellingPrice.toFixed(2) }} FCFA</span>
            </div>
          </v-card-text>
        </v-card>

        <!-- Adresse de livraison -->
        <v-card class="mt-4">
          <v-card-title>
            <v-icon class="mr-2">mdi-map-marker</v-icon>
            Adresse de livraison
          </v-card-title>
          <v-card-text>
            <div v-if="order.shippingAddress">
              <p><strong>{{ order.shippingAddress.firstName }} {{ order.shippingAddress.lastName }}</strong></p>
              <p>{{ order.shippingAddress.street }}</p>
              <p>{{ order.shippingAddress.city }}, {{ order.shippingAddress.zipCode }}</p>
              <p>{{ order.shippingAddress.mobile }}</p>
            </div>
            <div v-else class="text-grey">
              Aucune adresse de livraison
            </div>
          </v-card-text>
        </v-card>

        <!-- Informations de paiement -->
        <v-card class="mt-4">
          <v-card-title>
            <v-icon class="mr-2">mdi-credit-card</v-icon>
            Paiement
          </v-card-title>
          <v-card-text>
            <div class="d-flex align-center mb-2">
              <v-icon class="mr-2">mdi-check-circle</v-icon>
              <span>Paiement confirmé</span>
            </div>
            <div class="text-caption text-grey">
              Méthode: {{ order.paymentMethod || 'Non spécifiée' }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
  
  <v-container v-else>
    <v-row justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" />
        <p class="mt-4">Chargement de la commande...</p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/orders'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const order = ref(null)
const cancelling = ref(false)

const getDefaultProductImage = () => {
  return 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop'
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const orderStatuses = computed(() => {
  if (!order.value) return []
  
  const statuses = [
    { status: 'PENDING', date: order.value.orderDate, title: 'Commande en attente' },
    { status: 'CONFIRMED', date: order.value.confirmedDate, title: 'Commande confirmée' },
    { status: 'SHIPPED', date: order.value.shippedDate, title: 'Commande expédiée' },
    { status: 'DELIVERED', date: order.value.deliveredDate, title: 'Commande livrée' }
  ]
  
  return statuses.filter(s => s.date)
})

const canCancelOrder = computed(() => {
  return order.value && order.value.orderStatus === 'PENDING'
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'CONFIRMED': 'blue',
    'SHIPPED': 'purple',
    'DELIVERED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status.status] || 'grey'
}

const getStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock',
    'CONFIRMED': 'mdi-check',
    'SHIPPED': 'mdi-truck',
    'DELIVERED': 'mdi-check-circle',
    'CANCELLED': 'mdi-cancel'
  }
  return icons[status.status] || 'mdi-help'
}

const getStatusTitle = (status) => {
  return status.title
}

const getStatusDescription = (status) => {
  const descriptions = {
    'PENDING': 'Votre commande est en cours de traitement',
    'CONFIRMED': 'Votre commande a été confirmée et sera préparée',
    'SHIPPED': 'Votre commande a été expédiée et est en cours de livraison',
    'DELIVERED': 'Votre commande a été livrée avec succès',
    'CANCELLED': 'Votre commande a été annulée'
  }
  return descriptions[status.status] || ''
}

const getStatusDate = (status) => {
  return formatDate(status.date)
}

const cancelOrder = async () => {
  if (!confirm('Êtes-vous sûr de vouloir annuler cette commande ?')) {
    return
  }
  
  cancelling.value = true
  try {
    await orderStore.cancelOrder(order.value.id)
    // Recharger la commande
    await loadOrder()
  } catch (error) {
    console.error('Erreur lors de l\'annulation de la commande:', error)
  } finally {
    cancelling.value = false
  }
}

const loadOrder = async () => {
  const orderId = route.params.id
  try {
    order.value = await orderStore.fetchOrderById(orderId)
  } catch (error) {
    console.error('Erreur lors du chargement de la commande:', error)
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.order-item {
  border-bottom: 1px solid #e0e0e0;
}

.order-item:last-child {
  border-bottom: none;
}
</style>
