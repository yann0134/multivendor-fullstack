<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between align-center mb-6">
          <div>
            <h1 class="text-h4">Mes Commandes</h1>
            <p class="text-body-2 text-grey">Historique de toutes vos commandes</p>
          </div>
          <v-btn
            v-if="orderStore.orders.length > 0"
            color="error"
            variant="outlined"
            @click="showDeleteAllDialog = true"
            :loading="deletingAll"
          >
            <v-icon left>mdi-delete-sweep</v-icon>
            Supprimer toutes
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Filtres de statut -->
    <v-row class="mb-4" v-if="orderStore.orders.length > 0">
      <v-col cols="12">
        <v-btn-toggle v-model="statusFilter" mandatory density="comfortable" color="primary" class="mb-4">
          <v-btn value="all">
            <v-icon start>mdi-view-list</v-icon>
            Toutes ({{ orderStore.orders.length }})
          </v-btn>
          <v-btn value="PENDING">
            <v-icon start>mdi-clock-outline</v-icon>
            En attente ({{ stats.pendingOrders }})
          </v-btn>
          <v-btn value="CONFIRMED">
            <v-icon start>mdi-check-circle</v-icon>
            Confirmées ({{ stats.confirmedOrders }})
          </v-btn>
          <v-btn value="SHIPPED">
            <v-icon start>mdi-package-variant</v-icon>
            Expédiées ({{ stats.shippedOrders }})
          </v-btn>
          <v-btn value="DELIVERED">
            <v-icon start>mdi-check-all</v-icon>
            Livrées ({{ stats.deliveredOrders }})
          </v-btn>
        </v-btn-toggle>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mb-4" v-if="orderStore.orders.length > 0 && statusFilter === 'all'">
      <v-col cols="12" md="3">
        <v-card color="primary" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-package-variant-closed</v-icon>
            <div class="text-h4">{{ stats.totalOrders }}</div>
            <div class="text-body-2">Total</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="orange" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-clock-outline</v-icon>
            <div class="text-h4">{{ stats.pendingOrders }}</div>
            <div class="text-body-2">En attente</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="blue" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
            <div class="text-h4">{{ stats.confirmedOrders }}</div>
            <div class="text-body-2">Confirmées</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="green" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
            <div class="text-h4">{{ stats.deliveredOrders }}</div>
            <div class="text-body-2">Livrées</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="orderStore.orders.length === 0">
      <v-col cols="12" class="text-center">
        <v-icon size="120" color="grey">mdi-clipboard-outline</v-icon>
        <h2 class="text-h5 mb-4">Aucune commande</h2>
        <p class="text-grey mb-6">Vous n'avez pas encore passé de commande</p>
        <v-btn color="primary" to="/customer/products" size="large">
          <v-icon left>mdi-shopping</v-icon>
          Voir les produits
        </v-btn>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col v-for="order in filteredOrders" :key="order.id" cols="12" md="6">
        <v-card class="order-card" elevation="3" @click="$router.push(`/customer/order/${order.id}`)" style="cursor: pointer;">
          <v-card-title class="d-flex align-center justify-space-between">
            <div class="d-flex align-center">
              <v-icon class="mr-2" :color="getStatusColor(order.orderStatus)">mdi-package-variant</v-icon>
              <div>
                <div class="text-h6">Commande #{{ order.orderId }}</div>
                <div class="text-caption text-grey">{{ formatDate(order.orderDate) }}</div>
              </div>
            </div>
            <v-chip :color="getStatusColor(order.orderStatus)" size="small">
              {{ order.orderStatus }}
            </v-chip>
          </v-card-title>
          
          <v-card-text>
            <div class="mb-3">
              <div class="d-flex justify-space-between mb-2">
                <span class="text-body-2 text-grey">
                  <v-icon size="small" class="mr-1">mdi-cart</v-icon>
                  Articles
                </span>
                <span class="text-body-1 font-weight-bold">{{ order.totalItem }} article(s)</span>
              </div>
              
              <v-divider class="my-2" />
              
              <div class="d-flex justify-space-between mb-2">
                <span class="text-body-2 text-grey">
                  <v-icon size="small" class="mr-1">mdi-currency-cny</v-icon>
                  Total
                </span>
                <span class="text-h6 font-weight-bold text-primary">{{ formatPrice(order.totalSellingPrice) }}</span>
              </div>
              
              <div v-if="order.discount > 0" class="d-flex justify-space-between">
                <span class="text-body-2 text-success">
                  <v-icon size="small" class="mr-1">mdi-tag</v-icon>
                  Réduction
                </span>
                <span class="text-body-2 text-success font-weight-bold">-{{ formatPrice(order.discount) }}</span>
              </div>
              
              <v-divider class="my-2" />
              
              <!-- Statut de livraison -->
              <div class="d-flex align-center mb-2">
                <v-icon size="small" class="mr-1" :color="getDeliveryStatusColor(order.deliveryStatus)">
                  {{ getDeliveryStatusIcon(order.deliveryStatus) }}
                </v-icon>
                <v-chip :color="getDeliveryStatusColor(order.deliveryStatus)" size="x-small" variant="outlined">
                  {{ getDeliveryStatusText(order.deliveryStatus) }}
                </v-chip>
              </div>
              
              <!-- Livreur assigné -->
              <div v-if="order.deliveryPerson || order.deliveryUserId" class="d-flex align-center mb-2">
                <v-icon size="small" class="mr-1" color="primary">mdi-truck-delivery</v-icon>
                <span class="text-caption text-grey">
                  Livreur: {{ order.deliveryPerson?.fullName || 'En attente d\'assignation' }}
                </span>
              </div>
              
              <!-- Téléphone du livreur -->
              <div v-if="order.deliveryPerson?.mobile" class="d-flex align-center mb-2">
                <v-icon size="small" class="mr-1">mdi-phone</v-icon>
                <span class="text-caption text-grey">{{ order.deliveryPerson.mobile }}</span>
              </div>
              
              <!-- Notes de livraison -->
              <div v-if="order.deliveryNotes" class="mt-2">
                <v-icon size="small" class="mr-1">mdi-note-text</v-icon>
                <span class="text-caption text-grey">{{ order.deliveryNotes }}</span>
              </div>
            </div>
          </v-card-text>
          
          <v-card-actions>
            <v-btn
              color="primary"
              variant="text"
              @click.stop="$router.push(`/customer/order/${order.id}`)"
            >
              <v-icon left>mdi-eye</v-icon>
              Détails
            </v-btn>
            <v-btn
              v-if="order.deliveryPerson || order.deliveryUserId"
              color="info"
              variant="text"
              size="small"
              @click.stop="viewDeliveryStatus(order)"
            >
              <v-icon left size="small">mdi-truck-fast</v-icon>
              Suivre
            </v-btn>
            <v-spacer />
            <v-btn
              v-if="order.orderStatus === 'PENDING'"
              color="warning"
              variant="outlined"
              size="small"
              @click.stop="cancelOrder(order.id)"
            >
              <v-icon left size="small">mdi-cancel</v-icon>
              Annuler
            </v-btn>
            <!-- Ne permettre la suppression que pour les commandes PENDING -->
            <v-btn
              v-if="order.orderStatus === 'PENDING'"
              color="error"
              variant="outlined"
              size="small"
              @click.stop="showDeleteDialog(order.id)"
              :loading="deletingOrder === order.id"
            >
              <v-icon size="small">mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Modal de confirmation pour suppression individuelle -->
    <v-dialog v-model="showDeleteSingleDialog" max-width="500">
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
          <v-btn variant="outlined" @click="showDeleteSingleDialog = false">
            Annuler
          </v-btn>
          <v-btn color="error" @click="confirmDeleteOrder" :loading="deletingOrder">
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Modal de confirmation pour suppression de toutes les commandes -->
    <v-dialog v-model="showDeleteAllDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5">
          <v-icon class="mr-2" color="error">mdi-alert-circle</v-icon>
          Supprimer toutes les commandes
        </v-card-title>
        <v-card-text>
          <p>Êtes-vous sûr de vouloir supprimer <strong>toutes vos commandes</strong> ?</p>
          <p class="text-caption text-grey">Cette action est irréversible et supprimera {{ orderStore.orders.length }} commande(s).</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="outlined" @click="showDeleteAllDialog = false">
            Annuler
          </v-btn>
          <v-btn color="error" @click="confirmDeleteAllOrders" :loading="deletingAll">
            Supprimer toutes
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/orders'
import { useNotification } from '@/composables/useNotification'

