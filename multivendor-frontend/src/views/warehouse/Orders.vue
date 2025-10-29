<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">📋 Commandes à Préparer</h1>
        <p class="text-body-1 text-grey-600 mb-6">
          Gérez les commandes clients et affectez les livreurs pour la livraison
        </p>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mb-6">
      <v-col cols="12" md="3">
        <v-card color="orange" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-clock-outline</v-icon>
            <div class="text-h4">{{ pendingOrdersCount }}</div>
            <div class="text-body-2">À préparer</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="blue" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-package-variant-closed</v-icon>
            <div class="text-h4">{{ readyOrdersCount }}</div>
            <div class="text-body-2">Prêtes</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="green" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-truck-delivery</v-icon>
            <div class="text-h4">{{ assignedOrdersCount }}</div>
            <div class="text-body-2">Assignées</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card color="purple" dark>
          <v-card-text class="text-center">
            <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
            <div class="text-h4">{{ deliveredOrdersCount }}</div>
            <div class="text-body-2">Livrées</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Filtres et recherche -->
    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          label="Filtrer par statut"
          clearable
          prepend-icon="mdi-filter"
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-text-field
          v-model="searchQuery"
          label="Rechercher par client ou commande"
          prepend-icon="mdi-magnify"
          clearable
        />
      </v-col>
      <v-col cols="12" md="4" class="d-flex align-center">
        <v-btn
          color="primary"
          prepend-icon="mdi-refresh"
          @click="loadOrders"
          :loading="loading"
        >
          Actualiser
        </v-btn>
      </v-col>
    </v-row>

    <!-- Affichage des commandes -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-clipboard-list</v-icon>
            Commandes Clients
            <v-spacer />
            <v-chip color="primary" variant="outlined">
              {{ filteredOrders.length }} commande(s)
            </v-chip>
          </v-card-title>
          
          <!-- Vue en cartes pour les commandes -->
          <v-card-text v-if="!loading && filteredOrders.length > 0">
            <v-row>
              <v-col 
                v-for="order in filteredOrders" 
                :key="order.id" 
                cols="12" 
                md="6" 
                lg="4"
              >
                <v-card 
                  class="order-card mb-4" 
                  elevation="2"
                  :class="getOrderCardClass(order.orderStatus)"
                >
                  <!-- En-tête de la commande -->
                  <v-card-title class="pb-2">
                    <div class="d-flex align-center justify-space-between w-100">
                      <div class="d-flex align-center">
                        <v-avatar 
                          size="40" 
                          :color="getStatusColor(order.orderStatus)" 
                          class="mr-3"
                        >
                          <v-icon color="white">{{ getStatusIcon(order.orderStatus) }}</v-icon>
                        </v-avatar>
                        <div>
                          <div class="text-h6">#{{ order.orderId }}</div>
                          <div class="text-caption text-grey">{{ formatDate(order.orderDate) }}</div>
                        </div>
                      </div>
                      <v-chip 
                        :color="getStatusColor(order.orderStatus)" 
                        size="small"
                        variant="flat"
                      >
                        {{ getStatusText(order.orderStatus) }}
                      </v-chip>
                    </div>
                  </v-card-title>

                  <!-- Informations client -->
                  <v-card-text class="pt-0">
                    <div class="mb-3">
                      <div class="d-flex align-center mb-2">
                        <v-icon size="small" class="mr-2">mdi-account</v-icon>
                        <div>
                          <div class="font-weight-medium">{{ order.user?.fullName || 'Client' }}</div>
                          <div class="text-caption text-grey">{{ order.user?.email }}</div>
                        </div>
                      </div>
                      
                      <v-divider class="my-2" />
                      
                      <!-- Informations de livraison -->
                      <div class="mb-2">
                        <div class="d-flex align-center mb-1">
                          <v-icon size="small" class="mr-2">mdi-truck-delivery</v-icon>
                          <span class="text-body-2">Livraison</span>
                        </div>
                        <v-chip 
                          :color="getDeliveryStatusColor(order.deliveryStatus)" 
                          size="x-small"
                          variant="outlined"
                          class="ml-6"
                        >
                          {{ getDeliveryStatusText(order.deliveryStatus) }}
                        </v-chip>
                      </div>
                      
                      <!-- Livreur assigné -->
                      <div v-if="order.deliveryPerson" class="mb-2">
                        <div class="d-flex align-center">
                          <v-icon size="small" class="mr-2">mdi-truck</v-icon>
                          <div>
                            <div class="text-body-2 font-weight-medium">{{ order.deliveryPerson.fullName }}</div>
                            <div class="text-caption text-grey">{{ order.deliveryPerson.mobile }}</div>
                          </div>
                        </div>
                      </div>
                      
                      <div v-else class="mb-2">
                        <v-chip color="grey" size="x-small" variant="outlined">
                          <v-icon start size="small">mdi-truck-off</v-icon>
                          Aucun livreur assigné
                        </v-chip>
                      </div>
                      
                      <v-divider class="my-2" />
                      
                      <!-- Résumé financier -->
                      <div class="d-flex justify-space-between align-center">
                        <div>
                          <div class="text-body-2 text-grey">Total</div>
                          <div class="text-h6 font-weight-bold text-primary">
                            {{ formatPrice(order.totalSellingPrice) }}
                          </div>
                        </div>
                        <div class="text-right">
                          <div class="text-body-2 text-grey">{{ order.totalItem }} article(s)</div>
                          <div v-if="order.discount > 0" class="text-caption text-success">
                            -{{ formatPrice(order.discount) }} de réduction
                          </div>
                        </div>
                      </div>
                    </div>
                  </v-card-text>

                  <!-- Actions -->
                  <v-card-actions class="pt-0">
                    <v-btn
                      color="info"
                      variant="text"
                      size="small"
                      @click="viewOrderDetails(order)"
                    >
                      <v-icon start size="small">mdi-eye</v-icon>
                      Détails
                    </v-btn>
                    
                    <v-spacer />
                    
                    <!-- Marquer comme prêt -->
                    <v-btn
                      v-if="order.orderStatus === 'PENDING'"
                      color="success"
                      size="small"
                      variant="outlined"
                      @click="markAsReady(order)"
                      :loading="order.processing"
                    >
                      <v-icon start size="small">mdi-check</v-icon>
                      Prêt
                    </v-btn>

                    <!-- Affecter livreur -->
                    <v-btn
                      v-if="order.orderStatus === 'CONFIRMED' && !order.deliveryPerson"
                      color="primary"
                      size="small"
                      variant="outlined"
                      @click="openAssignDialog(order)"
                    >
                      <v-icon start size="small">mdi-truck</v-icon>
                      Assigner
                    </v-btn>

                    <!-- Changer livreur -->
                    <v-btn
                      v-if="order.deliveryPerson"
                      color="orange"
                      size="small"
                      variant="outlined"
                      @click="openAssignDialog(order)"
                    >
                      <v-icon start size="small">mdi-swap-horizontal</v-icon>
                      Changer
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>

          <!-- État vide -->
          <v-card-text v-else-if="!loading && filteredOrders.length === 0" class="text-center py-8">
            <v-icon size="64" color="grey">mdi-clipboard-outline</v-icon>
            <h3 class="text-h6 mt-4">Aucune commande trouvée</h3>
            <p class="text-grey">Aucune commande ne correspond aux critères de recherche</p>
          </v-card-text>

          <!-- État de chargement -->
          <v-card-text v-else-if="loading" class="text-center py-8">
            <v-progress-circular indeterminate color="primary" size="64" />
            <p class="mt-4 text-grey">Chargement des commandes...</p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog d'affectation de livreur -->
    <v-dialog v-model="assignDialog" max-width="600">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2">mdi-truck</v-icon>
          Affecter un livreur
          <v-spacer />
          <v-btn icon @click="assignDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <div class="mb-4">
            <h3 class="text-h6 mb-2">Commande #{{ selectedOrder?.orderId }}</h3>
            <p class="text-body-2 text-grey-600">
              Client: {{ selectedOrder?.user?.fullName }} ({{ selectedOrder?.user?.email }})
            </p>
            <p class="text-body-2 text-grey-600">
              Montant: {{ formatPrice(selectedOrder?.totalSellingPrice) }}
            </p>
          </div>

          <v-select
            v-model="selectedDeliveryPerson"
            :items="deliveryPersons"
            item-title="fullName"
            item-value="id"
            label="Sélectionner un livreur"
            prepend-icon="mdi-truck"
            return-object
            :loading="false"
          >
            <template v-slot:item="{ props, item }">
              <v-list-item v-bind="props">
                <template v-slot:prepend>
                  <v-avatar color="green" size="32">
                    <v-icon>mdi-truck</v-icon>
                  </v-avatar>
                </template>
                <v-list-item-title>{{ item.raw.fullName }}</v-list-item-title>
                <v-list-item-subtitle>{{ item.raw.mobile }} - {{ item.raw.email }}</v-list-item-subtitle>
              </v-list-item>
            </template>
            <template v-slot:no-data>
              <div class="text-center py-4">
                <v-icon color="grey">mdi-truck-off</v-icon>
                <p class="text-caption text-grey mt-2">Aucun livreur disponible</p>
              </div>
            </template>
          </v-select>
          
          <!-- Message de débogage -->
          <div v-if="deliveryPersons.length === 0" class="text-caption text-orange mt-2">
            <v-icon size="small">mdi-alert</v-icon>
            Aucun livreur disponible. Veuillez créer des livreurs.
          </div>

          <v-textarea
            v-model="deliveryNotes"
            label="Notes de livraison (optionnel)"
            prepend-icon="mdi-note-text"
            rows="3"
            placeholder="Instructions spéciales pour le livreur..."
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="assignDialog = false">Annuler</v-btn>
          <v-btn
            color="primary"
            @click="assignDeliveryPerson"
            :loading="assigning"
            :disabled="!selectedDeliveryPerson"
          >
            Affecter
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de détails de commande -->
    <v-dialog v-model="detailsDialog" max-width="1000" scrollable>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2">mdi-eye</v-icon>
          Détails de la commande #{{ selectedOrder?.orderId }}
          <v-spacer />
          <v-btn icon @click="detailsDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-row v-if="selectedOrder">
            <!-- Informations générales -->
            <v-col cols="12">
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-information</v-icon>
                  Informations générales
                </v-card-title>
                <v-card-text>
                  <v-row>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>Statut:</strong>
                        <v-chip :color="getStatusColor(selectedOrder.orderStatus)" size="small" class="ml-2">
                          {{ getStatusText(selectedOrder.orderStatus) }}
                        </v-chip>
                      </div>
                      <div class="mb-2">
                        <strong>Date de commande:</strong> {{ formatDate(selectedOrder.orderDate) }}
                      </div>
                      <div class="mb-2">
                        <strong>Total:</strong> {{ formatPrice(selectedOrder.totalSellingPrice) }}
                      </div>
                      <div class="mb-2">
                        <strong>Articles:</strong> {{ selectedOrder.totalItem }} article(s)
                      </div>
                    </v-col>
                    <v-col cols="12" md="6">
                      <div v-if="selectedOrder.discount > 0" class="mb-2">
                        <strong>Réduction:</strong> {{ formatPrice(selectedOrder.discount) }}
                      </div>
                      <div v-if="selectedOrder.deliverDate" class="mb-2">
                        <strong>Livraison prévue:</strong> {{ formatDate(selectedOrder.deliverDate) }}
                      </div>
                      <div v-if="selectedOrder.deliveryDate" class="mb-2">
                        <strong>Livraison effective:</strong> {{ formatDate(selectedOrder.deliveryDate) }}
                      </div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Informations client -->
            <v-col cols="12" md="6">
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-account</v-icon>
                  Informations Client
                </v-card-title>
                <v-card-text>
                  <div class="mb-2">
                    <strong>Nom:</strong> {{ selectedOrder.user?.fullName || 'Non renseigné' }}
                  </div>
                  <div class="mb-2">
                    <strong>Email:</strong> {{ selectedOrder.user?.email || 'Non renseigné' }}
                  </div>
                  <div v-if="selectedOrder.user?.mobile" class="mb-2">
                    <strong>Téléphone:</strong> {{ selectedOrder.user.mobile }}
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Adresse de livraison -->
            <v-col cols="12" md="6">
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-map-marker</v-icon>
                  Adresse de livraison
                </v-card-title>
                <v-card-text>
                  <div v-if="selectedOrder.shippingAddress">
                    <div v-if="selectedOrder.shippingAddress.name" class="mb-2">
                      <strong>{{ selectedOrder.shippingAddress.name }}</strong>
                    </div>
                    <div v-if="selectedOrder.shippingAddress.mobile" class="mb-2">
                      <v-icon size="small" class="mr-1">mdi-phone</v-icon>
                      {{ selectedOrder.shippingAddress.mobile }}
                    </div>
                    <div v-if="selectedOrder.shippingAddress.address" class="mb-2">
                      {{ selectedOrder.shippingAddress.address }}
                    </div>
                    <div v-if="selectedOrder.shippingAddress.city || selectedOrder.shippingAddress.pinCode">
                      {{ selectedOrder.shippingAddress.city }}, {{ selectedOrder.shippingAddress.pinCode }}
                    </div>
                  </div>
                  <div v-else class="text-grey">
                    Aucune adresse de livraison renseignée
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Informations de livraison -->
            <v-col cols="12" v-if="selectedOrder.deliveryPerson">
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-truck-delivery</v-icon>
                  Informations de livraison
                </v-card-title>
                <v-card-text>
                  <v-row>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>Livreur:</strong> {{ selectedOrder.deliveryPerson.fullName }}
                      </div>
                      <div class="mb-2">
                        <strong>Téléphone:</strong> {{ selectedOrder.deliveryPerson.mobile }}
                      </div>
                    </v-col>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>Statut livraison:</strong>
                        <v-chip :color="getDeliveryStatusColor(selectedOrder.deliveryStatus)" size="small" class="ml-2">
                          {{ getDeliveryStatusText(selectedOrder.deliveryStatus) }}
                        </v-chip>
                      </div>
                      <div v-if="selectedOrder.deliveryNotes" class="mb-2">
                        <strong>Notes:</strong> {{ selectedOrder.deliveryNotes }}
                      </div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Articles de la commande -->
            <v-col cols="12">
              <v-card variant="outlined">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-cart</v-icon>
                  Articles commandés ({{ selectedOrder.orderItems?.length || 0 }})
                </v-card-title>
                <v-card-text>
                  <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length > 0">
                    <v-list>
                      <v-list-item
                        v-for="(item, index) in selectedOrder.orderItems"
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
                            <div class="text-caption text-grey">
                              {{ formatPrice(item.sellingPrice) }} × {{ item.quantity }}
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
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="detailsDialog = false">Fermer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useNotification } from '@/composables/useNotification'
import { warehouseOrdersService, deliveryService } from '@/services/warehouseOrders'

