<template>
  <v-row>
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="green" dark>
        <v-icon size="48" class="mb-2">mdi-truck-check</v-icon>
        <h3 class="text-h6">Livrés et Reçus</h3>
        <p class="text-h4">{{ stats.deliveredAndReceived }}</p>
        <v-btn small color="white" text @click="$emit('view-delivered')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="blue" dark>
        <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
        <h3 class="text-h6">Expédiés</h3>
        <p class="text-h4">{{ stats.shipped }}</p>
        <v-btn small color="white" text @click="$emit('view-shipped')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="orange" dark>
        <v-icon size="48" class="mb-2">mdi-package-variant</v-icon>
        <h3 class="text-h6">Non Expédiés</h3>
        <p class="text-h4">{{ stats.notShipped }}</p>
        <v-btn small color="white" text @click="$emit('view-not-shipped')">
          Voir
        </v-btn>
      </v-card>
    </v-col>
    
    <v-col cols="12" md="3">
      <v-card class="text-center pa-4" color="purple" dark>
        <v-icon size="48" class="mb-2">mdi-chart-line</v-icon>
        <h3 class="text-h6">Total Produits</h3>
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
      deliveredAndReceived: 0,
      shipped: 0,
      notShipped: 0,
      total: 0
    })
  }
})

defineEmits(['view-delivered', 'view-shipped', 'view-not-shipped', 'view-all'])
</script>
