<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Analytics de la Plateforme</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Ventes par mois</v-card-title>
          <v-card-text>
            <div class="text-center pa-8">
              <v-icon size="64" color="grey">mdi-chart-line</v-icon>
              <p class="text-grey">Graphique des ventes à implémenter</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Utilisateurs actifs</v-card-title>
          <v-card-text>
            <div class="text-center pa-8">
              <v-icon size="64" color="grey">mdi-chart-pie</v-icon>
              <p class="text-grey">Graphique des utilisateurs à implémenter</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

onMounted(() => {
  // Charger les analytics de la plateforme
})
</script>
