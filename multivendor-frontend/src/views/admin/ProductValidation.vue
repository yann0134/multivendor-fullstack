<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="error">mdi-clipboard-check</v-icon>
            <span>📋 Validation des Produits</span>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchQuery"
              append-icon="mdi-magnify"
              label="Rechercher par nom ou fournisseur"
              single-line
              hide-details
              density="compact"
              class="flex-grow-0 mr-4"
              style="max-width: 250px;"
            ></v-text-field>
            <v-select
              v-model="selectedStatus"
              :items="statusOptions"
              label="Filtrer par statut"
              density="compact"
              class="flex-grow-0"
              style="max-width: 200px;"
            ></v-select>
          </v-card-title>
          <v-card-subtitle>
            Gérez l'approbation et le rejet des produits soumis par les fournisseurs.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="filteredProducts"
            :loading="loading"
            class="elevation-1"
            item-value="id"
          >
            <template v-slot:item.status="{ item }">
              <v-chip 
                :color="getStatusColor(item.status)" 
                :text-color="getStatusTextColor(item.status)"
                small
              >
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>

            <template v-slot:item.supplier="{ item }">
              <div>
                <div class="font-weight-medium">{{ item.supplier?.supplierName || 'N/A' }}</div>
                <div class="text-caption text-grey-600">{{ item.supplier?.email || 'N/A' }}</div>
              </div>
            </template>

            <template v-slot:item.price="{ item }">
              <div>
                <div class="font-weight-medium">{{ formatPrice(item.sellingPrice) }}</div>
                <div class="text-caption text-grey-600">MRP: {{ formatPrice(item.mrpPrice) }}</div>
              </div>
            </template>

            <template v-slot:item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>

            <template v-slot:item.statusUpdatedAt="{ item }">
              {{ item.statusUpdatedAt ? formatDate(item.statusUpdatedAt) : '-' }}
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="success" 
                  class="mr-2"
                  @click="approveProduct(item.id)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-check</v-icon>
                </v-btn>
                
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="primary" 
                  class="mr-2"
                  @click="openQuantityDialog(item)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-package-variant</v-icon>
                </v-btn>
                
                <!-- Bouton de validation finale après confirmation du fournisseur -->
                <v-btn 
                  v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                  icon 
                  small 
                  color="success" 
                  class="mr-2"
                  @click="finalApproval(item.id)"
                  :loading="processingProducts.includes(item.id)"
                  title="Validation finale après confirmation fournisseur"
                >
                  <v-icon>mdi-check-all</v-icon>
                </v-btn>
                
                <v-btn 
                  v-if="item.status === 'PENDING_APPROVAL'"
                  icon 
                  small 
                  color="error" 
                  class="mr-2"
                  @click="openRejectDialog(item)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-close</v-icon>
                </v-btn>

                <v-btn 
                  v-if="item.status === 'APPROVED'"
                  icon 
                  small 
                  color="warning" 
                  class="mr-2"
                  @click="suspendProduct(item.id)"
                  :loading="processingProducts.includes(item.id)"
                >
                  <v-icon>mdi-pause</v-icon>
                </v-btn>

                <v-btn 
                  icon 
                  small 
                  color="info"
                  @click="viewProductDetails(item)"
                >
                  <v-icon>mdi-eye</v-icon>
                </v-btn>
              </div>
            </template>

            <template v-slot:no-data>
              <v-alert type="info" class="ma-4">Aucun produit trouvé.</v-alert>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de rejet -->
    <v-dialog v-model="rejectDialog" max-width="500">
      <v-card>
        <v-card-title>Rejeter le Produit</v-card-title>
        <v-card-text>
          <v-textarea
            v-model="rejectionReason"
            label="Raison du rejet"
            placeholder="Expliquez pourquoi ce produit est rejeté..."
            rows="3"
            required
          ></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="rejectDialog = false">Annuler</v-btn>
          <v-btn color="error" @click="confirmReject" :loading="rejecting">Rejeter</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de demande de quantité -->
    <v-dialog v-model="quantityDialog" max-width="500">
      <v-card>
        <v-card-title>Demander une Quantité Spécifique</v-card-title>
        <v-card-text v-if="selectedProduct">
          <v-alert type="info" variant="tonal" class="mb-4">
            <strong>Produit:</strong> {{ selectedProduct.title }}<br>
            <strong>Quantité disponible:</strong> {{ selectedProduct.supplierAvailableQuantity }} unités
          </v-alert>
          
          <v-text-field
            v-model.number="requestedQuantity"
            label="Quantité demandée"
            type="number"
            :max="selectedProduct.supplierAvailableQuantity"
            :min="1"
            :rules="[
              v => !!v || 'La quantité est requise',
              v => v > 0 || 'La quantité doit être positive',
              v => v <= selectedProduct.supplierAvailableQuantity || 'Ne peut pas dépasser la quantité disponible'
            ]"
            required
          ></v-text-field>
          
          <v-alert type="warning" variant="tonal" class="mt-4">
            <v-icon class="mr-2">mdi-information</v-icon>
            Le fournisseur devra confirmer cette quantité avant que le produit soit approuvé.
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="quantityDialog = false">Annuler</v-btn>
          <v-btn color="primary" @click="confirmQuantityRequest" :loading="requestingQuantity">
            Demander la Quantité
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog de détails du produit amélioré -->
    <v-dialog v-model="detailsDialog" max-width="1200" scrollable>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-package-variant</v-icon>
          <span>📦 Détails du Produit</span>
          <v-spacer></v-spacer>
          <v-btn icon @click="detailsDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        
        <v-card-text v-if="selectedProduct" class="pa-0">
          <v-container fluid>
            <!-- Section principale avec images et informations -->
          <v-row>
              <!-- Galerie d'images -->
            <v-col cols="12" md="6">
                <v-card class="mb-4" elevation="2">
                  <v-card-title class="d-flex align-center">
                    <v-icon class="mr-2" color="primary">mdi-image-multiple</v-icon>
                    <span>🖼️ Galerie d'Images</span>
                  </v-card-title>
                  <v-card-text>
                    <!-- Image principale -->
                    <div v-if="selectedProduct.images && selectedProduct.images.length > 0" class="main-image-container">
                      <v-img
                        :src="selectedImage || selectedProduct.images[0].imageUrl"
                        height="300"
                        class="rounded main-image"
                        cover
                        @click="openLightbox"
                        @error="handleImageError"
                        style="cursor: pointer;"
                      >
                        <template v-slot:placeholder>
                          <div class="d-flex align-center justify-center fill-height">
                            <v-progress-circular indeterminate color="primary"></v-progress-circular>
                          </div>
                        </template>
                        
                        <!-- Overlay avec informations -->
                        <template v-slot:overlay>
                          <div class="d-flex align-center justify-center fill-height overlay">
                            <v-icon size="48" color="white">mdi-magnify-plus</v-icon>
                          </div>
                        </template>
                      </v-img>
                    </div>

                    <!-- Aucune image -->
                    <div v-else class="no-image-container">
                      <v-sheet
                        height="300"
                        class="d-flex align-center justify-center rounded"
                        color="grey-lighten-2"
                      >
                        <div class="text-center">
                          <v-icon size="64" color="grey">mdi-image-off</v-icon>
                          <div class="text-h6 mt-4 text-grey">Aucune image disponible</div>
                          <div class="text-body-2 text-grey">Ce produit n'a pas encore d'images</div>
                        </div>
                      </v-sheet>
              </div>
              
                    <!-- Miniatures -->
                    <div v-if="selectedProduct.images && selectedProduct.images.length > 1" class="thumbnails-container mt-4">
                      <div class="d-flex flex-wrap gap-2">
                        <div
                          v-for="(image, index) in selectedProduct.images"
                          :key="index"
                          class="thumbnail-wrapper"
                          style="position: relative;"
                        >
                          <v-img
                            :src="image.imageUrl"
                            height="60"
                            width="60"
                            class="rounded thumbnail"
                            :class="{ 'thumbnail-selected': selectedImage === image.imageUrl }"
                            @click="selectedImage = image.imageUrl"
                            @error="handleImageError"
                            style="cursor: pointer; border: 2px solid transparent;"
                          >
                            <template v-slot:placeholder>
                              <div class="d-flex align-center justify-center fill-height">
                                <v-icon color="grey">mdi-image</v-icon>
                              </div>
                            </template>
                          </v-img>
                        </div>
                      </div>
                </div>

                    <!-- Informations sur les images -->
                    <v-alert
                      v-if="selectedProduct.images && selectedProduct.images.length > 0"
                      type="info"
                      variant="tonal"
                      class="mt-4"
                      density="compact"
                    >
                      <v-icon left>mdi-information</v-icon>
                      {{ selectedProduct.images.length }} image(s) disponible(s)
                    </v-alert>
                  </v-card-text>
                </v-card>
              </v-col>

              <!-- Informations du produit -->
              <v-col cols="12" md="6">
                <v-card class="mb-4" elevation="2">
                  <v-card-title class="d-flex align-center">
                    <v-icon class="mr-2" color="primary">mdi-information</v-icon>
                    <span>📋 Informations du Produit</span>
                  </v-card-title>
                  <v-card-text>
                    <h2 class="text-h4 mb-4">{{ selectedProduct.title }}</h2>
                    <p class="text-body-1 mb-6">{{ selectedProduct.description }}</p>
                    
                    <!-- Statut du produit -->
                    <v-chip
                      :color="getStatusColor(selectedProduct.status)"
                      :text-color="getStatusTextColor(selectedProduct.status)"
                      class="mb-4"
                      size="large"
                    >
                      <v-icon left>{{ getStatusIcon(selectedProduct.status) }}</v-icon>
                      {{ getStatusText(selectedProduct.status) }}
                    </v-chip>

                    <!-- Informations de base -->
                    <v-list density="compact" class="mb-4">
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-currency-usd</v-icon>
                        </template>
                        <v-list-item-title>Prix de vente</v-list-item-title>
                        <v-list-item-subtitle>{{ formatPrice(selectedProduct.sellingPrice) }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-currency-usd-circle</v-icon>
                        </template>
                        <v-list-item-title>Prix MRP</v-list-item-title>
                        <v-list-item-subtitle>{{ formatPrice(selectedProduct.mrpPrice) }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="success">mdi-percent</v-icon>
                        </template>
                        <v-list-item-title>Remise</v-list-item-title>
                        <v-list-item-subtitle>{{ selectedProduct.discountPercent }}%</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item v-if="selectedProduct.color">
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-palette</v-icon>
                        </template>
                        <v-list-item-title>Couleur</v-list-item-title>
                        <v-list-item-subtitle>{{ selectedProduct.color }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item v-if="selectedProduct.sizes">
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-ruler</v-icon>
                        </template>
                        <v-list-item-title>Tailles</v-list-item-title>
                        <v-list-item-subtitle>{{ selectedProduct.sizes }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-package-variant</v-icon>
                        </template>
                        <v-list-item-title>Stock Disponible</v-list-item-title>
                        <v-list-item-subtitle>{{ selectedProduct.supplierAvailableQuantity || 0 }} unités</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>

                    <!-- Informations de négociation -->
                    <div v-if="selectedProduct.adminRequestedQuantity > 0 || selectedProduct.stockNegotiationPending" class="mt-4">
                      <h4 class="text-h6 mb-3">📊 Négociation de Stock</h4>
                      <v-alert
                        v-if="selectedProduct.adminRequestedQuantity > 0"
                        type="warning"
                        variant="tonal"
                        class="mb-3"
                      >
                        <v-icon left>mdi-account-tie</v-icon>
                        <strong>Quantité demandée par l'admin:</strong> {{ selectedProduct.adminRequestedQuantity }} unités
                      </v-alert>
                      
                      <v-chip 
                        v-if="selectedProduct.stockNegotiationPending"
                        color="orange" 
                        size="small" 
                        class="mt-2"
                      >
                    <v-icon left>mdi-clock</v-icon>
                    Négociation en cours
                  </v-chip>
                </div>
                  </v-card-text>
                </v-card>
            </v-col>
            </v-row>

            <!-- Informations du fournisseur et statut -->
            <v-row>
            <v-col cols="12" md="6">
                <v-card class="mb-4" elevation="2">
                  <v-card-title class="d-flex align-center">
                    <v-icon class="mr-2" color="success">mdi-account</v-icon>
                    <span>👤 Informations Fournisseur</span>
                  </v-card-title>
                  <v-card-text>
              <div v-if="selectedProduct.supplier">
                      <v-list density="compact">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="success">mdi-account-circle</v-icon>
                          </template>
                          <v-list-item-title>Nom</v-list-item-title>
                          <v-list-item-subtitle>{{ selectedProduct.supplier.supplierName }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="success">mdi-email</v-icon>
                          </template>
                          <v-list-item-title>Email</v-list-item-title>
                          <v-list-item-subtitle>{{ selectedProduct.supplier.email }}</v-list-item-subtitle>
                        </v-list-item>
                      </v-list>
                    </div>
                    <div v-else class="text-grey-600">
                      <v-icon color="grey" class="mr-2">mdi-help-circle</v-icon>
                      Informations fournisseur non disponibles
              </div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col cols="12" md="6">
                <v-card class="mb-4" elevation="2">
                  <v-card-title class="d-flex align-center">
                    <v-icon class="mr-2" color="info">mdi-history</v-icon>
                    <span>📅 Historique et Statut</span>
                  </v-card-title>
                  <v-card-text>
                    <v-list density="compact">
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="info">mdi-calendar-plus</v-icon>
                        </template>
                        <v-list-item-title>Créé le</v-list-item-title>
                        <v-list-item-subtitle>{{ formatDate(selectedProduct.createdAt) }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item v-if="selectedProduct.statusUpdatedAt">
                        <template v-slot:prepend>
                          <v-icon color="info">mdi-calendar-edit</v-icon>
                        </template>
                        <v-list-item-title>Dernière mise à jour</v-list-item-title>
                        <v-list-item-subtitle>{{ formatDate(selectedProduct.statusUpdatedAt) }}</v-list-item-subtitle>
                      </v-list-item>

                      <v-list-item v-if="selectedProduct.reviewedBy">
                        <template v-slot:prepend>
                          <v-icon color="info">mdi-account-check</v-icon>
                        </template>
                        <v-list-item-title>Révisé par</v-list-item-title>
                        <v-list-item-subtitle>{{ selectedProduct.reviewedBy }}</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>

                    <!-- Raison du rejet -->
                    <v-alert
                      v-if="selectedProduct.rejectionReason"
                      type="error"
                      variant="tonal"
                      class="mt-4"
                    >
                      <v-icon left>mdi-alert-circle</v-icon>
                      <strong>Raison du rejet:</strong> {{ selectedProduct.rejectionReason }}
                    </v-alert>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>

            <!-- Informations agricoles si disponibles -->
            <v-row v-if="hasAgriculturalInfo(selectedProduct)">
              <v-col cols="12">
                <v-card class="mb-4" elevation="2">
                  <v-card-title class="d-flex align-center">
                    <v-icon class="mr-2" color="success">mdi-sprout</v-icon>
                    <span>🌾 Informations Agricoles</span>
                  </v-card-title>
                  <v-card-text>
                    <v-row>
                      <v-col cols="12" md="4" v-if="selectedProduct.origin">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="success">mdi-map-marker</v-icon>
                          </template>
                          <v-list-item-title>Origine</v-list-item-title>
                          <v-list-item-subtitle>{{ selectedProduct.origin }}</v-list-item-subtitle>
                        </v-list-item>
                      </v-col>

                      <v-col cols="12" md="4" v-if="selectedProduct.farmingMethod">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="success">mdi-leaf</v-icon>
                          </template>
                          <v-list-item-title>Méthode de Culture</v-list-item-title>
                          <v-list-item-subtitle>{{ selectedProduct.farmingMethod }}</v-list-item-subtitle>
                        </v-list-item>
                      </v-col>

                      <v-col cols="12" md="4" v-if="selectedProduct.season">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="success">mdi-calendar</v-icon>
                          </template>
                          <v-list-item-title>Saison</v-list-item-title>
                          <v-list-item-subtitle>{{ selectedProduct.season }}</v-list-item-subtitle>
                        </v-list-item>
                      </v-col>
                    </v-row>

                    <!-- Badges de caractéristiques -->
                    <div class="d-flex flex-wrap gap-2 mt-4">
                      <v-chip
                        v-if="selectedProduct.organic"
                        color="success"
                        variant="tonal"
                      >
                        <v-icon left>mdi-leaf</v-icon>
                        Bio
                      </v-chip>

                      <v-chip
                        v-if="selectedProduct.local"
                        color="info"
                        variant="tonal"
                      >
                        <v-icon left>mdi-map-marker</v-icon>
                        Local
                      </v-chip>

                <v-chip 
                        v-if="selectedProduct.fresh"
                        color="green"
                        variant="tonal"
                >
                        <v-icon left>mdi-sprout</v-icon>
                        Frais
                </v-chip>
                </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="detailsDialog = false">
            <v-icon left>mdi-close</v-icon>
            Fermer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Lightbox pour les images -->
    <v-dialog v-model="lightboxOpen" max-width="90vw" max-height="90vh">
      <v-card>
        <v-card-title class="d-flex align-center justify-space-between">
          <span>🖼️ Galerie d'Images</span>
          <v-btn icon @click="lightboxOpen = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text class="pa-0">
          <v-img
            :src="selectedImage"
            height="70vh"
            contain
            @error="handleImageError"
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>
            </template>
          </v-img>
        </v-card-text>
        <v-card-actions v-if="selectedProduct && selectedProduct.images && selectedProduct.images.length > 1">
          <v-spacer></v-spacer>
          <v-btn
            icon
            @click="previousImage"
            :disabled="currentImageIndex === 0"
          >
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>
          <span class="mx-4">{{ currentImageIndex + 1 }} / {{ selectedProduct.images.length }}</span>
          <v-btn
            icon
            @click="nextImage"
            :disabled="currentImageIndex === selectedProduct.images.length - 1"
          >
            <v-icon>mdi-chevron-right</v-icon>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getPendingProducts, getApprovedProducts, getRejectedProducts, approveProduct, rejectProduct, suspendProduct } from '@/services/products'
import api from '@/services/api'

const products = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('PENDING_APPROVAL')
const processingProducts = ref([])
const rejectDialog = ref(false)
const rejecting = ref(false)
const rejectionReason = ref('')
const selectedProductForReject = ref(null)
const detailsDialog = ref(false)
const selectedProduct = ref(null)

// Variables pour la gestion des images
const selectedImage = ref(null)
const lightboxOpen = ref(false)

// Variables pour la gestion des quantités
const quantityDialog = ref(false)
const requestingQuantity = ref(false)
const requestedQuantity = ref(0)
const selectedProductForQuantity = ref(null)

const statusOptions = [
  { title: 'En Attente', value: 'PENDING_APPROVAL' },
  { title: 'Approuvés', value: 'APPROVED' },
  { title: 'Rejetés', value: 'REJECTED' },
  { title: 'Suspendus', value: 'SUSPENDED' }
]

const headers = [
  { title: 'Produit', key: 'title', sortable: true },
  { title: 'Fournisseur', key: 'supplier', sortable: false },
  { title: 'Prix', key: 'price', sortable: true },
  { title: 'Stock Fournisseur', key: 'supplierAvailableQuantity', sortable: true },
  { title: 'Demande Admin', key: 'adminRequestedQuantity', sortable: true },
  { title: 'Statut', key: 'status', sortable: true },
  { title: 'Créé le', key: 'createdAt', sortable: true },
  { title: 'Mis à jour', key: 'statusUpdatedAt', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

const filteredProducts = computed(() => {
  let filtered = products.value

  if (selectedStatus.value) {
    filtered = filtered.filter(p => p.status === selectedStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.title.toLowerCase().includes(query) ||
      (p.supplier?.supplierName && p.supplier.supplierName.toLowerCase().includes(query)) ||
      (p.supplier?.email && p.supplier.email.toLowerCase().includes(query))
    )
  }

  return filtered
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING_APPROVAL': 'orange',
    'APPROVED': 'success',
    'REJECTED': 'error',
    'SUSPENDED': 'warning'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return status === 'PENDING_APPROVAL' ? 'white' : 'white'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING_APPROVAL': 'En Attente',
    'APPROVED': 'Approuvé',
    'REJECTED': 'Rejeté',
    'SUSPENDED': 'Suspendu'
  }
  return texts[status] || status
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF'
  }).format(price)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await getPendingProducts()
    products.value = response.data.content || response.data
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  } finally {
    loading.value = false
  }
}

const approveProductAction = async (productId) => {
  processingProducts.value.push(productId)
  try {
    await approveProduct(productId)
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors de l\'approbation:', error)
  } finally {
    processingProducts.value = processingProducts.value.filter(id => id !== productId)
  }
}

const openRejectDialog = (product) => {
  selectedProductForReject.value = product
  rejectionReason.value = ''
  rejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectionReason.value.trim()) {
    return
  }

  rejecting.value = true
  try {
    await rejectProduct(selectedProductForReject.value.id, rejectionReason.value)
    rejectDialog.value = false
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors du rejet:', error)
  } finally {
    rejecting.value = false
  }
}

const suspendProductAction = async (productId) => {
  processingProducts.value.push(productId)
  try {
    await suspendProduct(productId)
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors de la suspension:', error)
  } finally {
    processingProducts.value = processingProducts.value.filter(id => id !== productId)
  }
}

const viewProductDetails = async (product) => {
  selectedProduct.value = product
  
  // Charger les images du produit séparément
  try {
    console.log('🖼️ Chargement des images pour le produit:', product.id)
    const response = await api.get(`/api/products/${product.id}/images`)
    const images = response.data || []
    
    // Construire des URLs complètes pour les images
    const imagesWithFullUrls = images.map(image => ({
      ...image,
      imageUrl: image.imageUrl && !image.imageUrl.startsWith('http') 
        ? `http://localhost:3026${image.imageUrl}`
        : image.imageUrl
    }))
    
    selectedProduct.value.images = imagesWithFullUrls
    console.log('🖼️ Images chargées avec URLs complètes:', imagesWithFullUrls)
    
    // Initialiser l'image sélectionnée
    if (imagesWithFullUrls.length > 0) {
      selectedImage.value = imagesWithFullUrls[0].imageUrl
    }
  } catch (error) {
    console.error('❌ Erreur lors du chargement des images:', error)
    selectedProduct.value.images = []
  }
  
  detailsDialog.value = true
}

// Méthodes pour la gestion des images
const openLightbox = () => {
  if (selectedProduct.value && selectedProduct.value.images && selectedProduct.value.images.length > 0) {
    lightboxOpen.value = true
  }
}

const previousImage = () => {
  if (currentImageIndex.value > 0) {
    selectedImage.value = selectedProduct.value.images[currentImageIndex.value - 1].imageUrl
  }
}

const nextImage = () => {
  if (currentImageIndex.value < selectedProduct.value.images.length - 1) {
    selectedImage.value = selectedProduct.value.images[currentImageIndex.value + 1].imageUrl
  }
}

// Computed pour l'index de l'image actuelle
const currentImageIndex = computed(() => {
  if (!selectedProduct.value || !selectedProduct.value.images || !selectedImage.value) return 0
  return selectedProduct.value.images.findIndex(img => img.imageUrl === selectedImage.value)
})

// Fonction pour vérifier si le produit a des informations agricoles
const hasAgriculturalInfo = (product) => {
  return product.origin || product.farmingMethod || product.season || product.organic || product.local || product.fresh
}

// Fonction pour obtenir l'icône du statut
const getStatusIcon = (status) => {
  const icons = {
    'PENDING_APPROVAL': 'mdi-clock',
    'APPROVED': 'mdi-check-circle',
    'REJECTED': 'mdi-close-circle',
    'SUSPENDED': 'mdi-pause-circle',
    'DRAFT': 'mdi-file-document'
  }
  return icons[status] || 'mdi-help-circle'
}

// Gestion des erreurs d'images
const handleImageError = (event) => {
  console.error('❌ Erreur de chargement de l\'image:', event.target.src)
  // Remplacer par une image par défaut
  event.target.src = getDefaultProductImage()
}

const getDefaultProductImage = () => {
  // Retourner une image SVG par défaut
  return 'data:image/svg+xml;base64,' + btoa(`
    <svg width="400" height="300" xmlns="http://www.w3.org/2000/svg">
      <rect width="400" height="300" fill="#f5f5f5"/>
      <text x="200" y="150" text-anchor="middle" font-family="Arial" font-size="16" fill="#666">
        Image non disponible
      </text>
    </svg>
  `)
}

// Méthodes pour la gestion des quantités
const openQuantityDialog = (product) => {
  selectedProductForQuantity.value = product
  selectedProduct.value = product
  requestedQuantity.value = 0
  quantityDialog.value = true
}

const confirmQuantityRequest = async () => {
  if (!requestedQuantity.value || requestedQuantity.value <= 0) {
    return
  }
  
  requestingQuantity.value = true
  
  try {
    // ✅ Utiliser l'instance API configurée
    const response = await api.put(`/api/products/${selectedProductForQuantity.value.id}/request-quantity?requestedQuantity=${requestedQuantity.value}`)
    
    if (response.status === 200) {
      // Mettre à jour le produit dans la liste
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      quantityDialog.value = false
      // Afficher un message de succès
      console.log('✅ Demande de quantité envoyée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    requestingQuantity.value = false
  }
}

// Validation finale après confirmation du fournisseur
const finalApproval = async (productId) => {
  processingProducts.value.push(productId)
  
  try {
    const response = await api.put(`/api/products/${productId}/final-approval`)
    
    if (response.status === 200) {
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Validation finale effectuée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur lors de la validation finale:', error)
  } finally {
    const index = processingProducts.value.indexOf(productId)
    if (index > -1) {
      processingProducts.value.splice(index, 1)
    }
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
/* Styles pour la galerie d'images */
.main-image-container {
  position: relative;
}

.main-image {
  transition: transform 0.3s ease;
}

.main-image:hover {
  transform: scale(1.02);
}

.overlay {
  background: rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.main-image:hover .overlay {
  opacity: 1;
}

.thumbnails-container {
  overflow-x: auto;
  padding-bottom: 8px;
}

.thumbnail {
  transition: all 0.3s ease;
  border-radius: 8px;
}

.thumbnail:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.thumbnail-selected {
  border-color: rgb(var(--v-theme-primary)) !important;
  box-shadow: 0 0 0 2px rgba(var(--v-theme-primary), 0.3);
}

.no-image-container {
  border: 2px dashed #ccc;
  border-radius: 8px;
}

/* Styles pour les cartes */
.v-card {
  border-radius: 12px;
}

.v-card-title {
  font-weight: 600;
}

/* Styles pour les listes */
.v-list-item {
  border-radius: 8px;
  margin-bottom: 4px;
}

.v-list-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.04);
}

/* Styles pour les chips */
.v-chip {
  font-weight: 500;
}

/* Responsive */
@media (max-width: 768px) {
  .main-image {
    height: 200px !important;
  }
  
  .thumbnail {
    height: 40px !important;
    width: 40px !important;
  }
}

.flex-grow-0 {
  flex-grow: 0;
}
</style>
