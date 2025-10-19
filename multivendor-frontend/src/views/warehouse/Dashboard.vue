<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Tableau de bord Entrepôt</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits en stock</h3>
          <p class="text-h4">{{ stats.totalProducts }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes traitées</h3>
          <p class="text-h4">{{ stats.processedOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-warehouse</v-icon>
          <h3 class="text-h6">Capacité utilisée</h3>
          <p class="text-h4">{{ stats.capacityUsed }}%</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-truck</v-icon>
          <h3 class="text-h6">Livraisons</h3>
          <p class="text-h4">{{ stats.deliveries }}</p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  totalProducts: 0,
  processedOrders: 0,
  capacityUsed: 0,
  deliveries: 0
})

onMounted(() => {
  // Charger les statistiques de l'entrepôt
})
</script>
