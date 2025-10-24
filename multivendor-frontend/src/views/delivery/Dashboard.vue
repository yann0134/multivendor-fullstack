<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Tableau de bord Livreur</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-truck</v-icon>
          <h3 class="text-h6">Tâches actives</h3>
          <p class="text-h4">{{ stats.activeTasks }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Livraisons terminées</h3>
          <p class="text-h4">{{ stats.completedTasks }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-currency-eur</v-icon>
          <h3 class="text-h6">Gains du mois</h3>
          <p class="text-h4">{{ stats.monthlyEarnings }} FCFA</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
            <v-icon size="48" color="warning" class="mb-2">mdi-star</v-icon>
          <h3 class="text-h6">Note moyenne</h3>
          <p class="text-h4">{{ stats.averageRating }}/5</p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  activeTasks: 0,
  completedTasks: 0,
  monthlyEarnings: 0,
  averageRating: 0
})

onMounted(() => {
  // Charger les statistiques du livreur
})
</script>
