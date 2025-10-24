<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mes Gains</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Résumé des gains</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Gains du mois</span>
              <span class="text-h6">{{ earnings.monthly }} FCFA</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
              <span>Gains totaux</span>
              <span class="text-h6">{{ earnings.total }} FCFA</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
              <span>Paiements en attente</span>
              <span class="text-h6">{{ earnings.pending }} FCFA</span>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Détails des paiements</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="payment in payments"
                :key="payment.id"
              >
                <v-list-item-title>{{ payment.description }}</v-list-item-title>
                <v-list-item-subtitle>{{ payment.date }}</v-list-item-subtitle>
                <template v-slot:append>
                  <span class="text-h6">{{ payment.amount }} FCFA</span>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const earnings = ref({
  monthly: 0,
  total: 0,
  pending: 0
})

const payments = ref([])

onMounted(() => {
  // Charger les gains du livreur
})
</script>
