<template>
  <v-card>
    <v-card-title class="d-flex align-center">
      <v-icon class="mr-3" color="primary">mdi-timeline</v-icon>
      <span>📅 Timeline de Récupération</span>
    </v-card-title>
    <v-card-text>
      <v-timeline density="compact" align="start">
        <v-timeline-item
          v-for="(item, index) in timeline"
          :key="index"
          :dot-color="item.color"
          :icon="item.icon"
          size="small"
        >
          <template v-slot:opposite>
            <span class="text-caption">{{ item.time }}</span>
          </template>
          <div>
            <div class="font-weight-medium">{{ item.title }}</div>
            <div class="text-caption text-grey-600">{{ item.description }}</div>
            <v-chip 
              v-if="item.status"
              :color="item.statusColor" 
              size="x-small" 
              class="mt-1"
            >
              {{ item.status }}
            </v-chip>
          </div>
        </v-timeline-item>
      </v-timeline>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const timeline = computed(() => {
  const items = []
  
  // Date de création
  items.push({
    time: new Date(props.product.createdAt).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    }),
    title: 'Produit créé',
    description: `Produit "${props.product.title}" ajouté au système`,
    icon: 'mdi-plus-circle',
    color: 'blue'
  })
  
  // Date d'approbation
  if (props.product.statusUpdatedAt) {
    items.push({
      time: new Date(props.product.statusUpdatedAt).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit'
      }),
      title: 'Produit approuvé',
      description: 'Produit validé par l\'administrateur',
      icon: 'mdi-check-circle',
      color: 'green',
      status: 'APPROUVÉ',
      statusColor: 'green'
    })
  }
  
  // Date de livraison prévue
  const deliveryDate = new Date(new Date(props.product.createdAt).getTime() + 3 * 24 * 60 * 60 * 1000)
  items.push({
    time: deliveryDate.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    }),
    title: 'Récupération prévue',
    description: 'Date prévue pour la récupération par l\'entrepôt',
    icon: 'mdi-truck-delivery',
    color: 'orange',
    status: 'PRÊT',
    statusColor: 'orange'
  })
  
  // Statut de récupération
  if (props.product.collectionStatus === 'COLLECTED') {
    items.push({
      time: 'Maintenant',
      title: 'Récupéré',
      description: 'Produit récupéré par l\'entrepôt',
      icon: 'mdi-truck-check',
      color: 'green',
      status: 'RÉCUPÉRÉ',
      statusColor: 'green'
    })
  }
  
  return items
})
</script>
