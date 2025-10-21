<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="warning">mdi-pencil</v-icon>
            <span>✏️ Modifier le Produit</span>
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="goBack">
              <v-icon left>mdi-arrow-left</v-icon>
              Retour
            </v-btn>
          </v-card-title>
          <v-card-subtitle>
            Modifiez les informations de votre produit agricole.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="loading">
      <v-col cols="12">
        <v-card>
          <v-card-text class="text-center py-8">
            <v-progress-circular indeterminate color="warning" size="64"></v-progress-circular>
            <div class="mt-4">Chargement du produit...</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-else-if="product">
      <v-col cols="12">
        <v-card>
          <v-form ref="form" v-model="valid" @submit.prevent="updateProduct">
            <v-card-text>
              <v-row>
                <!-- Informations de base -->
                <v-col cols="12" md="6">
                  <h3 class="mb-4">📝 Informations de Base</h3>
                  
                  <v-text-field
                    v-model="formData.title"
                    label="Nom du produit *"
                    :rules="[v => !!v || 'Le nom est requis']"
                    required
                    class="mb-3"
                  ></v-text-field>

                  <v-textarea
                    v-model="formData.description"
                    label="Description *"
                    :rules="[v => !!v || 'La description est requise']"
                    required
                    rows="3"
                    class="mb-3"
                  ></v-textarea>

                  <v-text-field
                    v-model="formData.color"
                    label="Couleur"
                    class="mb-3"
                  ></v-text-field>

                  <v-text-field
                    v-model="formData.sizes"
                    label="Tailles disponibles"
                    class="mb-3"
                  ></v-text-field>
                </v-col>

                <!-- Prix et catégorie -->
                <v-col cols="12" md="6">
                  <h3 class="mb-4">💰 Prix et Catégorie</h3>
                  
                  <v-text-field
                    v-model.number="formData.mrpPrice"
                    label="Prix MRP (Prix de marché) *"
                    type="number"
                    :rules="[v => !!v || 'Le prix MRP est requis', v => v > 0 || 'Le prix doit être positif']"
                    required
                    class="mb-3"
                    suffix="FCFA"
                  ></v-text-field>

                  <v-text-field
                    v-model.number="formData.sellingPrice"
                    label="Prix de vente *"
                    type="number"
                    :rules="[v => !!v || 'Le prix de vente est requis', v => v > 0 || 'Le prix doit être positif']"
                    required
                    class="mb-3"
                    suffix="FCFA"
                  ></v-text-field>

                  <v-autocomplete
                    v-model="formData.category"
                    :items="categories"
                    label="Catégorie *"
                    :rules="[v => !!v || 'La catégorie est requise']"
                    required
                    class="mb-3"
                  ></v-autocomplete>

                  <v-text-field
                    v-model="formData.category2"
                    label="Sous-catégorie"
                    class="mb-3"
                  ></v-text-field>
                </v-col>

                <!-- Gestion des stocks -->
                <v-col cols="12">
                  <h3 class="mb-4">📦 Gestion des Stocks</h3>
                  
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model.number="formData.supplierAvailableQuantity"
                        label="Quantité disponible chez vous *"
                        type="number"
                        :rules="[v => v >= 0 || 'La quantité doit être positive ou nulle']"
                        required
                        class="mb-3"
                        :disabled="product.status === 'APPROVED'"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-alert 
                        v-if="product.adminRequestedQuantity > 0"
                        type="info" 
                        variant="tonal"
                        class="mb-3"
                      >
                        <v-icon class="mr-2">mdi-information</v-icon>
                        <strong>Demande de l'administrateur:</strong> {{ product.adminRequestedQuantity }} unités
                      </v-alert>
                      
                      <v-alert 
                        v-if="product.status === 'APPROVED'"
                        type="warning" 
                        variant="tonal"
                        class="mb-3"
                      >
                        <v-icon class="mr-2">mdi-lock</v-icon>
                        Ce produit est déjà approuvé. La quantité ne peut plus être modifiée.
                      </v-alert>
                    </v-col>
                  </v-row>
                </v-col>

                <!-- Informations agricoles -->
                <v-col cols="12">
                  <h3 class="mb-4">🌱 Informations Agricoles</h3>
                  
                  <v-row>
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model="formData.origin"
                        label="Origine (région, ferme)"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model="formData.farmingMethod"
                        label="Méthode de culture"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model="formData.season"
                        label="Saison de production"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                  </v-row>

                  <v-row>
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model="formData.unit"
                        label="Unité de vente"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model.number="formData.weight"
                        label="Poids moyen"
                        type="number"
                        step="0.1"
                        class="mb-3"
                        suffix="kg"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-text-field
                        v-model="formData.storageConditions"
                        label="Conditions de stockage"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                  </v-row>

                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="formData.nutritionalInfo"
                        label="Informations nutritionnelles"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="formData.allergens"
                        label="Allergènes"
                        class="mb-3"
                      ></v-text-field>
                    </v-col>
                  </v-row>

                  <v-row>
                    <v-col cols="12" md="4">
                      <v-checkbox
                        v-model="formData.organic"
                        label="Produit bio"
                        color="success"
                      ></v-checkbox>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-checkbox
                        v-model="formData.local"
                        label="Produit local"
                        color="info"
                      ></v-checkbox>
                    </v-col>
                    
                    <v-col cols="12" md="4">
                      <v-checkbox
                        v-model="formData.fresh"
                        label="Produit frais"
                        color="warning"
                      ></v-checkbox>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-card-text>

            <v-card-actions class="pa-4">
              <v-spacer></v-spacer>
              <v-btn variant="text" @click="goBack">
                Annuler
              </v-btn>
              <v-btn 
                color="warning" 
                type="submit"
                :loading="updating"
                :disabled="!valid"
              >
                <v-icon left>mdi-content-save</v-icon>
                Mettre à jour
              </v-btn>
            </v-card-actions>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col cols="12">
        <v-card>
          <v-card-text class="text-center py-8">
            <v-icon size="64" color="error">mdi-alert-circle</v-icon>
            <div class="mt-4">Produit non trouvé</div>
            <v-btn color="warning" class="mt-2" @click="goBack">
              Retour à la liste
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getSupplierProduct, updateSupplierProduct } from '@/services/products'
import { getAllCategories } from '@/services/categories'

