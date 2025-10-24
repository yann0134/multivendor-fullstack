<template>
  <div class="product-gallery">
    <!-- Image principale -->
    <v-img
      :src="currentImage"
      height="400"
      cover
      class="main-image"
      @click="openLightbox"
    >
      <template v-slot:placeholder>
        <div class="d-flex align-center justify-center fill-height">
          <v-progress-circular indeterminate color="primary" />
        </div>
      </template>
    </v-img>

    <!-- Miniatures -->
    <div v-if="images.length > 1" class="thumbnails mt-4">
      <v-row>
        <v-col 
          v-for="(image, index) in images" 
          :key="index"
          cols="3"
          class="pa-1"
        >
          <v-img
            :src="image.url"
            height="80"
            cover
            class="thumbnail"
            :class="{ 'thumbnail-active': index === currentIndex }"
            @click="setCurrentImage(index)"
          />
        </v-col>
      </v-row>
    </div>

    <!-- Lightbox -->
    <v-dialog v-model="lightboxOpen" max-width="90vw" max-height="90vh">
      <v-card class="lightbox-card">
        <v-card-title class="d-flex align-center justify-space-between">
          <span>{{ productName }}</span>
          <v-btn icon @click="lightboxOpen = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text class="pa-0">
          <v-img
            :src="currentImage"
            max-height="70vh"
            contain
            class="lightbox-image"
          />
        </v-card-text>
        <v-card-actions v-if="images.length > 1">
          <v-btn 
            icon 
            @click="previousImage"
            :disabled="currentIndex === 0"
          >
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>
          <v-spacer />
          <span class="text-caption">{{ currentIndex + 1 }} / {{ images.length }}</span>
          <v-spacer />
          <v-btn 
            icon 
            @click="nextImage"
            :disabled="currentIndex === images.length - 1"
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
import { getDefaultProductImage } from '@/utils/defaultImages'

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  },
  productName: {
    type: String,
    default: 'Produit'
  }
})

const currentIndex = ref(0)
const lightboxOpen = ref(false)

const currentImage = computed(() => {
  if (props.images.length === 0) {
    return getDefaultImage()
  }
  return props.images[currentIndex.value]?.url || getDefaultImage()
})

const getDefaultImage = () => {
  return getDefaultProductImage({ category: { type: 'VEGETAL' } })
}

const setCurrentImage = (index) => {
  currentIndex.value = index
}

const openLightbox = () => {
  if (props.images.length > 0) {
    lightboxOpen.value = true
  }
}

const nextImage = () => {
  if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++
  }
}

const previousImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// Réinitialiser l'index quand les images changent
watch(() => props.images, () => {
  currentIndex.value = 0
})
</script>

<style scoped>
.product-gallery {
  position: relative;
}

.main-image {
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.2s;
}

.main-image:hover {
  transform: scale(1.02);
}

.thumbnails {
  max-height: 100px;
  overflow-x: auto;
}

.thumbnail {
  cursor: pointer;
  border-radius: 4px;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail:hover {
  border-color: #1976d2;
}

.thumbnail-active {
  border-color: #1976d2;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.3);
}

.lightbox-card {
  background-color: rgba(0, 0, 0, 0.9);
  color: white;
}

.lightbox-image {
  background-color: white;
}
</style>