<template>
  <div class="product-image-gallery">
    <!-- Image principale -->
    <div class="main-image-container">
      <v-img
        :src="mainImageUrl"
        :alt="productName"
        class="main-image"
        @click="openLightbox(0)"
      >
        <template v-slot:placeholder>
          <v-row class="fill-height ma-0" align="center" justify="center">
            <v-progress-circular indeterminate color="primary"></v-progress-circular>
          </v-row>
        </template>
      </v-img>
      
      <!-- Badge image principale -->
      <v-chip
        v-if="images.length > 1"
        class="main-image-badge"
        color="primary"
        small
      >
        {{ currentImageIndex + 1 }}/{{ images.length }}
      </v-chip>
    </div>

    <!-- Miniatures -->
    <div v-if="images.length > 1" class="thumbnails-container">
      <v-row no-gutters>
        <v-col
          v-for="(image, index) in images"
          :key="index"
          cols="3"
          class="pa-1"
        >
          <v-img
            :src="image.thumbnailUrl || image.url"
            :alt="image.altText || productName"
            class="thumbnail"
            :class="{ 'thumbnail-active': index === currentImageIndex }"
            @click="setMainImage(index)"
          >
            <template v-slot:placeholder>
              <v-skeleton-loader type="image"></v-skeleton-loader>
            </template>
          </v-img>
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
          <div class="lightbox-container">
            <!-- Image principale du lightbox -->
            <div class="lightbox-main-image">
              <v-img
                :src="currentLightboxImage"
                :alt="productName"
                class="lightbox-image"
                contain
              >
                <template v-slot:placeholder>
                  <v-row class="fill-height ma-0" align="center" justify="center">
                    <v-progress-circular indeterminate color="primary"></v-progress-circular>
                  </v-row>
                </template>
              </v-img>
            </div>

            <!-- Navigation du lightbox -->
            <div v-if="images.length > 1" class="lightbox-navigation">
              <v-btn
                icon
                large
                color="white"
                class="nav-btn nav-btn-left"
                @click="previousImage"
              >
                <v-icon>mdi-chevron-left</v-icon>
              </v-btn>
              
              <v-btn
                icon
                large
                color="white"
                class="nav-btn nav-btn-right"
                @click="nextImage"
              >
                <v-icon>mdi-chevron-right</v-icon>
              </v-btn>
            </div>

            <!-- Indicateurs -->
            <div v-if="images.length > 1" class="lightbox-indicators">
              <v-btn
                v-for="(image, index) in images"
                :key="index"
                icon
                small
                :color="index === currentLightboxIndex ? 'primary' : 'white'"
                @click="setLightboxImage(index)"
              >
                <v-icon>mdi-circle</v-icon>
              </v-btn>
            </div>
          </div>
        </v-card-text>

        <!-- Informations de l'image -->
        <v-card-actions v-if="currentImageInfo">
          <div class="image-info">
            <div class="text-caption">
              <strong>Description:</strong> {{ currentImageInfo.description || 'Aucune description' }}
            </div>
            <div class="text-caption">
              <strong>Dimensions:</strong> {{ currentImageInfo.width }}x{{ currentImageInfo.height }}px
            </div>
          </div>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'ProductImageGallery',
  props: {
    images: {
      type: Array,
      default: () => []
    },
    productName: {
      type: String,
      default: 'Produit'
    }
  },
  setup(props) {
    const currentImageIndex = ref(0)
    const lightboxOpen = ref(false)
    const currentLightboxIndex = ref(0)

    const mainImageUrl = computed(() => {
      if (props.images.length === 0) {
        return '/images/placeholder-product.jpg'
      }
      return props.images[currentImageIndex.value]?.url || props.images[0]?.url
    })

    const currentLightboxImage = computed(() => {
      if (props.images.length === 0) return ''
      return props.images[currentLightboxIndex.value]?.url || props.images[0]?.url
    })

    const currentImageInfo = computed(() => {
      if (props.images.length === 0) return null
      return props.images[currentLightboxIndex.value] || props.images[0]
    })

    const setMainImage = (index) => {
      currentImageIndex.value = index
    }

    const openLightbox = (index) => {
      currentLightboxIndex.value = index
      lightboxOpen.value = true
    }

    const nextImage = () => {
      if (currentLightboxIndex.value < props.images.length - 1) {
        currentLightboxIndex.value++
      } else {
        currentLightboxIndex.value = 0
      }
    }

    const previousImage = () => {
      if (currentLightboxIndex.value > 0) {
        currentLightboxIndex.value--
      } else {
        currentLightboxIndex.value = props.images.length - 1
      }
    }

    const setLightboxImage = (index) => {
      currentLightboxIndex.value = index
    }

    // Navigation au clavier
    const handleKeydown = (event) => {
      if (!lightboxOpen.value) return
      
      switch (event.key) {
        case 'ArrowLeft':
          previousImage()
          break
        case 'ArrowRight':
          nextImage()
          break
        case 'Escape':
          lightboxOpen.value = false
          break
      }
    }

    // Ajouter les écouteurs d'événements
    watch(lightboxOpen, (isOpen) => {
      if (isOpen) {
        document.addEventListener('keydown', handleKeydown)
      } else {
        document.removeEventListener('keydown', handleKeydown)
      }
    })

    return {
      currentImageIndex,
      lightboxOpen,
      currentLightboxIndex,
      mainImageUrl,
      currentLightboxImage,
      currentImageInfo,
      setMainImage,
      openLightbox,
      nextImage,
      previousImage,
      setLightboxImage
    }
  }
}
</script>

<style scoped>
.product-image-gallery {
  position: relative;
}

.main-image-container {
  position: relative;
  margin-bottom: 16px;
}

.main-image {
  width: 100%;
  height: 400px;
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.main-image:hover {
  transform: scale(1.02);
}

.main-image-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.thumbnails-container {
  margin-top: 16px;
}

.thumbnail {
  width: 100%;
  height: 80px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.thumbnail:hover {
  transform: scale(1.05);
  border-color: #1976d2;
}

.thumbnail-active {
  border-color: #1976d2 !important;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.3);
}

.lightbox-card {
  background: rgba(0, 0, 0, 0.9);
  color: white;
}

.lightbox-container {
  position: relative;
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-main-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.lightbox-navigation {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  pointer-events: none;
}

.nav-btn {
  pointer-events: all;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.nav-btn-left {
  margin-left: 16px;
}

.nav-btn-right {
  margin-right: 16px;
}

.lightbox-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.image-info {
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  margin: 16px;
}

/* Responsive */
@media (max-width: 768px) {
  .main-image {
    height: 300px;
  }
  
  .thumbnail {
    height: 60px;
  }
  
  .lightbox-container {
    height: 60vh;
  }
}
</style>