const { showNotification } = useNotification()

// État réactif
const orders = ref([])
const loading = ref(false)
const statusFilter = ref(null)
const searchQuery = ref('')
const assignDialog = ref(false)
const detailsDialog = ref(false)
const selectedOrder = ref(null)
const selectedDeliveryPerson = ref(null)
const deliveryPersons = ref([])
const deliveryNotes = ref('')
const assigning = ref(false)

// Statistiques
const pendingOrdersCount = ref(0)
const readyOrdersCount = ref(0)
const assignedOrdersCount = ref(0)
const deliveredOrdersCount = ref(0)

// Options de statut
const statusOptions = [
  { title: 'En attente', value: 'PENDING' },
  { title: 'Confirmée', value: 'CONFIRMED' },
  { title: 'Expédiée', value: 'SHIPPED' },
  { title: 'Livrée', value: 'DELIVERED' },
  { title: 'Annulée', value: 'CANCELLED' }
]

// Headers du tableau
const headers = [
  { title: 'Client', key: 'client', sortable: false },
  { title: 'Commande', key: 'orderId', sortable: true },
  { title: 'Statut', key: 'orderStatus', sortable: true },
  { title: 'Livraison', key: 'deliveryStatus', sortable: true },
  { title: 'Livreur', key: 'deliveryPerson', sortable: false },
  { title: 'Montant', key: 'totalSellingPrice', sortable: true },
  { title: 'Date', key: 'orderDate', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

// Headers pour les détails des articles
const itemHeaders = [
  { title: 'Produit', key: 'product', sortable: false },
  { title: 'Quantité', key: 'quantity', sortable: true },
  { title: 'Prix unitaire', key: 'price', sortable: true },
  { title: 'Total', key: 'total', sortable: true }
]

// Commandes filtrées
const filteredOrders = computed(() => {
  let filtered = orders.value

  // Filtrage par statut
  if (statusFilter.value) {
    filtered = filtered.filter(order => order.orderStatus === statusFilter.value)
  }

  // Filtrage par recherche
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(order => 
      (order.user?.fullName?.toLowerCase().includes(query)) ||
      (order.user?.email?.toLowerCase().includes(query)) ||
      order.orderId.toLowerCase().includes(query)
    )
  }

  return filtered
})

// Méthodes utilitaires
const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'CONFIRMED': 'blue',
    'SHIPPED': 'green',
    'DELIVERED': 'success',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const getStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock-outline',
    'CONFIRMED': 'mdi-check-circle-outline',
    'SHIPPED': 'mdi-package-variant',
    'DELIVERED': 'mdi-check-circle',
    'CANCELLED': 'mdi-cancel'
  }
  return icons[status] || 'mdi-help-circle'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'CONFIRMED': 'Confirmée',
    'SHIPPED': 'Expédiée',
    'DELIVERED': 'Livrée',
    'CANCELLED': 'Annulée'
  }
  return texts[status] || status
}

const getDeliveryStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'ASSIGNED': 'blue',
    'PICKED_UP': 'purple',
    'IN_TRANSIT': 'indigo',
    'DELIVERED': 'green',
    'FAILED': 'red',
    'CANCELLED': 'grey'
  }
  return colors[status] || 'grey'
}

const getDeliveryStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock-outline',
    'ASSIGNED': 'mdi-truck-outline',
    'PICKED_UP': 'mdi-package-up',
    'IN_TRANSIT': 'mdi-truck-delivery',
    'DELIVERED': 'mdi-check-circle',
    'FAILED': 'mdi-alert-circle',
    'CANCELLED': 'mdi-cancel'
  }
  return icons[status] || 'mdi-help-circle'
}

const getDeliveryStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'ASSIGNED': 'Assignée',
    'PICKED_UP': 'Récupérée',
    'IN_TRANSIT': 'En cours',
    'DELIVERED': 'Livrée',
    'FAILED': 'Échouée',
    'CANCELLED': 'Annulée'
  }
  return texts[status] || status
}

const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0
  }).format(price)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getOrderCardClass = (status) => {
  const classes = {
    'PENDING': 'border-left-orange',
    'CONFIRMED': 'border-left-blue',
    'SHIPPED': 'border-left-green',
    'DELIVERED': 'border-left-success',
    'CANCELLED': 'border-left-red'
  }
  return classes[status] || ''
}