const router = useRouter()
const route = useRoute()

const product = ref(null)
const loading = ref(false)
const updating = ref(false)
const valid = ref(false)
const categories = ref([])

const formData = reactive({
  title: '',
  description: '',
  mrpPrice: 0,
  sellingPrice: 0,
  supplierAvailableQuantity: 0,
  color: '',
  sizes: '',
  category: '',
  category2: '',
  origin: '',
  farmingMethod: '',
  season: '',
  unit: '',
  weight: 0,
  storageConditions: '',
  nutritionalInfo: '',
  allergens: '',
  organic: false,
  local: false,
  fresh: true
})

const fetchProduct = async () => {
  loading.value = true
  try {
    const productId = route.params.id
    const response = await getSupplierProduct(productId)
    product.value = response.data
    
    console.log('🔍 Données du produit reçues:', product.value)
    console.log('🔍 Catégorie du produit:', product.value.category)
    console.log('🔍 Sous-catégorie du produit:', product.value.subCategory)
    console.log('🔍 État bio du produit:', product.value.organic)
    console.log('🔍 État local du produit:', product.value.local)
    console.log('🔍 État frais du produit:', product.value.fresh)
    
    // Remplir le formulaire avec les données du produit
    formData.title = product.value.title || ''
    formData.description = product.value.description || ''
    formData.mrpPrice = product.value.mrpPrice || 0
    formData.sellingPrice = product.value.sellingPrice || 0
    formData.supplierAvailableQuantity = product.value.supplierAvailableQuantity || 0
    formData.color = product.value.color || ''
    formData.sizes = product.value.sizes || ''
    formData.category = product.value.category?.name || ''
    formData.category2 = product.value.subCategory?.name || ''
    formData.origin = product.value.origin || ''
    formData.farmingMethod = product.value.farmingMethod || ''
    formData.season = product.value.season || ''
    formData.unit = product.value.unit || ''
    formData.weight = product.value.weight || 0
    formData.storageConditions = product.value.storageConditions || ''
    formData.nutritionalInfo = product.value.nutritionalInfo || ''
    formData.allergens = product.value.allergens || ''
    
    console.log('🔍 Mapping des champs agricoles:')
    console.log('  - origin:', product.value.origin, '->', formData.origin)
    console.log('  - farmingMethod:', product.value.farmingMethod, '->', formData.farmingMethod)
    console.log('  - season:', product.value.season, '->', formData.season)
    console.log('  - unit:', product.value.unit, '->', formData.unit)
    console.log('  - weight:', product.value.weight, '->', formData.weight)
    console.log('  - storageConditions:', product.value.storageConditions, '->', formData.storageConditions)
    console.log('  - nutritionalInfo:', product.value.nutritionalInfo, '->', formData.nutritionalInfo)
    console.log('  - allergens:', product.value.allergens, '->', formData.allergens)
    formData.organic = Boolean(product.value.organic)
    formData.local = Boolean(product.value.local)
    formData.fresh = Boolean(product.value.fresh)
    
    console.log('🔍 Données du formulaire après mapping:', formData)
  } catch (error) {
    console.error('Erreur lors du chargement du produit:', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await getAllCategories()
    console.log('🔍 Catégories récupérées:', response)
    categories.value = response.map(cat => cat.name)
    console.log('🔍 Noms des catégories:', categories.value)
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  }
}

const updateProduct = async () => {
  if (!valid.value) return
  
  updating.value = true
  try {
    const productId = route.params.id
    
    console.log('🔍 Données envoyées pour la mise à jour:', formData)
    
    const response = await updateSupplierProduct(productId, formData)
    
    console.log('🔍 Réponse de la mise à jour:', response.data)
    
    // Rediriger vers la liste des produits
    router.push('/supplier/products')
  } catch (error) {
    console.error('Erreur lors de la mise à jour:', error)
  } finally {
    updating.value = false
  }
}

const goBack = () => {
  router.push('/supplier/products')
}

onMounted(() => {
  fetchProduct()
  fetchCategories()
})
</script>

<style scoped>
.v-card {
  border-radius: 12px;
}

.v-text-field, .v-textarea, .v-autocomplete {
  margin-bottom: 8px;
}
</style>
