<template>
  <v-card class="cart-summary">
    <v-card-title class="d-flex align-center">
      <v-icon class="mr-2">mdi-cart</v-icon>
      Résumé du panier
    </v-card-title>
    
    <v-card-text>
      <!-- Nombre d'articles -->
      <div class="d-flex justify-space-between align-center mb-3">
        <div class="d-flex align-center">
          <v-icon color="primary" class="mr-2">mdi-package-variant</v-icon>
          <span class="text-body-1">Articles</span>
        </div>
        <v-chip color="primary" size="small">
          {{ totalItems }} article{{ totalItems > 1 ? 's' : '' }}
        </v-chip>
      </div>
      
      <!-- Total des unités (si différent du nombre d'articles) -->
      <div v-if="totalUnits > totalItems" class="d-flex justify-space-between align-center mb-3">
        <div class="d-flex align-center">
          <v-icon color="info" class="mr-2">mdi-scale</v-icon>
          <span class="text-body-2 text-grey">Unités</span>
        </div>
        <v-chip color="info" variant="outlined" size="small">
          {{ formatQuantity(totalUnits) }} unité{{ totalUnits > 1 ? 's' : '' }}
        </v-chip>
      </div>
      
      <!-- Exemple d'explication -->
      <v-alert 
        v-if="totalUnits > totalItems" 
        type="info" 
        variant="tonal" 
        density="compact"
        class="mb-3"
      >
        <template v-slot:prepend>
          <v-icon>mdi-information</v-icon>
        </template>
        <div class="text-caption">
          <strong>Exemple :</strong> 5 carottes + 2 tomates = 2 articles (7 unités)
        </div>
      </v-alert>
      
      <!-- Prix total -->
      <v-divider class="my-3" />
      
      <div class="d-flex justify-space-between align-center">
        <span class="text-h6 font-weight-bold">Total</span>
        <span class="text-h5 text-primary font-weight-bold">
          {{ formatPrice(totalPrice) }}
        </span>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup>
// Fonction de formatage des prix
const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price).replace('XOF', 'FCFA')
}

// Fonction de formatage des quantités
const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
}

const props = defineProps({
  totalItems: {
    type: Number,
    default: 0
  },
  totalUnits: {
    type: Number,
    default: 0
  },
  totalPrice: {
    type: Number,
    default: 0
  }
})
</script>

<style scoped>
.cart-summary {
  position: sticky;
  top: 20px;
}
</style>