// Méthodes principales
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await warehouseOrdersService.getAllOrders()
    console.log('📦 Réponse de l\'API:', response)
    
    // La réponse peut être une Page (Spring Data) ou un tableau
    if (response && response.content) {
      // C'est un objet Page (Spring Data)
      orders.value = response.content
      console.log(`✅ ${response.content.length} commandes chargées depuis la pagination`)
    } else if (Array.isArray(response)) {
      // C'est déjà un tableau
      orders.value = response
      console.log(`✅ ${response.length} commandes chargées directement`)
    } else {
      console.error('Format de réponse inattendu:', response)
      orders.value = []
    }
    
    console.log('📋 Commandes dans orders.value:', orders.value)
    updateStats()
    
    showNotification('Commandes chargées avec succès', 'success')
  } catch (error) {
    console.error('Erreur lors du chargement des commandes:', error)
    showNotification('Erreur lors du chargement des commandes', 'error')
  } finally {
    loading.value = false
  }
}

const loadDeliveryPersons = async () => {
  try {
    console.log('📋 Chargement des livreurs...')
    const deliveryPersonsData = await deliveryService.getAvailableDeliveryPersons()
    console.log('📋 Livreurs reçus:', deliveryPersonsData)
    console.log('📋 Nombre de livreurs:', deliveryPersonsData?.length)
    deliveryPersons.value = deliveryPersonsData
    console.log('📋 deliveryPersons.value mis à jour avec', deliveryPersons.value.length, 'livreurs')
  } catch (error) {
    console.error('Erreur lors du chargement des livreurs:', error)
    showNotification('Erreur lors du chargement des livreurs', 'error')
  }
}

