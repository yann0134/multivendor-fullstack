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
            Gestion des produits
            <v-spacer />
            <v-btn color="primary" @click="showAddProduct = true">
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
              <template v-slot:item.images="{ item }">
                <v-img
                  :src="item.images?.[0] || '/placeholder.jpg'"
                  width="50"
                  height="50"
                  cover
                />
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn icon @click="editProduct(item)">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon @click="deleteProduct(item.id)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProductStore } from '@/stores/products'

const productStore = useProductStore()

const products = ref([])
const loading = ref(false)
const showAddProduct = ref(false)

const headers = [
  { title: 'Image', key: 'images', sortable: false },
  { title: 'Nom', key: 'title' },
  { title: 'Prix', key: 'sellingPrice' },
  { title: 'Stock', key: 'quantity' },
  { title: 'Catégorie', key: 'category.name' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const editProduct = (product) => {
  // Logique d'édition
  console.log('Éditer le produit:', product)
}

const deleteProduct = async (productId) => {
  try {
    await productStore.deleteProduct(productId)
    await fetchProducts()
  } catch (error) {
    console.error('Erreur lors de la suppression:', error)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    // Récupérer les produits du vendeur
    // products.value = await productStore.fetchSellerProducts()
  } catch (error) {
    console.error('Erreur lors du chargement des produits:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
