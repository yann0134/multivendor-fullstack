<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">📦 Gestion de l'Inventaire</h1>
        <p class="text-body-1 text-grey-600 mb-6">
          Produits validés et reçus par l'entrepôt
        </p>
      </v-col>
    </v-row>

    <!-- Statistiques rapides -->
    <v-row class="mb-8">
      <v-col cols="12" md="4">
        <v-card color="primary" dark class="text-center pa-4 elevation-4" rounded>
          <v-icon size="48" class="mb-2">mdi-check-circle</v-icon>
          <h3 class="text-h6">Produits Reçus</h3>
          <p class="text-h3 font-weight-bold">{{ stats.totalReceived }}</p>
          <v-chip small class="mt-2" color="white" text-color="primary">
            produits en stock
          </v-chip>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="success" dark class="text-center pa-4 elevation-4" rounded>
          <v-icon size="48" class="mb-2">mdi-cash-multiple</v-icon>
          <h3 class="text-h6">Montant des Ventes</h3>
          <p class="text-h3 font-weight-bold">{{ formatPrice(stats.totalSalesAmount) }}</p>
          <v-chip small class="mt-2" color="white" text-color="success">
            {{ stats.quantitySold }} vendus
          </v-chip>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card color="orange darken-2" dark class="text-center pa-4 elevation-4" rounded>
          <v-icon size="48" class="mb-2">mdi-warehouse</v-icon>
          <h3 class="text-h6">Valeur du Stock</h3>
          <p class="text-h3 font-weight-bold">{{ formatPrice(stats.totalValue) }}</p>
          <v-chip small class="mt-2" color="white" text-color="orange">
            en inventaire
          </v-chip>
        </v-card>
      </v-col>
    </v-row>

    <!-- Tableau de l'inventaire -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-warehouse</v-icon>
            <span>Inventaire de l'Entrepôt</span>
            <v-spacer></v-spacer>
            <!--<v-btn color="orange" @click="initializeWarehouseStock" class="mr-2">
              <v-icon left>mdi-database-sync</v-icon>
              Initialiser Stock
            </v-btn>-->
            <v-btn color="primary" @click="refreshInventory">
              <v-icon left>mdi-refresh</v-icon>
              Actualiser
            </v-btn>
          </v-card-title>
          
          <v-card-text>
            <!-- Champ de recherche -->
            <v-text-field
              v-model="searchQuery"
              prepend-inner-icon="mdi-magnify"
              label="Rechercher dans l'inventaire..."
              clearable
              filled
              rounded
              dense
              class="mb-4"
            >
              <template v-slot:append-outer>
                <v-chip small color="primary" v-if="searchQuery">
                  {{ filteredInventory.length }} résultat(s)
                </v-chip>
              </template>
            </v-text-field>
            
            <v-data-table
              :headers="headers"
              :items="filteredInventory"
              :loading="loading"
              class="elevation-1"
            >
              <!-- En-têtes personnalisés avec icônes et tooltips -->
              <template v-slot:headers="{ columns }">
                <tr>
                  <th v-for="column in columns" :key="column.key" class="text-center">
                    <div class="d-flex align-center justify-center">
                      <v-tooltip bottom>
                        <template v-slot:activator="{ props }">
                          <v-icon 
                            v-bind="props"
                            :color="column.sortable ? 'primary' : 'grey'"
                            size="small"
                            class="mr-2"
                          >
                            {{ column.icon }}
                          </v-icon>
                        </template>
                        <span>{{ column.tooltip }}</span>
                      </v-tooltip>
                      <span class="font-weight-bold">{{ column.text }}</span>
                    </div>
                  </th>
                </tr>
              </template>
              <!-- Image du produit -->
              <template v-slot:item.image="{ item }">
                <v-avatar size="50" class="mr-3">
                  <v-img 
                    v-if="item.images && item.images.length > 0" 
                    :src="item.images[0].imageUrl" 
                    alt="Product"
                    @error="handleImageError"
                  ></v-img>
                  <v-icon v-else>mdi-image</v-icon>
                </v-avatar>
              </template>

              <!-- Informations du produit -->
              <template v-slot:item.productInfo="{ item }">
                <div>
                  <div class="text-h6 font-weight-bold">{{ item.title }}</div>
                  <div class="text-caption text-grey-600">{{ item.description?.substring(0, 50) }}...</div>
                  <div class="text-caption">
                    <v-chip size="small" color="green">{{ item.category?.name || 'Non catégorisé' }}</v-chip>
                  </div>
                </div>
              </template>

              <!-- Fournisseur -->
              <template v-slot:item.supplier="{ item }">
                <div>
                  <div class="font-weight-bold">{{ item.supplier?.supplierName || 'N/A' }}</div>
                  <div class="text-caption text-grey-600">{{ item.supplier?.pickupAddress?.city || 'N/A' }}</div>
                </div>
              </template>

              <!-- Quantité Livrée par le Fournisseur -->
              <template v-slot:item.deliveredQuantity="{ item }">
                <div class="text-center">
                  <div class="text-h6 text-success">{{ item.stockQuantity || 0 }}</div>
                  <div class="text-caption">{{ item.unit || 'unité' }}</div>
                </div>
              </template>

              <!-- Quantité -->
              <template v-slot:item.quantity="{ item }">
                <div class="text-center">
                  <div class="text-h6">{{ item.warehouseQuantity || 0 }}</div>
                  <div class="text-caption">{{ item.unit || 'unité' }}</div>
                </div>
              </template>

              <!-- Prix -->
              <template v-slot:item.price="{ item }">
                <div class="text-right">
                  <div class="font-weight-bold">{{ formatPrice(item.sellingPrice) }}</div>
                  <div class="text-caption">par {{ item.unit || 'unité' }}</div>
                </div>
              </template>

              <!-- Statuts -->
              <template v-slot:item.status="{ item }">
                <div class="d-flex flex-column gap-1">
                  <v-chip color="green" size="small">{{ item.status || 'N/A' }}</v-chip>
                  <v-chip color="blue" size="small">{{ item.receptionStatus || 'N/A' }}</v-chip>
                </div>
              </template>

              <!-- Date de livraison -->
              <template v-slot:item.deliveryDate="{ item }">
                <div class="text-caption">
                  {{ item.deliveryDate ? formatDate(item.deliveryDate) : 'N/A' }}
                </div>
              </template>

              <!-- Date de réception -->
              <template v-slot:item.receivedAt="{ item }">
                <div class="text-caption">
                  {{ item.receivedAt ? formatDate(item.receivedAt) : 'N/A' }}
                </div>
              </template>

              <!-- Actions -->
              <template v-slot:item.actions="{ item }">
                <v-btn 
                  color="info" 
                  small 
                  text 
                  @click="viewProductDetails(item)"
                >
                  <v-icon small left>mdi-eye</v-icon>
                  Détails
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Modal de détails du produit -->
    <v-dialog v-model="showDetailsModal" max-width="90vw" scrollable>
      <v-card v-if="selectedProduct">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="info">mdi-information</v-icon>
          <span>📦 Détails du Produit en Inventaire</span>
        </v-card-title>
        
        <v-card-text>
          <!-- Section récapitulative -->
          <v-row class="mb-4">
            <v-col cols="12" md="4">
              <v-card color="success" dark class="text-center elevation-4">
                <v-card-text>
                  <v-icon size="48" class="mb-3">mdi-truck-delivery</v-icon>
                  <div class="text-h6 mb-2">Quantité Livrée</div>
                  <div class="text-h3 font-weight-bold">{{ getDeliveredQuantity(selectedProduct) }}</div>
                  <div class="text-subtitle-1">{{ selectedProduct.unit }}</div>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card color="info" dark class="text-center elevation-4">
                <v-card-text>
                  <v-icon size="48" class="mb-3">mdi-warehouse</v-icon>
                  <div class="text-h6 mb-2">Stock Disponible</div>
                  <div class="text-h3 font-weight-bold">{{ selectedProduct.warehouseQuantity || 0 }}</div>
                  <div class="text-subtitle-1">{{ selectedProduct.unit }}</div>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card color="orange darken-2" dark class="text-center elevation-4">
                <v-card-text>
                  <v-icon size="48" class="mb-3">mdi-chart-line-variant</v-icon>
                  <div class="text-h6 mb-2">Quantité Vendue</div>
                  <div class="text-h3 font-weight-bold">{{ getDeliveredQuantity(selectedProduct) - (selectedProduct.warehouseQuantity || 0) }}</div>
                  <div class="text-subtitle-1">{{ selectedProduct.unit }}</div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
          
          <v-row dense>
            <!-- Colonne gauche - Images et Informations -->
            <v-col cols="12" md="6">
              <!-- Galerie d'images -->
              <v-card outlined class="mb-3">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-image-multiple</v-icon>
                  Images
                </v-card-title>
                <v-card-text>
                  <ProductImageGallery 
                    :images="productImages" 
                    :show-delete-buttons="false"
                  />
                </v-card-text>
              </v-card>
              
              <!-- Informations Produit -->
              <v-card outlined>
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-information-outline</v-icon>
                  Informations
                </v-card-title>
                <v-card-text>
                  <v-list dense>
                <v-list-item>
                  <v-list-item-title>Nom du Produit</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.title }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Description</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.description || 'Aucune description' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Catégorie</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.category?.name || 'Non catégorisé' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Date de Livraison</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(getDeliveryDate(selectedProduct)) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Date de Réception</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(getReceptionDate(selectedProduct)) }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-divider class="my-3"></v-divider>
                
                <!-- Section des prix -->
                <v-list-item class="price-info-item">
                  <v-list-item-avatar>
                    <v-icon color="success">mdi-cash-register</v-icon>
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title class="font-weight-bold">Prix de Vente</v-list-item-title>
                    <v-list-item-subtitle class="text-h6 text-success font-weight-bold">
                      {{ formatPrice(selectedProduct.sellingPrice) }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle class="text-caption">Prix unitaire</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                
                <v-list-item v-if="selectedProduct.mrpPrice" class="price-info-item">
                  <v-list-item-avatar>
                    <v-icon color="grey">mdi-tag-text</v-icon>
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title class="font-weight-bold">Prix MRP</v-list-item-title>
                    <v-list-item-subtitle class="text-decoration-line-through text-grey">
                      {{ formatPrice(selectedProduct.mrpPrice) }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                
                <v-list-item v-if="selectedProduct.supplierPrice" class="price-info-item">
                  <v-list-item-avatar>
                    <v-icon color="orange">mdi-cart-arrow-down</v-icon>
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title class="font-weight-bold">Prix Fournisseur</v-list-item-title>
                    <v-list-item-subtitle class="text-h6 text-orange">
                      {{ formatPrice(selectedProduct.supplierPrice) }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                
                <v-list-item v-if="selectedProduct.supplierPrice && selectedProduct.sellingPrice" class="price-info-item success">
                  <v-list-item-avatar>
                    <v-icon color="success">mdi-trending-up</v-icon>
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title class="font-weight-bold">Bénéfice par Unité</v-list-item-title>
                    <v-list-item-subtitle class="text-h6 text-success font-weight-bold">
                      {{ formatPrice(selectedProduct.sellingPrice - selectedProduct.supplierPrice) }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle class="text-caption">
                      Marge: {{ ((selectedProduct.sellingPrice - selectedProduct.supplierPrice) / selectedProduct.supplierPrice * 100).toFixed(1) }}%
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                
                <v-divider class="my-3"></v-divider>
                
                <v-list-item>
                  <v-list-item-title>Méthode de Culture</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.farmingMethod || 'Non spécifiée' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Origine</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedProduct.origin || 'Non spécifiée' }}</v-list-item-subtitle>
                </v-list-item>
                  </v-list>
                </v-card-text>
              </v-card>
            </v-col>
            
            <!-- Colonne droite - Prix, Statuts et Fournisseur -->
            <v-col cols="12" md="6">
              <!-- Section des prix -->
              <v-card outlined class="mb-3">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-cash-multiple</v-icon>
                  Prix et Bénéfices
                </v-card-title>
                <v-card-text>
                  <v-list dense>
                    <v-list-item>
                      <v-list-item-avatar>
                        <v-icon color="success">mdi-cash-register</v-icon>
                      </v-list-item-avatar>
                      <v-list-item-content>
                        <v-list-item-title>Prix de Vente</v-list-item-title>
                        <v-list-item-subtitle class="text-h6 text-success">{{ formatPrice(selectedProduct.sellingPrice) }}</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                    
                    <v-list-item v-if="selectedProduct.supplierPrice">
                      <v-list-item-avatar>
                        <v-icon color="orange">mdi-cart-arrow-down</v-icon>
                      </v-list-item-avatar>
                      <v-list-item-content>
                        <v-list-item-title>Prix Fournisseur</v-list-item-title>
                        <v-list-item-subtitle class="text-h6 text-orange">{{ formatPrice(selectedProduct.supplierPrice) }}</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                    
                    <v-list-item v-if="selectedProduct.supplierPrice && selectedProduct.sellingPrice">
                      <v-list-item-avatar>
                        <v-icon color="success">mdi-trending-up</v-icon>
                      </v-list-item-avatar>
                      <v-list-item-content>
                        <v-list-item-title>Bénéfice par Unité</v-list-item-title>
                        <v-list-item-subtitle class="text-h6 text-success">{{ formatPrice(selectedProduct.sellingPrice - selectedProduct.supplierPrice) }}</v-list-item-subtitle>
                        <v-list-item-subtitle class="text-caption">Marge: {{ ((selectedProduct.sellingPrice - selectedProduct.supplierPrice) / selectedProduct.supplierPrice * 100).toFixed(1) }}%</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                  </v-list>
                </v-card-text>
              </v-card>
              
              <!-- Statuts -->
              <v-card outlined class="mb-3">
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-flag-checkered</v-icon>
                  Statuts
                </v-card-title>
                <v-card-text>
                  <v-list dense>
                <v-list-item>
                  <v-list-item-title>Statut Produit</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getProductStatusColor(selectedProduct.status)" small>
                      {{ getProductStatusText(selectedProduct.status) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Statut Expédition</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getShipmentStatusColor(selectedProduct.shipmentStatus)" small>
                      {{ getShipmentStatusText(selectedProduct.shipmentStatus) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <v-list-item-title>Statut Réception</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip :color="getReceptionStatusColor(selectedProduct.receptionStatus)" small>
                      {{ getReceptionStatusText(selectedProduct.receptionStatus) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                  </v-list>
                </v-card-text>
              </v-card>
              
              <!-- Fournisseur -->
              <v-card outlined>
                <v-card-title class="text-h6">
                  <v-icon class="mr-2">mdi-account-tie</v-icon>
                  Fournisseur
                </v-card-title>
                <v-card-text>
                  <v-list dense>
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-bold">Nom</v-list-item-title>
                      <v-list-item-subtitle>{{ getSupplierName(selectedProduct.supplier) }}</v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                  
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-bold">Email</v-list-item-title>
                      <v-list-item-subtitle>{{ selectedProduct.supplier?.email || 'N/A' }}</v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                  
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-bold">Localisation</v-list-item-title>
                      <v-list-item-subtitle>{{ getSupplierLocation(selectedProduct.supplier) }}</v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                  
                  <v-list-item v-if="selectedProduct.supplier?.businessDetails">
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-bold">Entreprise</v-list-item-title>
                      <v-list-item-subtitle>{{ selectedProduct.supplier.businessDetails.businessName || 'N/A' }}</v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                  </v-list>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="closeDetailsModal">
            Fermer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import api from '@/services/api'
import ProductImageGallery from '@/components/supplier/ProductImageGallery.vue'
import { getProductImages } from '@/services/productImages'

// Variables réactives
const inventory = ref([])
const loading = ref(false)
const showDetailsModal = ref(false)
const selectedProduct = ref(null)
const productImages = ref([])
const searchQuery = ref('')

// Watcher pour déboguer les changements d'inventaire
watch(inventory, (newVal) => {
  console.log('🔍 Watcher: inventory changed to', newVal.length, 'items')
  if (newVal.length > 0) {
    console.log('🔍 Watcher: First item:', newVal[0])
  }
}, { immediate: true })

// Computed pour filtrer l'inventaire
const filteredInventory = computed(() => {
  if (!searchQuery.value) {
    return inventory.value
  }
  
  const query = searchQuery.value.toLowerCase()
  return inventory.value.filter(product => {
    const title = product.title?.toLowerCase() || ''
    const description = product.description?.toLowerCase() || ''
    const category = product.category?.name?.toLowerCase() || ''
    const supplierName = product.supplier?.supplierName?.toLowerCase() || ''
    const supplierEmail = product.supplier?.email?.toLowerCase() || ''
    const city = product.supplier?.pickupAddress?.city?.toLowerCase() || ''
    
    return title.includes(query) ||
           description.includes(query) ||
           category.includes(query) ||
           supplierName.includes(query) ||
           supplierEmail.includes(query) ||
           city.includes(query)
  })
})

// Statistiques
const stats = ref({
  totalReceived: 0,
  totalStock: 0,
  totalValue: 0,
  totalSalesAmount: 0,
  quantitySold: 0
})

// En-têtes du tableau avec icônes et tooltips
const headers = [
  { 
    text: 'Image', 
    value: 'image', 
    sortable: false, 
    width: '80px',
    icon: 'mdi-image',
    tooltip: 'Photo du produit'
  },
  { 
    text: 'Produit', 
    value: 'productInfo', 
    sortable: true,
    icon: 'mdi-package-variant',
    tooltip: 'Nom, description et catégorie du produit'
  },
  { 
    text: 'Fournisseur', 
    value: 'supplier', 
    sortable: true,
    icon: 'mdi-account',
    tooltip: 'Nom et localisation du fournisseur'
  },
  { 
    text: 'Qté Livrée', 
    value: 'deliveredQuantity', 
    sortable: true,
    icon: 'mdi-truck-delivery',
    tooltip: 'Quantité livrée par le fournisseur'
  },
  { 
    text: 'Quantité', 
    value: 'quantity', 
    sortable: true,
    icon: 'mdi-scale',
    tooltip: 'Stock disponible en entrepôt'
  },
  { 
    text: 'Prix', 
    value: 'price', 
    sortable: true,
    icon: 'mdi-currency-usd',
    tooltip: 'Prix de vente unitaire'
  },
  { 
    text: 'Statuts', 
    value: 'status', 
    sortable: false,
    icon: 'mdi-information',
    tooltip: 'Statut du produit et de la réception'
  },
  { 
    text: 'Livré le', 
    value: 'deliveryDate', 
    sortable: true,
    icon: 'mdi-truck-delivery',
    tooltip: 'Date d\'expédition par le fournisseur'
  },
  { 
    text: 'Reçu le', 
    value: 'receivedAt', 
    sortable: true,
    icon: 'mdi-check-circle',
    tooltip: 'Date de réception en entrepôt'
  },
  { 
    text: 'Actions', 
    value: 'actions', 
    sortable: false, 
    width: '120px',
    icon: 'mdi-cog',
    tooltip: 'Actions disponibles'
  }
]

// Fonctions de récupération des données
const fetchInventory = async () => {
  loading.value = true
  try {
    console.log('📦 Récupération de l\'inventaire...')
    const response = await api.get('/api/products/received-products')
    console.log('📊 Réponse API:', response)
    console.log('📦 Données reçues:', response.data)
    inventory.value = response.data || []
    console.log('📦 Inventaire chargé:', inventory.value.length, 'produits')
    console.log('📦 Premier produit:', inventory.value[0])
    console.log('📦 Tous les produits:', JSON.stringify(inventory.value))
    
    // Récupérer les statistiques de ventes depuis le backend
    await fetchSalesStats()
    
    // Calculer les statistiques
    updateStats()
    
    // Log après updateStats
    console.log('📊 Inventaire après updateStats:', inventory.value.length)
    console.log('📊 Premier produit après updateStats:', inventory.value[0])
  } catch (error) {
    console.error('❌ Erreur lors du chargement de l\'inventaire:', error)
    console.error('❌ Détails de l\'erreur:', error.response?.data)
  } finally {
    loading.value = false
  }
}

const fetchSalesStats = async () => {
  try {
    console.log('💰 Récupération des statistiques de ventes...')
    const response = await api.get('/api/warehouse/sales-stats')
    console.log('✅ Statistiques de ventes:', response.data)
    
    // Mettre à jour les statistiques avec les données du backend
    if (response.data) {
      stats.value.totalSalesAmount = response.data.totalSalesAmount || 0
      stats.value.quantitySold = response.data.totalQuantitySold || 0
    }
  } catch (error) {
    console.error('❌ Erreur lors de la récupération des statistiques de ventes:', error)
    // Ne pas bloquer l'affichage si erreur
    stats.value.totalSalesAmount = 0
    stats.value.quantitySold = 0
  }
}

const updateStats = () => {
  stats.value.totalReceived = inventory.value.length  // Nombre de types de produits reçus
  stats.value.totalStock = inventory.value.length     // Nombre de types de produits en stock (par type, pas par quantité)
  
  // Calculer la valeur totale du stock actuel
  stats.value.totalValue = inventory.value.reduce((total, product) => {
    const quantity = getDisplayQuantity(product) || 0
    const price = product.sellingPrice || 0  // Utiliser le prix de vente au lieu du prix fournisseur
    return total + (quantity * price)
  }, 0)
  
  console.log(`📊 Statistiques calculées:`)
  console.log(`  💰 Valeur stock actuel: ${stats.value.totalValue}`)
  console.log(`  💵 Montant des ventes: ${stats.value.totalSalesAmount}`)
  console.log(`  📦 Quantité vendue: ${stats.value.quantitySold}`)
}

const refreshInventory = () => {
  fetchInventory()
}

const initializeWarehouseStock = async () => {
  try {
    console.log('🏭 Initialisation du stock d\'entrepôt...')
    const response = await api.post('/api/products/initialize-warehouse-stock')
    console.log('✅ Réponse d\'initialisation:', response.data)
    
    // Afficher un message de succès
    alert(`Stock initialisé avec succès!\n${response.data.updatedProducts} produits mis à jour sur ${response.data.totalProducts}`)
    
    // Actualiser l'inventaire
    await fetchInventory()
    
  } catch (error) {
    console.error('❌ Erreur lors de l\'initialisation du stock:', error)
    alert('Erreur lors de l\'initialisation du stock: ' + (error.response?.data?.message || error.message))
  }
}

// Fonctions utilitaires
const getSupplierName = (supplier) => {
  if (!supplier) return 'N/A'
  
  // Essayer différents champs pour le nom
  if (supplier.supplierName) return supplier.supplierName
  if (supplier.businessDetails?.businessName) return supplier.businessDetails.businessName
  if (supplier.email) return supplier.email.split('@')[0]
  
  return 'Fournisseur inconnu'
}

const getSupplierLocation = (supplier) => {
  if (!supplier) return 'Localisation non définie'
  
  // Essayer différents champs pour la localisation
  if (supplier.pickupAddress?.city) return supplier.pickupAddress.city
  if (supplier.businessDetails?.businessAddress) return supplier.businessDetails.businessAddress
  if (supplier.pickupAddress?.address) return supplier.pickupAddress.address
  
  return 'Localisation non définie'
}

const getDisplayQuantity = (product) => {
  // Toujours utiliser warehouseQuantity comme source de vérité
  // C'est le seul champ qui reflète le stock réel dans l'entrepôt
  return product.warehouseQuantity || 0
}

const getDeliveredQuantity = (product) => {
  // Si deliveredQuantity est défini, l'utiliser
  if (product.deliveredQuantity && product.deliveredQuantity > 0) {
    return product.deliveredQuantity
  }
  
  // Sinon, utiliser stockQuantity comme base (quantité initialement reçue)
  // stockQuantity = quantité livrée à l'entrepôt
  if (product.stockQuantity && product.stockQuantity > 0) {
    return product.stockQuantity
  }
  
  // Fallback: utiliser les anciens champs
  const adminRequested = product.adminRequestedQuantity || 0
  const supplierAvailable = product.supplierAvailableQuantity || 0
  
  // Prendre la plus grande des deux (quantité réellement livrée)
  return Math.max(adminRequested, supplierAvailable)
}

const getQuantityType = (product) => {
  return product.unit || 'unité'
}

const formatPrice = (price) => {
  if (!price) return '0  FCFA'
  return new Intl.NumberFormat('fr-FR').format(price) + '  FCFA'
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getMostRecentDate = (item) => {
  if (!item) return null
  
  const receivedAt = item.receivedAt ? new Date(item.receivedAt) : null
  const updatedAt = item.updatedAt ? new Date(item.updatedAt) : null
  
  // Si les deux dates existent, retourner la plus récente
  if (receivedAt && updatedAt) {
    return receivedAt > updatedAt ? item.receivedAt : item.updatedAt
  }
  
  // Si seule une date existe, la retourner
  if (receivedAt) return item.receivedAt
  if (updatedAt) return item.updatedAt
  
  // Aucune date disponible
  return null
}

const getDeliveryDate = (item) => {
  if (!item) return null
  return item.deliveryDate || null
}

const getReceptionDate = (item) => {
  if (!item) return null
  return item.receivedAt || null
}

// Fonctions de statut
const getProductStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'green',
    'REJECTED': 'red',
    'SUSPENDED': 'grey',
    'DRAFT': 'blue'
  }
  return colors[status] || 'grey'
}

const getProductStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En attente d\'approbation',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu',
    'DRAFT': 'Brouillon'
  }
  return texts[status] || status
}

const getShipmentStatusColor = (status) => {
  const colors = {
    'NOT_SHIPPED': 'orange',
    'SHIPPED': 'blue',
    'DELIVERED': 'green',
    'RETURNED': 'red'
  }
  return colors[status] || 'grey'
}

const getShipmentStatusText = (status) => {
  const texts = {
    'NOT_SHIPPED': 'Non expédié',
    'SHIPPED': 'Expédié',
    'DELIVERED': 'Livré',
    'RETURNED': 'Retourné'
  }
  return texts[status] || status
}

const getReceptionStatusColor = (status) => {
  const colors = {
    'PENDING': 'orange',
    'RECEIVED': 'green',
    'PARTIALLY_RECEIVED': 'blue',
    'REJECTED': 'red',
    'DAMAGED': 'red',
    'QUANTITY_MISMATCH': 'orange'
  }
  return colors[status] || 'grey'
}

const getReceptionStatusText = (status) => {
  const texts = {
    'PENDING': 'En attente',
    'RECEIVED': 'Reçu',
    'PARTIALLY_RECEIVED': 'Partiellement reçu',
    'REJECTED': 'Rejeté',
    'DAMAGED': 'Endommagé',
    'QUANTITY_MISMATCH': 'Écart quantité'
  }
  return texts[status] || status
}

// Fonction pour charger les images du produit
const fetchProductImages = async (productId) => {
  try {
    const images = await getProductImages(productId)
    productImages.value = images
    console.log('🖼️ Images chargées pour le produit:', images.length)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des images:', error)
    productImages.value = []
  }
}

// Fonctions de gestion des modals
const viewProductDetails = async (product) => {
  console.log('📦 Affichage des détails du produit:', product)
  selectedProduct.value = product
  showDetailsModal.value = true
  
  // Charger les images du produit
  await fetchProductImages(product.id)
}

const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedProduct.value = null
}

const handleImageError = (event) => {
  event.target.src = '/images/default-product.jpg'
}

onMounted(() => {
  fetchInventory()
})
</script>

<style scoped>
/* Styles pour les statistiques de stock dans la modal */
.stock-info-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  margin: 8px 0;
  padding: 12px;
  transition: all 0.3s ease;
}

.stock-info-item:hover {
  background-color: #f0f0f0;
  transform: translateX(5px);
}

/* Styles pour les informations de prix dans la modal */
.price-info-item {
  background-color: #fafafa;
  border-radius: 8px;
  margin: 8px 0;
  padding: 12px;
  transition: all 0.3s ease;
}

.price-info-item:hover {
  background-color: #f0f0f0;
  transform: translateX(5px);
}

.price-info-item.success {
  background-color: #e8f5e9;
}

.price-info-item.success:hover {
  background-color: #c8e6c9;
}

/* Styles pour les en-têtes du tableau */
.v-data-table :deep(.v-data-table__wrapper) table thead tr th {
  background-color: #f5f5f5;
  border-bottom: 2px solid #e0e0e0;
  padding: 12px 8px;
  font-weight: 600;
  color: #1976d2;
}

.v-data-table :deep(.v-data-table__wrapper) table thead tr th:hover {
  background-color: #e3f2fd;
}

/* Styles pour les icônes dans les en-têtes */
.v-data-table :deep(.v-data-table__wrapper) table thead tr th .v-icon {
  transition: all 0.3s ease;
}

.v-data-table :deep(.v-data-table__wrapper) table thead tr th .v-icon:hover {
  transform: scale(1.1);
  color: #1976d2 !important;
}

/* Styles pour les tooltips */
.v-tooltip :deep(.v-tooltip__content) {
  background-color: #424242;
  color: white;
  font-size: 12px;
  padding: 8px 12px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

/* Styles pour les cellules du tableau */
.v-data-table :deep(.v-data-table__wrapper) table tbody tr td {
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
}

.v-data-table :deep(.v-data-table__wrapper) table tbody tr:hover {
  background-color: #f8f9fa;
}

/* Styles pour les cartes */
.v-card {
  margin-bottom: 20px;
}

.v-chip {
  margin: 2px;
}

/* Suppression du style global pour v-dialog afin de permettre max-width="90vw" */
</style>
