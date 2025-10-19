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

const products = ref([])
const loading = ref(false)

const headers = [
  { title: 'Nom', key: 'title' },
  { title: 'Prix fournisseur', key: 'supplierPrice' },
  { title: 'Stock', key: 'warehouseQuantity' },
  { title: 'Catégorie', key: 'category.name' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const editProduct = (product) => {
  console.log('Éditer le produit:', product)
}

const deleteProduct = (productId) => {
  console.log('Supprimer le produit:', productId)
}

onMounted(() => {
  // Charger les produits du fournisseur
})
</script>
