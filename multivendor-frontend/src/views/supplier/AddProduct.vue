<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="warning">mdi-plus-circle</v-icon>
            <span>🌱 Ajouter un Nouveau Produit</span>
          </v-card-title>
          <v-card-subtitle>
            Soumettez vos produits agricoles pour validation par l'administrateur.
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-form ref="productForm" @submit.prevent="submitProduct">
            <v-card-text>
              <v-row>
                <!-- Informations de base -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">📝 Informations de Base</h3>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.title"
                    label="Nom du produit *"
                    placeholder="Ex: Tomates Bio"
                    :rules="[v => !!v || 'Le nom est requis']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.color"
                    label="Couleur"
                    placeholder="Ex: Rouge, Vert, Jaune"
                  ></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="formData.description"
                    label="Description détaillée *"
                    placeholder="Décrivez votre produit, ses caractéristiques, sa qualité..."
                    :rules="[v => !!v || 'La description est requise']"
                    rows="3"
                    required
                  ></v-textarea>
                </v-col>

                <!-- Prix et quantités -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">💰 Prix et Quantités</h3>
                </v-col>

                <v-col cols="12" md="4">
                  <v-text-field
                    v-model.number="formData.mrpPrice"
                    label="Prix de marché ( FCFA) *"
                    type="number"
                    placeholder="1000"
                    :rules="[v => !!v || 'Le prix MRP est requis', v => v > 0 || 'Le prix doit être positif']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="4">
                  <v-text-field
                    v-model.number="formData.sellingPrice"
                    label="Prix de vente ( FCFA) *"
                    type="number"
                    placeholder="800"
                    :rules="[v => !!v || 'Le prix de vente est requis', v => v > 0 || 'Le prix doit être positif']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="4">
                  <v-text-field
                    v-model="formData.sizes"
                    label="Unité/Taille"
                    placeholder="Ex: 1kg, 500g, Pièce"
                  ></v-text-field>
                </v-col>

                <!-- Gestion des stocks -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">📦 Gestion des Stocks</h3>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model.number="formData.supplierAvailableQuantity"
                    label="Quantité disponible chez vous *"
                    type="number"
                    placeholder="Ex: 100"
                    hint="Indiquez la quantité totale que vous avez en stock"
                    persistent-hint
                    :rules="[v => !!v || 'La quantité est requise', v => v >= 0 || 'La quantité doit être positive']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-alert
                    type="info"
                    variant="tonal"
                    class="mb-4"
                  >
                    <v-icon class="mr-2">mdi-information</v-icon>
                    L'administrateur pourra demander une quantité spécifique lors de la validation de votre produit.
                  </v-alert>
                </v-col>

                <!-- Catégories -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">🏷️ Classification</h3>
                </v-col>

                <v-col cols="12" md="4">
                  <v-autocomplete
                    v-model="formData.category"
                    :items="categories"
                    item-title="name"
                    item-value="name"
                    label="Catégorie principale *"
                    placeholder="Rechercher une catégorie..."
                    :rules="[v => !!v || 'La catégorie est requise']"
                    :loading="loadingCategories"
                    clearable
                    required
                  >
                    <template v-slot:item="{ props, item }">
                      <v-list-item v-bind="props">
                        <template v-slot:prepend>
                          <v-icon :color="item.raw.color || 'grey'">
                            {{ item.raw.icon || 'mdi-tag' }}
                          </v-icon>
                        </template>
                        <v-list-item-title>{{ item.raw.name }}</v-list-item-title>
                        <v-list-item-subtitle>{{ item.raw.description }}</v-list-item-subtitle>
                      </v-list-item>
                    </template>
                  </v-autocomplete>
                </v-col>

                <v-col cols="12" md="4">
                  <v-autocomplete
                    v-model="formData.category2"
                    :items="subCategories"
                    item-title="name"
                    item-value="name"
                    label="Sous-catégorie"
                    placeholder="Rechercher une sous-catégorie..."
                    :loading="loadingSubCategories"
                    clearable
                    :disabled="!formData.category"
                  >
                    <template v-slot:item="{ props, item }">
                      <v-list-item v-bind="props">
                        <template v-slot:prepend>
                          <v-icon color="grey-lighten-1">mdi-tag-outline</v-icon>
                        </template>
                        <v-list-item-title>{{ item.raw.name }}</v-list-item-title>
                        <v-list-item-subtitle>{{ item.raw.description }}</v-list-item-subtitle>
                      </v-list-item>
                    </template>
                  </v-autocomplete>
                </v-col>

                <v-col cols="12" md="4">
                  <v-text-field
                    v-model="formData.category3"
                    label="Sous-sous-catégorie"
                    placeholder="Ex: tomates-bio"
                    :disabled="!formData.category2"
                  ></v-text-field>
                </v-col>

                <!-- Images -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">📸 Images du Produit</h3>
                </v-col>

                <v-col cols="12">
                  <v-file-input
                    v-model="imageFiles"
                    label="Sélectionner des images"
                    multiple
                    accept="image/*"
                    prepend-icon="mdi-camera"
                    show-size
                    counter
                    @change="handleImageUpload"
                  ></v-file-input>
                </v-col>

                <!-- Aperçu des images -->
                <v-col cols="12" v-if="imagePreviews.length > 0">
                  <v-row>
                    <v-col 
                      v-for="(preview, index) in imagePreviews" 
                      :key="index" 
                      cols="12" 
                      sm="6" 
                      md="3"
                    >
                      <v-card>
                        <v-img
                          :src="preview"
                          height="150"
                          cover
                        ></v-img>
                        <v-card-actions>
                          <v-btn
                            icon
                            size="small"
                            color="error"
                            @click="removeImage(index)"
                          >
                            <v-icon>mdi-delete</v-icon>
                          </v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-col>
                  </v-row>
                </v-col>

                <!-- Informations agricoles -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">🌾 Informations Agricoles</h3>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.origin"
                    label="Origine/Région"
                    placeholder="Ex: Douala, Cameroun"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.farmingMethod"
                    label="Méthode de culture"
                    placeholder="Ex: Bio, Conventionnel, Permaculture"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.season"
                    label="Saison de production"
                    placeholder="Ex: Toute l'année, Saison sèche"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.unit"
                    label="Unité de vente"
                    placeholder="Ex: kg, pièce, litre"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model.number="formData.weight"
                    label="Poids moyen"
                    type="number"
                    placeholder="1.5"
                    suffix="kg"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.storageConditions"
                    label="Conditions de stockage"
                    placeholder="Ex: Frais, Sec, Réfrigéré"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.nutritionalInfo"
                    label="Informations nutritionnelles"
                    placeholder="Ex: Riche en vitamine C"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.allergens"
                    label="Allergènes"
                    placeholder="Ex: Aucun, Gluten, Noix"
                  ></v-text-field>
                </v-col>

                <!-- Options -->
                <v-col cols="12">
                  <h3 class="text-h6 mb-4 text-warning">⚙️ Options</h3>
                </v-col>

                <v-col cols="12" md="4">
                  <v-switch
                    v-model="formData.isOrganic"
                    label="Produit Bio"
                    color="success"
                  ></v-switch>
                </v-col>

                <v-col cols="12" md="4">
                  <v-switch
                    v-model="formData.isLocal"
                    label="Produit Local"
                    color="primary"
                  ></v-switch>
                </v-col>

                <v-col cols="12" md="4">
                  <v-switch
                    v-model="formData.isAvailable"
                    label="Disponible immédiatement"
                    color="warning"
                  ></v-switch>
                </v-col>
              </v-row>
            </v-card-text>

            <v-card-actions class="pa-4">
              <v-spacer></v-spacer>
              <v-btn
                variant="text"
                @click="resetForm"
                :disabled="submitting"
              >
                Annuler
              </v-btn>
              <v-btn
                color="warning"
                type="submit"
                :loading="submitting"
              >
                <v-icon left>mdi-send</v-icon>
                Soumettre pour Validation
              </v-btn>
            </v-card-actions>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de confirmation -->
    <v-dialog v-model="confirmDialog" max-width="500">
      <v-card>
        <v-card-title>Confirmer la soumission</v-card-title>
        <v-card-text>
          Votre produit sera soumis pour validation par l'administrateur. 
          Vous recevrez une notification une fois la décision prise.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="confirmDialog = false">Annuler</v-btn>
          <v-btn color="warning" @click="confirmSubmission" :loading="submitting">
            Confirmer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { createProduct } from '@/services/products'