const router = useRouter()
const orderStore = useOrderStore()
const { showNotification } = useNotification()

// Variables réactives pour les modales et états de chargement
const showDeleteSingleDialog = ref(false)
const showDeleteAllDialog = ref(false)
const deletingOrder = ref(null)
const deletingAll = ref(false)
const orderToDelete = ref(null)
const statusFilter = ref('all')

// Calculer les statistiques
const stats = computed(() => {
  const orders = orderStore.orders
  return {
    totalOrders: orders.length,
    pendingOrders: orders.filter(o => o.orderStatus === 'PENDING').length,
    confirmedOrders: orders.filter(o => o.orderStatus === 'CONFIRMED').length,
    shippedOrders: orders.filter(o => o.orderStatus === 'SHIPPED').length,
    deliveredOrders: orders.filter(o => o.orderStatus === 'DELIVERED').length
  }
})

// Filtrer les commandes
const filteredOrders = computed(() => {
  if (statusFilter.value === 'all') {
    return orderStore.orders
  }
  return orderStore.orders.filter(order => order.orderStatus === statusFilter.value)
})

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

const getDeliveryStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'ASSIGNED': 'blue',
    'PICKED_UP': 'purple',
    'IN_TRANSIT': 'indigo',
    'DELIVERED': 'green',
    'FAILED': 'red'
  }
  return colors[status] || 'grey'
}

const getDeliveryStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'ASSIGNED': 'Assignée',
    'PICKED_UP': 'Récupérée',
    'IN_TRANSIT': 'En transit',
    'DELIVERED': 'Livrée',
    'FAILED': 'Échouée'
  }
  return texts[status] || status
}

const getDeliveryStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock-outline',
    'ASSIGNED': 'mdi-truck-outline',
    'PICKED_UP': 'mdi-package-up',
    'IN_TRANSIT': 'mdi-truck-delivery',
    'DELIVERED': 'mdi-check-circle',
    'FAILED': 'mdi-alert-circle'
  }
  return icons[status] || 'mdi-help-circle'
}

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

const cancelOrder = async (orderId) => {
  try {
    await orderStore.cancelOrder(orderId)
    await orderStore.fetchUserOrders()
    showNotification('Commande annulée avec succès', 'success')
  } catch (error) {
    console.error('Erreur lors de l\'annulation de la commande:', error)
    showNotification('Erreur lors de l\'annulation de la commande', 'error')
  }
}

const showDeleteDialog = (orderId) => {
  orderToDelete.value = orderId
  showDeleteSingleDialog.value = true
}

const confirmDeleteOrder = async () => {
  if (!orderToDelete.value) return
  
  deletingOrder.value = orderToDelete.value
  try {
    const result = await orderStore.deleteOrder(orderToDelete.value)
    showNotification('🗑️ ' + result.message, 'success')
    showDeleteSingleDialog.value = false
    orderToDelete.value = null
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
    showNotification('❌ Erreur lors de la suppression de la commande', 'error')
  } finally {
    deletingOrder.value = null
  }
}

const confirmDeleteAllOrders = async () => {
  deletingAll.value = true
  try {
    const result = await orderStore.deleteAllOrders()
    showNotification('🗑️ ' + result.message, 'success')
    showDeleteAllDialog.value = false
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
    showNotification('❌ Erreur lors de la suppression des commandes', 'error')
  } finally {
    deletingAll.value = false
  }
}

const viewDeliveryStatus = (order) => {
  // Afficher une notification pour le suivi
  const deliveryInfo = order.deliveryPerson 
    ? `Livreur: ${order.deliveryPerson.fullName} - Statut: ${getDeliveryStatusText(order.deliveryStatus)}`
    : 'En attente d\'assignation d\'un livreur'
  
  showNotification(deliveryInfo, 'info')
  
  // Optionnel : Rediriger vers la page de détail pour plus d'info
  if (order.deliveryPerson) {
    setTimeout(() => {
      router.push(`/customer/order/${order.id}`)
    }, 2000)
  }
}

// Détecter les changements de statut des commandes
const checkOrderStatusChanges = () => {
  let previousOrders = [...orderStore.orders]
  
  // Polling toutes les 30 secondes pour vérifier les changements
  setInterval(async () => {
    await orderStore.fetchUserOrders()
    
    // Vérifier les changements de statut
    orderStore.orders.forEach(order => {
      const previousOrder = previousOrders.find(o => o.id === order.id)
      
      if (previousOrder && previousOrder.orderStatus !== order.orderStatus) {
        // Commande confirmée
        if (order.orderStatus === 'CONFIRMED' && previousOrder.orderStatus !== 'CONFIRMED') {
          showNotification('🎉 Votre commande #' + order.orderId + ' a été confirmée !', 'success')
        }
        
        // Commande expédiée
        if (order.orderStatus === 'SHIPPED' && previousOrder.orderStatus !== 'SHIPPED') {
          showNotification('📦 Votre commande #' + order.orderId + ' a été expédiée !', 'info')
        }
        
        // Commande livrée
        if (order.orderStatus === 'DELIVERED' && previousOrder.orderStatus !== 'DELIVERED') {
          showNotification('✅ Votre commande #' + order.orderId + ' a été livrée !', 'success')
        }
        
        // Livreur assigné
        if (order.deliveryPerson && !previousOrder.deliveryPerson) {
          showNotification('🚚 Un livreur a été assigné à votre commande #' + order.orderId, 'info')
        }
      }
    })
    
    // Mettre à jour les commandes précédentes
    previousOrders = [...orderStore.orders]
  }, 30000) // Vérifier toutes les 30 secondes
}

onMounted(async () => {
  await orderStore.fetchUserOrders()
  
  // Démarrer la surveillance des changements de statut
  checkOrderStatusChanges()
})
</script>

<style scoped>
.order-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.15) !important;
}
</style>
