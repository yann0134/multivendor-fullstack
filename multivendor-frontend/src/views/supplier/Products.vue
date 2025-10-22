<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mes Produits</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>
            Catalogue fournisseur
            <v-spacer />
            <v-btn color="primary">
              <v-icon left>mdi-plus</v-icon>
              Ajouter un produit
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="products"
              :loading="loading"
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

              <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                  <!-- Bouton pour confirmer la quantité demandée par l'admin -->
                  <v-btn 
                    v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                    icon 
                    small 
                    color="success" 
                    class="mr-2"
                    @click="confirmQuantity(item)"
                    :loading="processingProducts.includes(item.id)"
                    title="Accepter la demande admin"
                  >
                    <v-icon>mdi-check-circle</v-icon>
                  </v-btn>
                  
                  <!-- Bouton pour refuser la demande admin -->
                  <v-btn 
                    v-if="item.stockNegotiationPending && item.adminRequestedQuantity > 0"
                    icon 
                    small 
                    color="error" 
                    class="mr-2"
                    @click="rejectQuantityRequest(item)"
                    :loading="processingProducts.includes(item.id)"
                    title="Refuser la demande admin"
                  >
                    <v-icon>mdi-close-circle</v-icon>
                  </v-btn>
                  
                  <!-- Bouton pour modifier la quantité rapidement -->
                  <v-btn 
                    v-if="item.status !== 'APPROVED'"
                    icon 
                    small 
                    color="orange" 
                    class="mr-2"
                    @click="openQuantityDialog(item)"
                    :loading="processingProducts.includes(item.id)"
                  >
                    <v-icon>mdi-package-variant</v-icon>
                  </v-btn>
                  
                  <!-- Bouton pour éditer -->
                  <v-btn 
                    icon 
                    small 
                    color="primary" 
                    class="mr-2"
                    @click="editProduct(item)"
                  >
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  
                  <!-- Bouton pour supprimer -->
                  <v-btn 
                    icon 
                    small 
                    color="error" 
                    @click="deleteProduct(item.id)"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </div>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog pour modifier rapidement la quantité -->
    <v-dialog v-model="quantityDialog" max-width="400">
      <v-card>
        <v-card-title>Modifier la Quantité Disponible</v-card-title>
        <v-card-text v-if="selectedProduct">
          <v-alert type="info" variant="tonal" class="mb-4">
            <strong>Produit:</strong> {{ selectedProduct.title }}<br>
            <strong>Quantité actuelle:</strong> {{ selectedProduct.supplierAvailableQuantity }} unités
          </v-alert>
          
          <v-text-field
            v-model.number="newQuantity"
            label="Nouvelle quantité disponible"
            type="number"
            :min="0"
            :rules="[
              v => v >= 0 || 'La quantité doit être positive ou nulle'
            ]"
            required
          ></v-text-field>
          
          <v-alert 
            v-if="selectedProduct.adminRequestedQuantity > 0"
            type="warning" 
            variant="tonal" 
            class="mt-4"
          >
            <v-icon class="mr-2">mdi-information</v-icon>
            L'administrateur a demandé {{ selectedProduct.adminRequestedQuantity }} unités. 
            Si vous réduisez la quantité en dessous de cette valeur, la demande sera annulée.
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="quantityDialog = false">Annuler</v-btn>
          <v-btn color="primary" @click="updateQuantity" :loading="updatingQuantity">
            Mettre à jour
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const products = ref([])
const loading = ref(false)
const processingProducts = ref([])

// Variables pour la modification rapide de quantité
const quantityDialog = ref(false)
const selectedProduct = ref(null)
const newQuantity = ref(0)
const updatingQuantity = ref(false)

const headers = [
  { title: 'Nom', key: 'title' },
  { title: 'Prix fournisseur', key: 'supplierPrice' },
  { title: 'Stock Disponible', key: 'supplierAvailableQuantity' },
  { title: 'Demande Admin', key: 'adminRequestedQuantity' },
  { title: 'Stock Entrepôt', key: 'warehouseQuantity' },
  { title: 'Catégorie', key: 'category.name' },
  { title: 'Statut', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const editProduct = (product) => {
  console.log('Éditer le produit:', product)
}

const deleteProduct = (productId) => {
  console.log('Supprimer le produit:', productId)
}

// Méthodes pour la gestion des statuts
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

// Méthode pour confirmer la quantité demandée par l'admin
const confirmQuantity = async (product) => {
  processingProducts.value.push(product.id)
  
  try {
    // ✅ Utiliser l'instance API configurée
    const response = await api.put(`/api/products/${product.id}/confirm-quantity`)
    
    if (response.status === 200) {
      // Mettre à jour le produit dans la liste
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Quantité confirmée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    const index = processingProducts.value.indexOf(product.id)
    if (index > -1) {
      processingProducts.value.splice(index, 1)
    }
  }
}

// Méthode pour refuser la demande de quantité de l'admin
const rejectQuantityRequest = async (product) => {
  processingProducts.value.push(product.id)
  
  try {
    // ✅ Utiliser l'instance API configurée
    const response = await api.put(`/api/products/${product.id}/reject-quantity-request`)
    
    if (response.status === 200) {
      // Mettre à jour le produit dans la liste
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      console.log('✅ Demande admin refusée avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    const index = processingProducts.value.indexOf(product.id)
    if (index > -1) {
      processingProducts.value.splice(index, 1)
    }
  }
}

// Méthodes pour la modification rapide de quantité
const openQuantityDialog = (product) => {
  selectedProduct.value = product
  newQuantity.value = product.supplierAvailableQuantity
  quantityDialog.value = true
}

const updateQuantity = async () => {
  if (newQuantity.value < 0) {
    return
  }
  
  updatingQuantity.value = true
  
  try {
    // ✅ Utiliser l'instance API configurée
    const response = await api.put(`/api/products/${selectedProduct.value.id}/update-quantity?newQuantity=${newQuantity.value}`)
    
    if (response.status === 200) {
      // Mettre à jour le produit dans la liste
      const updatedProduct = response.data
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      quantityDialog.value = false
      console.log('✅ Quantité mise à jour avec succès')
    }
  } catch (error) {
    console.error('❌ Erreur:', error)
  } finally {
    updatingQuantity.value = false
  }
}

// Fonction pour charger les produits du fournisseur
const fetchProducts = async () => {
  loading.value = true
  try {
    // Récupérer les produits du fournisseur connecté
    const response = await api.get('/api/products/supplier')
    products.value = response.data
    console.log('📦 Produits chargés:', products.value)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des produits:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
