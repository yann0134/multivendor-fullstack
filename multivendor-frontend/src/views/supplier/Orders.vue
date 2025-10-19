<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Commandes d'Approvisionnement</h1>
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
              <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)">
                  {{ item.status }}
                </v-chip>
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn
                  v-if="item.status === 'PENDING'"
                  color="success"
                  size="small"
                  @click="updateOrderStatus(item.id, 'CONFIRMED')"
                >
                  Confirmer
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

const orders = ref([])
const loading = ref(false)

const headers = [
  { title: 'ID Commande', key: 'supplyOrderId' },
  { title: 'Entrepôt', key: 'warehouse.warehouseName' },
  { title: 'Montant', key: 'totalAmount' },
  { title: 'Statut', key: 'status' },
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

const updateOrderStatus = (orderId, status) => {
  console.log('Mettre à jour le statut:', orderId, status)
}

onMounted(() => {
  // Charger les commandes d'approvisionnement
})
</script>