const updateStats = () => {
  pendingOrdersCount.value = orders.value.filter(o => o.orderStatus === 'PENDING').length
  readyOrdersCount.value = orders.value.filter(o => o.orderStatus === 'CONFIRMED').length
  assignedOrdersCount.value = orders.value.filter(o => o.deliveryPerson).length
  deliveredOrdersCount.value = orders.value.filter(o => o.orderStatus === 'DELIVERED').length
}

const markAsReady = async (order) => {
  try {
    order.processing = true
    console.log('🎯 Marquage de la commande comme prête:', order)
    
    const response = await warehouseOrdersService.markOrderAsReady(order.id)
    console.log('✅ Réponse du serveur:', response)
    
    // Mettre à jour l'ordre local
    const index = orders.value.findIndex(o => o.id === order.id)
    if (index !== -1) {
      orders.value[index].orderStatus = 'CONFIRMED'
      console.log('✅ Commande mise à jour dans la liste locale')
    }
    
    updateStats()
    
    showNotification(`Commande ${order.orderId} marquée comme prête`, 'success')
  } catch (error) {
    console.error('❌ Erreur lors de la mise à jour:', error)
    showNotification('Erreur lors de la mise à jour', 'error')
  } finally {
    order.processing = false
  }
}

const openAssignDialog = (order) => {
  selectedOrder.value = order
  console.log('📦 Commande sélectionnée pour affectation:', order)
  
  // Si c'est un objet, utiliser l'objet complet pour le v-select
  // Le v-select avec return-object attend l'objet complet
  if (order.deliveryPerson && typeof order.deliveryPerson === 'object') {
    selectedDeliveryPerson.value = order.deliveryPerson
  } else if (order.deliveryPerson) {
    // Si c'est juste un ID, trouver l'objet dans la liste
    selectedDeliveryPerson.value = deliveryPersons.value.find(dp => dp.id === order.deliveryPerson) || null
  } else {
    selectedDeliveryPerson.value = null
  }
  
  console.log('📦 Livreur sélectionné:', selectedDeliveryPerson.value)
  
  deliveryNotes.value = order.deliveryNotes || ''
  assignDialog.value = true
}

