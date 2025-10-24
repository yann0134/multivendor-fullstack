<template>
  <v-card class="mb-4">
    <v-card-text>
      <v-row align="center">
        <!-- Image du produit -->
        <v-col cols="12" sm="3" md="2">
          <v-img
            :src="getProductImage(item.product)"
            :alt="item.product.title"
            height="80"
            width="80"
            class="rounded"
          />
        </v-col>
        
        <!-- Informations du produit -->
        <v-col cols="12" sm="9" md="10">
          <v-row>
            <v-col cols="12" md="6">
              <h3 class="text-h6 mb-2">{{ item.product.title }}</h3>
              <p class="text-body-2 text-grey mb-2">{{ item.product.description }}</p>
              
              <!-- Informations spécifiques selon le type -->
              <div class="mb-2">
                <v-chip
                  v-if="item.product.category?.type === 'ANIMAL'"
                  color="orange"
                  size="small"
                  class="mr-2"
                >
                  <v-icon left>mdi-paw</v-icon>
                  {{ item.product.category.name }}
                </v-chip>
                
                <v-chip
                  v-if="item.product.category?.type === 'VEGETAL'"
                  color="green"
                  size="small"
                  class="mr-2"
                >
                  <v-icon left>mdi-leaf</v-icon>
                  {{ item.product.category.name }}
                </v-chip>
                
                <v-chip
                  v-if="item.organic"
                  color="success"
                  size="small"
                  class="mr-2"
                >
                  <v-icon left>mdi-leaf-circle</v-icon>
                  Bio
                </v-chip>
                
                <v-chip
                  v-if="item.fresh"
                  color="info"
                  size="small"
                  class="mr-2"
                >
                  <v-icon left>mdi-snowflake</v-icon>
                  Frais
                </v-chip>
              </div>
              
              <!-- Détails spécifiques -->
              <div class="text-caption text-grey">
                <div v-if="item.variety">
                  <v-icon size="small" class="mr-1">mdi-seed</v-icon>
                  Variété: {{ item.variety }}
                </div>
                <div v-if="item.packaging">
                  <v-icon size="small" class="mr-1">mdi-package-variant</v-icon>
                  Conditionnement: {{ item.packaging }}
                </div>
                <div v-if="item.weight">
                  <v-icon size="small" class="mr-1">mdi-scale</v-icon>
                  Poids: {{ formatWeight(item.weight) }}kg
                </div>
                <div v-if="item.storageConditions">
                  <v-icon size="small" class="mr-1">mdi-thermometer</v-icon>
                  Stockage: {{ item.storageConditions }}
                </div>
              </div>
            </v-col>
            
            <!-- Prix et quantité -->
            <v-col cols="12" md="6">
              <div class="d-flex justify-space-between align-center mb-2">
                <div>
                  <div class="text-body-2 text-grey mb-1">Prix unitaire</div>
                  <span class="text-h6 text-primary font-weight-bold">{{ formatPrice(item.product.sellingPrice) }}</span>
                  <span v-if="item.product.mrpPrice > item.product.sellingPrice" class="text-decoration-line-through text-grey ml-2 text-body-2">
                    {{ formatPrice(item.product.mrpPrice) }}
                  </span>
                </div>
                
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  size="small"
                  @click="removeItem"
                />
              </div>
              
            
              
              <!-- Contrôle de quantité amélioré -->
              <div class="d-flex align-center">
                <v-text-field
                  v-model.number="localQuantity"
                  type="number"
                  min="1"
                  :max="item.product.supplierAvailableQuantity || item.product.quantity"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  style="max-width: 120px; margin-right: 12px"
                  label="Quantité"
                  suffix="unité(s)"
                  :rules="quantityRules"
                  @input="updateQuantityRealtime"
                  @blur="validateAndUpdateQuantity"
                  @keydown.enter="validateAndUpdateQuantity"
                  class="quantity-field"
                />
                
                <!-- Affichage du stock disponible -->
                <v-chip
                  v-if="item.product.supplierAvailableQuantity || item.product.quantity"
                  color="info"
                  variant="outlined"
                  size="small"
                >
                  <v-icon left size="small">mdi-package-variant</v-icon>
                  Stock: {{ formatQuantity(item.product.supplierAvailableQuantity || item.product.quantity) }}
                </v-chip>
              </div>
              
              <!-- Total pour cet article -->
              <div class="text-right mt-2">
                <div class="text-caption text-grey mb-1">
                  {{ localQuantity.value || item.quantity }} × {{ formatPrice(item.product.sellingPrice) }}
                </div>
                <span class="text-h6 font-weight-bold text-primary">
                  Total: {{ formatPrice(calculateItemTotal()) }}
                </span>
              </div>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getDefaultProductImage } from '@/utils/defaultImages'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update-quantity', 'remove-item'])

