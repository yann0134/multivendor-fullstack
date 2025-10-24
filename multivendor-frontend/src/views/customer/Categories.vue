<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">🏷️ Nos Catégories</h1>
        <p class="text-body-1 text-grey mb-6">
          Découvrez nos produits organisés par catégories pour faciliter votre recherche
        </p>
      </v-col>
    </v-row>

    <!-- Catégories principales -->
    <v-row>
      <v-col 
        v-for="category in categories" 
        :key="category.id"
        cols="12" 
        sm="6" 
        md="4" 
        lg="3"
      >
        <v-card 
          class="category-card pa-4" 
          :style="{ borderLeft: `4px solid ${getCategoryColor(category.type)}` }"
          @click="goToCategory(category)"
        >
          <div class="d-flex align-center">
            <v-icon 
              :color="getCategoryColor(category.type)" 
              size="48" 
              class="mr-4"
            >
              {{ getCategoryIcon(category.type) }}
            </v-icon>
            <div>
              <h3 class="text-h6">{{ category.name }}</h3>
              <p class="text-body-2 text-grey">{{ category.description }}</p>
              <v-chip 
                :color="getCategoryColor(category.type)" 
                size="small" 
                class="mt-2"
              >
                {{ category.type }}
              </v-chip>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Sous-catégories si une catégorie est sélectionnée -->
    <v-row v-if="selectedCategory" class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">
          Sous-catégories de {{ selectedCategory.name }}
        </h2>
        <v-row>
          <v-col 
            v-for="subCategory in subCategories" 
            :key="subCategory.id"
            cols="12" 
            sm="6" 
            md="4"
          >
            <v-card 
              class="subcategory-card pa-3" 
              @click="goToSubCategory(subCategory)"
            >
              <div class="d-flex align-center">
                <v-icon size="32" class="mr-3">mdi-tag</v-icon>
                <div>
                  <h4 class="text-subtitle-1">{{ subCategory.name }}</h4>
                  <p class="text-caption text-grey">{{ subCategory.description }}</p>
                </div>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- Produits de la catégorie sélectionnée -->
    <v-row v-if="selectedCategory && categoryProducts.length > 0" class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">
          Produits dans {{ selectedCategory.name }}
        </h2>
        <v-row>
          <v-col 
            v-for="product in categoryProducts" 
            :key="product.id"
            cols="12" 
            sm="6" 
            md="4" 
            lg="3"
          >
            <ProductCard :product="product" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/products'
import ProductCard from '@/components/customer/ProductCard.vue'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()

const categories = ref([])
const subCategories = ref([])
const selectedCategory = ref(null)
const categoryProducts = ref([])

const getCategoryColor = (type) => {
  const colors = {
    'VEGETAL': 'green',
    'ANIMAL': 'orange'
  }
  return colors[type] || 'primary'
}

const getCategoryIcon = (type) => {
  const icons = {
    'VEGETAL': 'mdi-sprout',
    'ANIMAL': 'mdi-cow'
  }
  return icons[type] || 'mdi-package-variant'
}

const goToCategory = async (category) => {
  selectedCategory.value = category
  try {
    // Charger les sous-catégories
    await productStore.fetchSubCategories(category.id)
    subCategories.value = productStore.subCategories
    
    // Charger les produits de la catégorie
    const data = await productStore.fetchProductsByCategory(category.id)
    categoryProducts.value = productStore.products
  } catch (error) {
    console.error('Erreur lors du chargement de la catégorie:', error)
  }
}

const goToSubCategory = async (subCategory) => {
  try {
    // Charger les produits de la sous-catégorie
    const data = await productStore.fetchProductsBySubCategory(subCategory.id)
    categoryProducts.value = productStore.products
  } catch (error) {
    console.error('Erreur lors du chargement de la sous-catégorie:', error)
  }
}

onMounted(async () => {
  try {
    await productStore.fetchCategories()
    categories.value = productStore.categories
  } catch (error) {
    console.error('Erreur lors du chargement des catégories:', error)
  }
})
</script>

<style scoped>
.category-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.subcategory-card {
  cursor: pointer;
  transition: transform 0.2s;
  border: 1px solid #e0e0e0;
}

.subcategory-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
</style>