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
                <!-- Galerie d'images -->
                <v-col cols="12" md="6">
                  <v-card class="mb-4">
                    <v-card-title class="d-flex align-center">
                      <v-icon class="mr-2" color="primary">mdi-image-multiple</v-icon>
                      <span>📸 Galerie d'Images</span>
                      <v-spacer></v-spacer>
                      <v-btn
                        color="primary"
                        variant="outlined"
                        size="small"
                        @click="triggerImageUpload"
                        :loading="uploading"
                      >
                        <v-icon left>mdi-plus</v-icon>
                        Ajouter
                      </v-btn>
                    </v-card-title>
                    <v-card-text>
                      <ProductImageGallery 
                        :images="productImages" 
                        :show-delete-buttons="true"
                        @delete-image="removeImage"
                      />
                      
                      <!-- Input file caché pour l'upload -->
                      <input
                        ref="fileInput"
                        type="file"
                        accept="image/*"
                        style="display: none"
                        @change="handleImageUpload"
                      />
                    </v-card-text>
                  </v-card>
                </v-col>

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
                    suffix=" FCFA"
                  ></v-text-field>

                  <v-text-field
                    v-model.number="formData.sellingPrice"
                    label="Prix de vente *"
                    type="number"
                    :rules="[v => !!v || 'Le prix de vente est requis', v => v > 0 || 'Le prix doit être positif']"
                    required
                    class="mb-3"
                    suffix=" FCFA"
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
import ProductImageGallery from '@/components/supplier/ProductImageGallery.vue'
import { getProductImages, addProductImage, deleteProductImage } from '@/services/productImages'

const router = useRouter()
const route = useRoute()

const product = ref(null)
const productImages = ref([])
const loading = ref(false)
const updating = ref(false)
const valid = ref(false)
const categories = ref([])
const uploading = ref(false)
const fileInput = ref(null)

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
    formData.organic = Boolean(product.value.organic)
    formData.local = Boolean(product.value.local)
    formData.fresh = Boolean(product.value.fresh)
    
    console.log('🔍 Données du formulaire après mapping:', formData)
    
    // Charger les images du produit
    await fetchProductImages(productId)
    
  } catch (error) {
    console.error('Erreur lors du chargement du produit:', error)
  } finally {
    loading.value = false
  }
}

const fetchProductImages = async (productId) => {
  try {
    const response = await getProductImages(productId)
    productImages.value = response.data
    console.log('🖼️ Images du produit chargées:', productImages.value)
  } catch (error) {
    console.error('Erreur lors du chargement des images du produit:', error)
    productImages.value = []
  }
}

const fetchCategories = async () => {
  try {
    const response = await getAllCategories()
    categories.value = response.map(cat => cat.name)
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  }
}

const updateProduct = async () => {
  if (!valid.value) return
  
  updating.value = true
  try {
    const productId = route.params.id
    await updateSupplierProduct(productId, formData)
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

// Fonctions pour l'upload d'images
const triggerImageUpload = () => {
  fileInput.value?.click()
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // Vérifier le type de fichier
  if (!file.type.startsWith('image/')) {
    alert('Veuillez sélectionner un fichier image')
    return
  }
  
  // Vérifier la taille (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('La taille du fichier ne doit pas dépasser 5MB')
    return
  }
  
  await uploadImage(file)
}

const uploadImage = async (file) => {
  uploading.value = true
  
  try {
    console.log('📸 Upload de l\'image:', file.name)
    
    const productId = route.params.id
    const response = await addProductImage(productId, file)
    
    console.log('✅ Image uploadée avec succès:', response)
    
    // Recharger les images du produit
    await fetchProductImages(productId)
    
    // Afficher un message de succès
    alert('Image ajoutée avec succès !')
    
  } catch (error) {
    console.error('❌ Erreur lors de l\'upload:', error)
    alert('Erreur lors de l\'ajout de l\'image. Veuillez réessayer.')
  } finally {
    uploading.value = false
    // Réinitialiser l'input file
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// Fonction pour supprimer une image
const removeImage = async (imageId) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette image ?')) {
    return
  }
  
  try {
    console.log('🗑️ Suppression de l\'image:', imageId)
    
    const productId = route.params.id
    await deleteProductImage(productId, imageId)
    
    console.log('✅ Image supprimée avec succès')
    
    // Recharger les images du produit
    await fetchProductImages(productId)
    
    // Afficher un message de succès
    alert('Image supprimée avec succès !')
    
  } catch (error) {
    console.error('❌ Erreur lors de la suppression:', error)
    alert('Erreur lors de la suppression de l\'image. Veuillez réessayer.')
  }
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
