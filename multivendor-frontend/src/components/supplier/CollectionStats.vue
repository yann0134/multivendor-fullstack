<template>
  <v-row>
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="orange" dark>
        <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
        <h3 class="text-h6">Prêts pour Récupération</h3>
        <p class="text-h4">{{ stats.readyForCollection }}</p>
        <v-btn small color="white" text @click="$emit('view-ready')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="green" dark>
        <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
        <h3 class="text-h6">Récupérés</h3>
        <p class="text-h4">{{ stats.collected }}</p>
        <v-btn small color="white" text @click="$emit('view-collected')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="blue" dark>
        <v-icon size="48" class="mb-2">mdi-clock</v-icon>
        <h3 class="text-h6">En Attente</h3>
        <p class="text-h4">{{ stats.pending }}</p>
        <v-btn small color="white" text @click="$emit('view-pending')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="purple" dark>
        <v-icon size="48" class="mb-2">mdi-chart-line</v-icon>
        <h3 class="text-h6">Total Récupérations</h3>
        <p class="text-h4">{{ stats.total }}</p>
        <v-btn small color="white" text @click="$emit('view-all')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
defineProps({
  stats: {
    type: Object,
    required: true,
    default: () => ({
      readyForCollection: 0,
      collected: 0,
      pending: 0,
      total: 0
    })
  }
})

defineEmits(['view-ready', 'view-collected', 'view-pending', 'view-all'])
</script>
