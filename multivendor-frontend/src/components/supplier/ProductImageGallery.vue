<template>
  <div class="product-image-gallery">
    <!-- Image principale -->
    <div v-if="images && images.length > 0" class="main-image-container">
        <v-img
          :src="selectedImage || images[0].imageUrl"
          height="400"
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
        height="400"
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
    <div v-if="images && images.length > 1" class="thumbnails-container mt-4">
      <div class="d-flex flex-wrap gap-2">
        <div
          v-for="(image, index) in images"
          :key="index"
          class="thumbnail-wrapper"
          style="position: relative;"
        >
          <v-img
            :src="image.imageUrl"
            height="80"
            width="80"
            class="rounded thumbnail"
            :class="{ 'thumbnail-selected': selectedImage === image.imageUrl }"
            @click="selectedImage = image.imageUrl"
            style="cursor: pointer; border: 2px solid transparent;"
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-icon color="grey">mdi-image</v-icon>
              </div>
            </template>
          </v-img>
          
          <!-- Bouton de suppression -->
          <v-btn
            v-if="showDeleteButtons"
            icon
            size="small"
            color="error"
            variant="elevated"
            class="delete-btn"
            @click="emit('delete-image', image.id)"
            style="position: absolute; top: -8px; right: -8px; z-index: 1;"
          >
            <v-icon size="16">mdi-close</v-icon>
          </v-btn>
        </div>
      </div>
    </div>

    <!-- Informations sur les images -->
    <v-alert
      v-if="images && images.length > 0"
      type="info"
      variant="tonal"
      class="mt-4"
      density="compact"
    >
      <v-icon left>mdi-information</v-icon>
      {{ images.length }} image(s) disponible(s)
    </v-alert>

    <!-- Lightbox -->
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
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>
            </template>
          </v-img>
        </v-card-text>
        <v-card-actions v-if="images && images.length > 1">
          <v-spacer></v-spacer>
          <v-btn
            icon
            @click="previousImage"
            :disabled="currentImageIndex === 0"
          >
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>
          <span class="mx-4">{{ currentImageIndex + 1 }} / {{ images.length }}</span>
          <v-btn
            icon
            @click="nextImage"
            :disabled="currentImageIndex === images.length - 1"
          >
            <v-icon>mdi-chevron-right</v-icon>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  },
  showDeleteButtons: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['delete-image'])

const selectedImage = ref(null)
const lightboxOpen = ref(false)

// Computed properties
const currentImageIndex = computed(() => {
  if (!props.images || !selectedImage.value) return 0
  return props.images.findIndex(img => img.imageUrl === selectedImage.value)
})

// Watchers
watch(() => props.images, (newImages) => {
  if (newImages && newImages.length > 0) {
    selectedImage.value = newImages[0].imageUrl
  }
}, { immediate: true })

// Methods
const openLightbox = () => {
  if (props.images && props.images.length > 0) {
    lightboxOpen.value = true
  }
}

const previousImage = () => {
  if (currentImageIndex.value > 0) {
    selectedImage.value = props.images[currentImageIndex.value - 1].imageUrl
  }
}

const nextImage = () => {
  if (currentImageIndex.value < props.images.length - 1) {
    selectedImage.value = props.images[currentImageIndex.value + 1].imageUrl
  }
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
</script>

<style scoped>
.product-image-gallery {
  width: 100%;
}

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
</style>
