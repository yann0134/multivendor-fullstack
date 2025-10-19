<template>
  <v-container v-if="order">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Commande #{{ order.orderId }}</h1>
      </v-col>
    </v-row>

    <v-row>
      <!-- Informations de la commande -->
      <v-col cols="12" md="8">
        <v-card class="mb-4">
          <v-card-title>Articles commandés</v-card-title>
          <v-list>
            <v-list-item
              v-for="item in order.orderItems"
              :key="item.id"
            >
              <template v-slot:prepend>
                <v-img
                  :src="item.product.images?.[0] || '/placeholder.jpg'"
                  width="60"
                  height="60"
                  cover
                />
              </template>
              
              <v-list-item-title>{{ item.product.title }}</v-list-item-title>
              <v-list-item-subtitle>
                Taille: {{ item.size }} | Quantité: {{ item.quantity }}
              </v-list-item-subtitle>
              
              <template v-slot:append>
                <div class="text-right">
                  <div class="text-h6">{{ (item.sellingPrice * item.quantity).toFixed(2) }}€</div>
                  <div class="text-caption">{{ item.sellingPrice }}€ × {{ item.quantity }}</div>
                </div>
              </template>
            </v-list-item>
          </v-list>
        </v-card>

        <!-- Adresse de livraison -->
        <v-card class="mb-4">
          <v-card-title>Adresse de livraison</v-card-title>
          <v-card-text>
            <p>{{ order.shippingAddress?.firstName }} {{ order.shippingAddress?.lastName }}</p>
            <p>{{ order.shippingAddress?.street }}</p>
            <p>{{ order.shippingAddress?.city }}, {{ order.shippingAddress?.zipCode }}</p>
            <p>{{ order.shippingAddress?.mobile }}</p>
          </v-card-text>
        </v-card>

        <!-- Suivi de livraison -->
        <v-card v-if="order.deliveryPerson">
          <v-card-title>Suivi de livraison</v-card-title>
          <v-card-text>
            <div class="d-flex align-center mb-2">
              <v-icon class="mr-2">mdi-truck</v-icon>
              <span>Livreur: {{ order.deliveryPerson.fullName }}</span>
            </div>
            <div class="d-flex align-center">
              <v-icon class="mr-2">mdi-phone</v-icon>
              <span>{{ order.deliveryPerson.mobile }}</span>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Résumé de la commande</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Statut</span>
              <v-chip :color="getStatusColor(order.orderStatus)">
                {{ order.orderStatus }}
              </v-chip>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Date de commande</span>
              <span>{{ order.orderDate | formatDate }}</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Date de livraison prévue</span>
              <span>{{ order.deliverDate | formatDate }}</span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between mb-2">
              <span>Sous-total</span>
              <span>{{ order.totalMrpPrice.toFixed(2) }}€</span>
            </div>
            
            <div v-if="order.discount > 0" class="d-flex justify-space-between mb-2 text-success">
              <span>Remise</span>
              <span>-{{ order.discount.toFixed(2) }}€</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Livraison</span>
              <span>Gratuite</span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between text-h6">
              <span>Total</span>
              <span>{{ order.totalSellingPrice.toFixed(2) }}€</span>
            </div>
          </v-card-text>
          
          <v-card-actions>
            <v-btn
              v-if="order.orderStatus === 'PENDING'"
              color="error"
              variant="outlined"
              block
              @click="cancelOrder"
            >
              Annuler la commande
            </v-btn>
            
            <v-btn
              v-if="order.orderStatus === 'DELIVERED'"
              color="primary"
              block
              @click="leaveReview"
            >
              Laisser un avis
            </v-btn>
          </v-card-actions>
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/orders'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const order = ref(null)

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'CONFIRMED': 'blue',
    'SHIPPED': 'purple',
    'DELIVERED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const cancelOrder = async () => {
  try {
    await orderStore.cancelOrder(order.value.id)
    await orderStore.fetchUserOrders()
    router.push('/customer/orders')
  } catch (error) {
    console.error('Erreur lors de l\'annulation de la commande:', error)
  }
}

const leaveReview = () => {
  // Rediriger vers la page d'avis
  router.push(`/customer/review/${order.value.id}`)
}

onMounted(async () => {
  const orderId = route.params.id
  try {
    order.value = await orderStore.fetchOrderById(orderId)
  } catch (error) {
    console.error('Erreur lors du chargement de la commande:', error)
  }
})
</script>