import { getAllCategories, getSubCategoriesByCategoryName } from '@/services/categories'
import { useRouter } from 'vue-router'

const router = useRouter()

// État du formulaire avec reactive pour une meilleure réactivité
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
  category3: '',
  origin: '',
  farmingMethod: '',
  season: '',
  unit: '',
  weight: 0,
  storageConditions: '',
  nutritionalInfo: '',
  allergens: '',
  isOrganic: false,
  isLocal: false,
  isAvailable: true
})

const imageFiles = ref([])
const imagePreviews = ref([])
const submitting = ref(false)
const confirmDialog = ref(false)
const productFormRef = ref(null)

// État des catégories
const categories = ref([])
const subCategories = ref([])
const loadingCategories = ref(false)
const loadingSubCategories = ref(false)

// Validation du formulaire
const isFormValid = computed(() => {
  const isValid = formData.title && 
         formData.description && 
         formData.mrpPrice > 0 && 
         formData.sellingPrice > 0 && 
         formData.supplierAvailableQuantity >= 0 &&
         formData.category
  console.log('🔍 Validation isFormValid:', {
    title: formData.title,
    description: formData.description,
    mrpPrice: formData.mrpPrice,
    sellingPrice: formData.sellingPrice,
    category: formData.category,
    isValid
  })
  return isValid
})