const assignDeliveryPerson = async () => {
  if (!selectedDeliveryPerson.value) return

  try {
    assigning.value = true
    
    // Extraire l'ID du livreur si c'est un objet
    let deliveryPersonId = selectedDeliveryPerson.value
    if (typeof selectedDeliveryPerson.value === 'object' && selectedDeliveryPerson.value !== null) {
      deliveryPersonId = selectedDeliveryPerson.value.id
      console.log('🆔 ID du livreur à assigner:', deliveryPersonId)
    }
    
    console.log('📦 Assignation du livreur à la commande:', selectedOrder.value.orderId)
    
    const updatedOrder = await warehouseOrdersService.assignDeliveryPerson(
      selectedOrder.value.id,
      deliveryPersonId,
      deliveryNotes.value
    )
    
    // Mettre à jour l'ordre local dans la liste
    const index = orders.value.findIndex(o => o.id === selectedOrder.value.id)
    if (index !== -1) {
      orders.value[index] = updatedOrder
    }
    
    // Mettre à jour aussi selectedOrder pour que le modal affiche immédiatement le livreur
    // Utiliser Object.assign pour préserver la réactivité
    Object.assign(selectedOrder.value, updatedOrder)
    
    updateStats()
    assignDialog.value = false
    
    showNotification(`Livreur assigné à la commande ${selectedOrder.value.orderId}`, 'success')
    
    // Log pour déboguer
    console.log('✅ Livreur assigné, selectedOrder mis à jour:', selectedOrder.value)
    console.log('📦 Info livreur:', selectedOrder.value.deliveryPerson)
  } catch (error) {
    console.error('Erreur lors de l\'assignation:', error)
    showNotification('Erreur lors de l\'assignation', 'error')
  } finally {
    assigning.value = false
  }
}

const viewOrderDetails = (order) => {
  selectedOrder.value = order
  detailsDialog.value = true
}

// Cycle de vie
onMounted(() => {
  loadOrders()
  loadDeliveryPersons()
})
</script>

<style scoped>
.order-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.12);
}

.border-left-orange {
  border-left: 4px solid #ff9800;
}

.border-left-blue {
  border-left: 4px solid #2196f3;
}

.border-left-green {
  border-left: 4px solid #4caf50;
}

.border-left-success {
  border-left: 4px solid #4caf50;
}

.border-left-red {
  border-left: 4px solid #f44336;
}
</style>
