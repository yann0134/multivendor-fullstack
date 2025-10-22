<template>
  <div class="d-flex align-center">
    <!-- Affichage simple de la quantité -->
    <template v-if="quantitySource.type === 'admin'">
      <div class="d-flex align-center text-orange">
        <v-icon size="small" class="mr-1">mdi-account-tie</v-icon>
        <span class="font-weight-medium">{{ quantitySource.quantity }}</span>
        <span class="text-caption ml-1">unités</span>
      </div>
    </template>
    <template v-else-if="quantitySource.type === 'supplier'">
      <div class="d-flex align-center text-blue">
        <v-icon size="small" class="mr-1">mdi-account</v-icon>
        <span class="font-weight-medium">{{ quantitySource.quantity }}</span>
        <span class="text-caption ml-1">unités</span>
      </div>
    </template>
    <template v-else>
      <div class="d-flex align-center text-grey">
        <v-icon size="small" class="mr-1">mdi-alert-circle</v-icon>
        <span class="text-caption">Aucune quantité</span>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

// Méthode pour déterminer la source de la quantité
const getQuantitySource = (product) => {
  // Priorité absolue à la quantité admin si > 0
  if (product?.adminRequestedQuantity > 0) {
    return {
      type: 'admin',
      quantity: product.adminRequestedQuantity
    }
  } else if (product?.supplierAvailableQuantity > 0) {
    return {
      type: 'supplier',
      quantity: product.supplierAvailableQuantity
    }
  }
  return {
    type: 'none',
    quantity: 0
  }
}

const quantitySource = computed(() => getQuantitySource(props.product))
</script>
