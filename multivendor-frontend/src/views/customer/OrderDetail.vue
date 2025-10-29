<template>
  <v-container v-if="!loading && order">
    <v-row>
      <v-col cols="12">
        <v-btn variant="text" @click="$router.back()" class="mb-4">
          <v-icon left>mdi-arrow-left</v-icon>
          Retour
        </v-btn>
        
        <h1 class="text-h4 mb-6">Détails de la commande</h1>
      </v-col>
    </v-row>

    <v-row>
      <!-- Informations principales -->
      <v-col cols="12" md="8">
        <v-card class="mb-4">
          <v-card-title class="d-flex align-center justify-space-between">
            <div class="d-flex align-center">
              <v-icon class="mr-2" :color="getStatusColor(order.orderStatus)">mdi-package-variant</v-icon>
              <div>
                <div class="text-h5">Commande #{{ order.orderId }}</div>
                <div class="text-caption text-grey">{{ formatDate(order.orderDate) }}</div>
              </div>
            </div>
            <v-chip :color="getStatusColor(order.orderStatus)" size="large">
              {{ order.orderStatus }}
            </v-chip>
          </v-card-title>
          
          <v-card-text>
            <v-divider class="my-3" />
            
            <!-- Articles de la commande -->
            <h3 class="text-h6 mb-3">
              <v-icon class="mr-2">mdi-cart</v-icon>
              Articles commandés ({{ order.totalItem }})
            </h3>
            
            <div v-if="order.orderItems && order.orderItems.length > 0" class="mb-4">
              <v-list>
                <v-list-item
                  v-for="(item, index) in order.orderItems"
                  :key="index"
                  class="px-0"
                >
                  <template v-slot:prepend>
                    <v-avatar size="60" class="mr-3">
                      <v-img
                        v-if="item.product?.firstProductImage"
                        :src="item.product.firstProductImage"
                        :alt="item.product.title"
                      />
                      <v-icon v-else color="grey">mdi-image-off</v-icon>
                    </v-avatar>
                  </template>
                  
                  <v-list-item-title class="text-h6">
                    {{ item.product?.title || 'Produit' }}
                  </v-list-item-title>
                  
                  <v-list-item-subtitle>
                    <div class="d-flex align-center mt-1">
                      <v-chip size="x-small" class="mr-2" v-if="item.size">
                        Taille: {{ item.size }}
                      </v-chip>
                      <span class="text-grey">Quantité: {{ item.quantity }}</span>
                    </div>
                  </v-list-item-subtitle>
                  
                  <template v-slot:append>
                    <div class="text-right">
                      <div class="text-h6 text-primary">
                        {{ formatPrice(item.sellingPrice * item.quantity) }}
                      </div>
                      <div class="text-caption text-grey" v-if="item.mrpPrice && item.mrpPrice > item.sellingPrice">
                        <s>{{ formatPrice(item.mrpPrice * item.quantity) }}</s>
                      </div>
                    </div>
                  </template>
                </v-list-item>
              </v-list>
            </div>
            
            <div v-else class="text-center py-4">
              <v-icon size="48" color="grey">mdi-cart-off</v-icon>
              <p class="text-grey mt-2">Aucun article dans cette commande</p>
            </div>
          </v-card-text>
        </v-card>

        <!-- Adresse de livraison -->
        <v-card v-if="order.shippingAddress" class="mb-4">
          <v-card-title>
            <v-icon class="mr-2">mdi-map-marker</v-icon>
            Adresse de livraison
          </v-card-title>
          <v-card-text>
            <div v-if="order.shippingAddress.name">
              <strong>{{ order.shippingAddress.name }}</strong>
            </div>
            <div v-if="order.shippingAddress.mobile">
              <v-icon size="small" class="mr-1">mdi-phone</v-icon>
              {{ order.shippingAddress.mobile }}
            </div>
            <div v-if="order.shippingAddress.address" class="mt-2">
              {{ order.shippingAddress.address }}
            </div>
            <div v-if="order.shippingAddress.city || order.shippingAddress.pinCode">
              {{ order.shippingAddress.city }}, {{ order.shippingAddress.pinCode }}
            </div>
          </v-card-text>
        </v-card>

        <!-- Informations de livraison -->
        <v-card v-if="order.deliveryPerson" class="mb-4">
          <v-card-title>
            <v-icon class="mr-2">mdi-truck-delivery</v-icon>
            Informations de livraison
          </v-card-title>
          <v-card-text>
            <div class="mb-2">
              <strong>Livreur:</strong> {{ order.deliveryPerson.fullName }}
            </div>
            <div v-if="order.deliveryPerson.mobile">
              <v-icon size="small" class="mr-1">mdi-phone</v-icon>
              {{ order.deliveryPerson.mobile }}
            </div>
            <div class="mt-2" v-if="order.deliveryNotes">
              <strong>Notes:</strong> {{ order.deliveryNotes }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <v-card sticky>
          <v-card-title>Résumé de la commande</v-card-title>
          
          <v-card-text>
            <div class="mb-3">
              <div class="d-flex justify-space-between mb-2">
                <span class="text-body-2">
                  <v-icon size="small" class="mr-1">mdi-cart</v-icon>
                  Sous-total
                </span>
                <span class="text-body-2 font-weight-bold">
                  {{ formatPrice(order.totalMrpPrice) }}
                </span>
              </div>
              
              <div v-if="order.discount > 0" class="d-flex justify-space-between mb-2">
                <span class="text-body-2 text-success">
                  <v-icon size="small" class="mr-1">mdi-tag</v-icon>
                  Réduction
                </span>
                <span class="text-body-2 text-success font-weight-bold">
                  -{{ formatPrice(order.discount) }}
                </span>
              </div>
              
              <v-divider class="my-3" />
              
              <div class="d-flex justify-space-between">
                <span class="text-h6 font-weight-bold">Total</span>
                <span class="text-h5 font-weight-bold text-primary">
                  {{ formatPrice(order.totalSellingPrice) }}
                </span>
              </div>
            </div>
            
            <v-divider class="my-3" />
            
            <div class="text-caption text-grey">
              <div class="mb-1">
                <strong>Date de commande:</strong> {{ formatDate(order.orderDate) }}
              </div>
              <div v-if="order.deliverDate">
                <strong>Livraison prévue:</strong> {{ formatDate(order.deliverDate) }}
              </div>
              <div v-if="order.deliveryDate">
                <strong>Date de livraison:</strong> {{ formatDate(order.deliveryDate) }}
              </div>
            </div>
          </v-card-text>
          
          <v-card-actions>
            <v-btn
              v-if="order.orderStatus === 'PENDING'"
              color="warning"
              variant="outlined"
              block
              @click="cancelOrder(order.id)"
            >
              <v-icon left>mdi-cancel</v-icon>
              Annuler la commande
            </v-btn>
            <!-- Ne permettre la suppression que pour les commandes PENDING -->
            <v-btn
              v-if="order.orderStatus === 'PENDING'"
              color="error"
              variant="outlined"
              block
              @click="showDeleteDialog(order.id)"
              :loading="deleting"
              class="mt-2"
            >
              <v-icon left>mdi-delete</v-icon>
              Supprimer la commande
            </v-btn>
            <!-- Message informatif si la commande est confirmée -->
            <v-alert
              v-if="order.orderStatus !== 'PENDING' && order.orderStatus !== 'CANCELLED'"
              type="info"
              variant="tonal"
              class="mt-2"
            >
              <div class="d-flex align-center">
                <v-icon class="mr-2">mdi-information</v-icon>
                <span>Cette commande est {{ order.orderStatus === 'CONFIRMED' ? 'confirmée' : order.orderStatus.toLowerCase() }} et ne peut plus être supprimée.</span>
              </div>
            </v-alert>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Modal de confirmation pour suppression -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5">
          <v-icon class="mr-2" color="error">mdi-alert-circle</v-icon>
          Supprimer la commande
        </v-card-title>
        <v-card-text>
          <p>Êtes-vous sûr de vouloir supprimer cette commande ?</p>
          <p class="text-caption text-grey">Cette action est irréversible.</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="outlined" @click="showDeleteDialog = false">
            Annuler
          </v-btn>
          <v-btn color="error" @click="confirmDelete" :loading="deleting">
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>

  <!-- État de chargement -->
  <v-container v-else-if="loading" class="text-center">
    <v-progress-circular indeterminate color="primary" size="64" />
    <p class="mt-4 text-grey">Chargement des détails...</p>
  </v-container>

  <!-- État d'erreur -->
  <v-container v-else class="text-center">
    <v-icon size="64" color="error">mdi-alert-circle</v-icon>
    <p class="mt-4 text-h6">Commande non trouvée</p>
    <v-btn class="mt-4" to="/customer/orders">Retour aux commandes</v-btn>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/orders'
import { useNotification } from '@/composables/useNotification'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()
const { showNotification } = useNotification()

const order = ref(null)
const loading = ref(true)
const deleting = ref(false)
const showDeleteDialog = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price).replace('XOF', 'FCFA')
}

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'CONFIRMED': 'blue',
    'SHIPPED': 'purple',
    'DELIVERED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const cancelOrder = async (orderId) => {
  try {
    await orderStore.cancelOrder(orderId)
    showNotification('✅ Commande annulée avec succès', 'success')
    await fetchOrderDetails()
  } catch (error) {
    console.error('Erreur lors de l\'annulation:', error)
    showNotification('❌ Erreur lors de l\'annulation de la commande', 'error')
  }
}

const confirmDelete = async () => {
  deleting.value = true
  try {
    await orderStore.deleteOrder(order.value.id)
    showNotification('🗑️ Commande supprimée avec succès', 'success')
    router.push('/customer/orders')
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
    showNotification('❌ Erreur lors de la suppression de la commande', 'error')
  } finally {
    deleting.value = false
    showDeleteDialog.value = false
  }
}

const fetchOrderDetails = async () => {
  loading.value = true
  try {
    const orderId = route.params.id
    const fetchedOrder = await orderStore.fetchOrderById(orderId)
    order.value = fetchedOrder
  } catch (error) {
    console.error('Erreur lors de la récupération des détails:', error)
    showNotification('Erreur lors de la récupération des détails', 'error')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchOrderDetails()
})
</script>

<style scoped>
.order-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.order-card:hover {
  transform: translateY(-2px);
}
</style>
