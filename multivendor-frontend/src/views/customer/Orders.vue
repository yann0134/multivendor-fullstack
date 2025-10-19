<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mes Commandes</h1>
      </v-col>
    </v-row>

    <v-row v-if="orderStore.orders.length === 0">
      <v-col cols="12" class="text-center">
        <v-icon size="120" color="grey">mdi-clipboard-outline</v-icon>
        <h2 class="text-h5 mb-4">Aucune commande</h2>
        <p class="text-grey mb-6">Vous n'avez pas encore passé de commande</p>
        <v-btn color="primary" to="/customer/products" size="large">
          Voir les produits
        </v-btn>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col cols="12">
        <v-card>
          <v-list>
            <v-list-item
              v-for="order in orderStore.orders"
              :key="order.id"
              @click="$router.push(`/customer/order/${order.id}`)"
            >
              <template v-slot:prepend>
                <v-icon>mdi-package-variant</v-icon>
              </template>
              
              <v-list-item-title>
                Commande #{{ order.orderId }}
              </v-list-item-title>
              
              <v-list-item-subtitle>
                {{ order.orderDate | formatDate }} • {{ order.totalSellingPrice }}€ • {{ order.totalItem }} article(s)
              </v-list-item-subtitle>
              
              <template v-slot:append>
                <div class="d-flex align-center">
                  <v-chip :color="getStatusColor(order.orderStatus)" class="mr-2">
                    {{ order.orderStatus }}
                  </v-chip>
                  <v-btn
                    v-if="order.orderStatus === 'PENDING'"
                    color="error"
                    variant="outlined"
                    size="small"
                    @click.stop="cancelOrder(order.id)"
                  >
                    Annuler
                  </v-btn>
                </div>
              </template>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { onMounted } from 'vue'
import { useOrderStore } from '@/stores/orders'

const orderStore = useOrderStore()

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

const cancelOrder = async (orderId) => {
  try {
    await orderStore.cancelOrder(orderId)
    await orderStore.fetchUserOrders()
  } catch (error) {
    console.error('Erreur lors de l\'annulation de la commande:', error)
  }
}

onMounted(async () => {
  await orderStore.fetchUserOrders()
})
</script>
