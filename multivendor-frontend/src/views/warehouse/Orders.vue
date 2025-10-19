<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Commandes Entrepôt</h1>
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
                  @click="processOrder(item.id)"
                >
                  Traiter
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
  { title: 'ID Commande', key: 'orderId' },
  { title: 'Type', key: 'orderType' },
  { title: 'Statut', key: 'status' },
  { title: 'Date', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'PROCESSING': 'blue',
    'COMPLETED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const processOrder = (orderId) => {
  console.log('Traiter la commande:', orderId)
}

onMounted(() => {
  // Charger les commandes de l'entrepôt
})
</script>
