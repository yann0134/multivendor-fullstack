<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Gestion de l'Inventaire</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>Stock de l'entrepôt</v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="inventory"
              :loading="loading"
            >
              <template v-slot:item.actions="{ item }">
                <v-btn icon @click="updateStock(item)">
                  <v-icon>mdi-pencil</v-icon>
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

const inventory = ref([])
const loading = ref(false)

const headers = [
  { title: 'Produit', key: 'product.title' },
  { title: 'Stock actuel', key: 'quantity' },
  { title: 'Stock réservé', key: 'reservedQuantity' },
  { title: 'Stock disponible', key: 'availableQuantity' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const updateStock = (item) => {
  console.log('Mettre à jour le stock:', item)
}

onMounted(() => {
  // Charger l'inventaire de l'entrepôt
})
</script>
