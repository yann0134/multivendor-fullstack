<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Rapports de Vente</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-currency-eur</v-icon>
          <h3 class="text-h6">Chiffre d'affaires</h3>
          <p class="text-h4">{{ report.totalEarnings }}€</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes</h3>
          <p class="text-h4">{{ report.totalOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits vendus</h3>
          <p class="text-h4">{{ report.totalSales }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-star</v-icon>
          <h3 class="text-h6">Note moyenne</h3>
          <p class="text-h4">{{ report.averageRating }}/5</p>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>Graphique des ventes</v-card-title>
          <v-card-text>
            <div class="text-center pa-8">
              <v-icon size="64" color="grey">mdi-chart-line</v-icon>
              <p class="text-grey">Graphique des ventes à implémenter</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const report = ref({
  totalEarnings: 0,
  totalOrders: 0,
  totalSales: 0,
  averageRating: 0
})

onMounted(async () => {
  // Charger les rapports du vendeur
  // report.value = await sellerReportService.getSellerReport(authStore.user)
})
</script>
