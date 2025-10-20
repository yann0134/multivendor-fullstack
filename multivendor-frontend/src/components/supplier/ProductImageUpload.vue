<template>
  <v-card class="product-image-upload">
    <v-card-title class="d-flex align-center">
      <v-icon class="mr-2">mdi-image-multiple</v-icon>
      Images du Produit
    </v-card-title>

    <v-card-text>
      <!-- Zone de drop pour upload multiple -->
      <v-file-input
        v-model="selectedFiles"
        multiple
        accept="image/*"
        label="Sélectionner des images"
        prepend-icon="mdi-camera"
        show-size
        counter
        @change="handleFileSelection"
      />

      <!-- Bouton d'upload -->
      <v-btn
        color="primary"
        :loading="uploading"
        :disabled="!selectedFiles || selectedFiles.length === 0"
        @click="uploadImages"
        class="mt-4"
      >
        <v-icon left>mdi-upload</v-icon>
        Uploader les Images
      </v-btn>

      <!-- Aperçu des images sélectionnées -->
      <div v-if="previewImages.length > 0" class="mt-4">
        <h4 class="mb-2">Aperçu des images :</h4>
        <v-row>
          <v-col
            v-for="(preview, index) in previewImages"
            :key="index"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card class="preview-card">
              <v-img
                :src="preview.url"
                height="150"
                cover
              />
              <v-card-actions>
                <v-btn
                  icon
                  color="red"
                  @click="removePreview(index)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <!-- Images existantes du produit -->
      <div v-if="productImages.length > 0" class="mt-6">
        <h4 class="mb-2">Images actuelles :</h4>
        <v-row>
          <v-col
            v-for="image in productImages"
            :key="image.id"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card class="image-card" :class="{ 'main-image': image.isMainImage }">
              <v-img
                :src="getImageUrl(image)"
                height="150"
                cover
                @click="viewImage(image)"
              />
              <v-card-actions class="pa-2">
                <v-btn
                  icon
                  size="small"
                  :color="image.isMainImage ? 'primary' : 'grey'"
                  @click="setAsMain(image.id)"
                  :disabled="image.isMainImage"
                >
                  <v-icon>mdi-star</v-icon>
                </v-btn>
                <v-btn
                  icon
                  size="small"
                  color="red"
                  @click="deleteImage(image.id)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
              <v-chip
                v-if="image.isMainImage"
                color="primary"
                size="small"
                class="ma-2"
              >
                Image principale
              </v-chip>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <!-- Message d'information -->
      <v-alert
        type="info"
        variant="tonal"
        class="mt-4"
      >
        <v-alert-title>Conseils pour les images :</v-alert-title>
        <ul>
          <li>Format recommandé : JPG, PNG, WebP</li>
          <li>Taille maximale : 5MB par image</li>
          <li>Résolution minimale : 800x600px</li>
          <li>Ajoutez 3-10 images par produit</li>
          <li>Définissez une image principale</li>
        </ul>
      </v-alert>
    </v-card-text>

    <!-- Dialog pour voir l'image en grand -->
    <v-dialog v-model="imageDialog" max-width="800">
      <v-card v-if="selectedImage">
        <v-card-title>
          {{ selectedImage.altText || 'Image du produit' }}
        </v-card-title>
        <v-img
          :src="getImageUrl(selectedImage)"
          max-height="600"
        />
        <v-card-text>
          <p><strong>Description :</strong> {{ selectedImage.description || 'Aucune description' }}</p>
          <p><strong>Taille :</strong> {{ selectedImage.width }}x{{ selectedImage.height }}px</p>
          <p><strong>Type :</strong> {{ selectedImage.imageType }}</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="imageDialog = false">Fermer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useProductImageStore } from '@/stores/productImages'

const props = defineProps({
  productId: {
    type: Number,
    required: true
  }
})

const productImageStore = useProductImageStore()

// État local
const selectedFiles = ref([])
const previewImages = ref([])
const productImages = ref([])
const uploading = ref(false)
const imageDialog = ref(false)
const selectedImage = ref(null)

// Méthodes
const handleFileSelection = (files) => {
  if (files && files.length > 0) {
    previewImages.value = []
    Array.from(files).forEach(file => {
      if (file.type.startsWith('image/')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          previewImages.value.push({
            file,
            url: e.target.result,
            name: file.name,
            size: file.size
          })
        }
        reader.readAsDataURL(file)
      }
    })
  }
}

const uploadImages = async () => {
  if (!selectedFiles.value || selectedFiles.value.length === 0) return

  uploading.value = true
  try {
    const formData = new FormData()
    selectedFiles.value.forEach(file => {
      formData.append('files', file)
    })

    const response = await productImageStore.uploadMultipleImages(props.productId, formData)
    
    if (response.success) {
      // Recharger les images du produit
      await loadProductImages()
      
      // Réinitialiser
      selectedFiles.value = []
      previewImages.value = []
      
      // Notification de succès
      console.log('Images uploadées avec succès')
    }
  } catch (error) {
    console.error('Erreur lors de l\'upload:', error)
  } finally {
    uploading.value = false
  }
}

const loadProductImages = async () => {
  try {
    const response = await productImageStore.getProductImages(props.productId)
    if (response.success) {
      productImages.value = response.images
    }
  } catch (error) {
    console.error('Erreur lors du chargement des images:', error)
  }
}

const setAsMain = async (imageId) => {
  try {
    const response = await productImageStore.setMainImage(imageId)
    if (response.success) {
      await loadProductImages()
    }
  } catch (error) {
    console.error('Erreur lors de la définition de l\'image principale:', error)
  }
}

const deleteImage = async (imageId) => {
  if (confirm('Êtes-vous sûr de vouloir supprimer cette image ?')) {
    try {
      const response = await productImageStore.deleteImage(imageId)
      if (response.success) {
        await loadProductImages()
      }
    } catch (error) {
      console.error('Erreur lors de la suppression:', error)
    }
  }
}

const viewImage = (image) => {
  selectedImage.value = image
  imageDialog.value = true
}

const removePreview = (index) => {
  previewImages.value.splice(index, 1)
  selectedFiles.value.splice(index, 1)
}

const getImageUrl = (image) => {
  return image.imageUrl || '/placeholder-image.jpg'
}

// Lifecycle
onMounted(() => {
  loadProductImages()
})

// Watcher pour recharger les images quand le productId change
watch(() => props.productId, () => {
  if (props.productId) {
    loadProductImages()
  }
})
</script>

<style scoped>
.product-image-upload {
  margin-bottom: 20px;
}

.preview-card,
.image-card {
  position: relative;
}

.main-image {
  border: 2px solid #1976d2;
}

.image-card:hover {
  transform: scale(1.02);
  transition: transform 0.2s;
}

.v-chip {
  position: absolute;
  top: 8px;
  right: 8px;
}
</style>