// Gestion des images
const handleImageUpload = (files) => {
  if (files) {
    imagePreviews.value = []
    files.forEach(file => {
      const reader = new FileReader()
      reader.onload = (e) => {
        imagePreviews.value.push(e.target.result)
      }
      reader.readAsDataURL(file)
    })
  }
}

const removeImage = (index) => {
  imagePreviews.value.splice(index, 1)
  imageFiles.value.splice(index, 1)
}

// Charger les catégories
const loadCategories = async () => {
  loadingCategories.value = true
  try {
    const response = await getAllCategories()
    categories.value = response || []
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  } finally {
    loadingCategories.value = false
  }
}

// Charger les sous-catégories quand une catégorie est sélectionnée
const loadSubCategories = async (categoryName) => {
  if (!categoryName) {
    subCategories.value = []
    return
  }
  
  loadingSubCategories.value = true
  try {
    const response = await getSubCategoriesByCategoryName(categoryName)
    subCategories.value = response || []
  } catch (error) {
    console.error('Erreur lors du chargement des sous-catégories:', error)
    // En cas d'erreur, on peut afficher des sous-catégories par défaut
    subCategories.value = []
  } finally {
    loadingSubCategories.value = false
  }
}

// Watcher pour charger les sous-catégories quand la catégorie change
watch(() => formData.category, (newCategory) => {
  if (newCategory) {
    loadSubCategories(newCategory)
  } else {
    subCategories.value = []
    formData.category2 = ''
    formData.category3 = ''
  }
})

// Watcher pour réinitialiser la sous-sous-catégorie quand la sous-catégorie change
watch(() => formData.category2, (newSubCategory) => {
  if (!newSubCategory) {
    formData.category3 = ''
  }
})

// Validation manuelle simple
const validateForm = () => {
  const errors = []
  
  if (!formData.title || formData.title.trim() === '') {
    errors.push('Le nom du produit est requis')
  }
  
  if (!formData.description || formData.description.trim() === '') {
    errors.push('La description est requise')
  }
  
  if (!formData.mrpPrice || formData.mrpPrice <= 0) {
    errors.push('Le prix MRP doit être supérieur à 0')
  }
  
  if (!formData.sellingPrice || formData.sellingPrice <= 0) {
    errors.push('Le prix de vente doit être supérieur à 0')
  }
  
  if (formData.supplierAvailableQuantity < 0) {
    errors.push('La quantité disponible doit être positive ou nulle')
  }
  
  if (!formData.category || formData.category.trim() === '') {
    errors.push('La catégorie est requise')
  }
  
  return errors
}

// Soumission du formulaire
const submitProduct = (event) => {
  event.preventDefault()
  console.log('🔍 Tentative de soumission du formulaire')
  console.log('🔍 Données du formulaire:', formData)
  
  const validationErrors = validateForm()
  
  if (validationErrors.length > 0) {
    console.log('❌ Erreurs de validation:', validationErrors)
    alert('Erreurs de validation:\n' + validationErrors.join('\n'))
    return
  }
  
  console.log('✅ Validation réussie, ouverture du dialog')
  confirmDialog.value = true
}

const confirmSubmission = async () => {
  console.log('🚀 Confirmation de soumission')
  submitting.value = true
  try {
    // Préparer les données pour l'API
    const productData = {
      ...formData,
      images: imagePreviews.value
    }
    
    console.log('📤 Données envoyées à l\'API:', productData)

    await createProduct(productData)
    
    console.log('✅ Produit créé avec succès')
    // Rediriger vers la liste des produits
    router.push('/supplier/products')
    
  } catch (error) {
    console.error('❌ Erreur lors de la création du produit:', error)
    alert('Erreur lors de la création du produit: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
    confirmDialog.value = false
  }
}

const resetForm = () => {
  // Réinitialiser tous les champs
  Object.assign(formData, {
    title: '',
    description: '',
    mrpPrice: 0,
    sellingPrice: 0,
    supplierAvailableQuantity: 0,
    color: '',
    sizes: '',
    category: '',
    category2: '',
    category3: '',
    origin: '',
    farmingMethod: '',
    season: '',
    unit: '',
    weight: 0,
    storageConditions: '',
    nutritionalInfo: '',
    allergens: '',
    isOrganic: false,
    isLocal: false,
    isAvailable: true
  })
  
  imageFiles.value = []
  imagePreviews.value = []
  
  if (productFormRef.value) {
    productFormRef.value.reset()
  }
}

// Charger les catégories au montage du composant
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.text-warning {
  color: #ff9800 !important;
}
</style>