// Variable locale pour gérer la quantité
const localQuantity = ref(props.item.quantity)

// Watcher pour synchroniser avec les changements externes
watch(() => props.item.quantity, (newValue) => {
  localQuantity.value = newValue
})

const getProductImage = (product) => {
  if (product?.images && product.images.length > 0) {
    return product.images[0].imageUrl || product.images[0].url || product.images[0]
  }
  return getDefaultProductImage(product)
}

// Règles de validation pour la quantité
const quantityRules = [
  (v) => !!v || 'Quantité requise',
  (v) => v >= 1 || 'Minimum 1 unité',
  (v) => v <= (props.item.product.supplierAvailableQuantity || props.item.product.quantity) || `Maximum ${props.item.product.supplierAvailableQuantity || props.item.product.quantity} unités`
]

// Mise à jour en temps réel (pendant la saisie)
const updateQuantityRealtime = () => {
  const quantity = parseInt(localQuantity.value) || 1
  const maxQuantity = props.item.product.supplierAvailableQuantity || props.item.product.quantity
  
  // Validation des limites
  if (quantity < 1) {
    localQuantity.value = 1
  } else if (quantity > maxQuantity) {
    localQuantity.value = maxQuantity
  }
  
  // Mise à jour immédiate si la quantité est valide
  if (quantity >= 1 && quantity <= maxQuantity && quantity !== props.item.quantity) {
    emit('update-quantity', quantity)
  }
}

// Validation et mise à jour finale (au blur/enter)
const validateAndUpdateQuantity = () => {
  const quantity = parseInt(localQuantity.value) || 1
  const maxQuantity = props.item.product.supplierAvailableQuantity || props.item.product.quantity
  
  if (quantity < 1) {
    localQuantity.value = 1
    emit('update-quantity', 1)
  } else if (quantity > maxQuantity) {
    localQuantity.value = maxQuantity
    emit('update-quantity', maxQuantity)
  } else if (quantity !== props.item.quantity) {
    emit('update-quantity', quantity)
  }
}

// Calcul du total pour cet article
const calculateItemTotal = () => {
  const unitPrice = props.item.product?.sellingPrice || 0
  const quantity = localQuantity.value || props.item.quantity
  return unitPrice * quantity
}


const removeItem = () => {
  emit('remove-item')
}

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

// Fonction de formatage du poids
const formatWeight = (weight) => {
  if (!weight) return '0'
  return new Intl.NumberFormat('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3
  }).format(weight)
}

// Fonction de formatage des quantités
const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
}
</script>

<style scoped>
.quantity-field {
  text-align: center;
}

.quantity-field :deep(.v-field__input) {
  text-align: center;
  font-weight: 600;
  font-size: 16px;
  color: #1976d2;
}

.quantity-field :deep(.v-field__suffix) {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.quantity-field :deep(.v-field__outline) {
  border-color: #1976d2;
}

.quantity-field :deep(.v-field--focused .v-field__outline) {
  border-color: #1976d2;
  border-width: 2px;
}

.quantity-field :deep(.v-field--error .v-field__outline) {
  border-color: #f44336;
}

.quantity-field :deep(.v-field--error .v-field__input) {
  color: #f44336;
}
</style>
