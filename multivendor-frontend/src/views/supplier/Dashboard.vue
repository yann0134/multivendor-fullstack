<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Tableau de bord Fournisseur</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits</h3>
          <p class="text-h4">{{ stats.totalProducts }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes</h3>
          <p class="text-h4">{{ stats.totalOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-currency-eur</v-icon>
          <h3 class="text-h6">Chiffre d'affaires</h3>
          <p class="text-h4">{{ stats.totalEarnings }}€</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-truck</v-icon>
          <h3 class="text-h6">Livraisons</h3>
          <p class="text-h4">{{ stats.totalDeliveries }}</p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  totalProducts: 0,
  totalOrders: 0,
  totalEarnings: 0,
  totalDeliveries: 0
})

onMounted(() => {
  // Charger les statistiques du fournisseur
})
</script>
