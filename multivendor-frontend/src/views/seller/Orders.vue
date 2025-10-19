<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Commandes Reçues</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>Gestion des commandes</v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="orders"
              :loading="loading"
            >
              <template v-slot:item.orderStatus="{ item }">
                <v-chip :color="getStatusColor(item.orderStatus)">
                  {{ item.orderStatus }}
                </v-chip>
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn
                  v-if="item.orderStatus === 'PENDING'"
                  color="success"
                  size="small"
                  @click="updateOrderStatus(item.id, 'CONFIRMED')"
                >
                  Confirmer
                </v-btn>
                <v-btn
                  v-if="item.orderStatus === 'CONFIRMED'"
                  color="info"
                  size="small"
                  @click="updateOrderStatus(item.id, 'SHIPPED')"
                >
                  Expédier
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrderStore } from '@/stores/orders'

const orderStore = useOrderStore()

const orders = ref([])
const loading = ref(false)

const headers = [
  { title: 'ID Commande', key: 'orderId' },
  { title: 'Client', key: 'user.fullName' },
  { title: 'Montant', key: 'totalSellingPrice' },
  { title: 'Statut', key: 'orderStatus' },
  { title: 'Date', key: 'orderDate' },
  { title: 'Actions', key: 'actions', sortable: false }
]

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

const updateOrderStatus = async (orderId, status) => {
  try {
    await orderStore.updateOrderStatus(orderId, status)
    await fetchOrders()
  } catch (error) {
    console.error('Erreur lors de la mise à jour:', error)
  }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    await orderStore.fetchSellerOrders()
    orders.value = orderStore.orders
  } catch (error) {
    console.error('Erreur lors du chargement des commandes:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
