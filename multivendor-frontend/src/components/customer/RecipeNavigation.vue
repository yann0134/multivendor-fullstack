<template>
  <v-card class="mb-6" color="primary" dark>
    <v-card-text class="pa-4">
      <div class="d-flex align-center justify-space-between">
        <div class="d-flex align-center">
          <v-icon size="32" class="mr-3">mdi-chef-hat</v-icon>
          <div>
            <h2 class="text-h5 mb-1">Recettes de Cuisine</h2>
            <p class="text-body-2 mb-0">Découvrez et créez vos recettes</p>
          </div>
        </div>
        
        <div class="d-flex gap-2">
          <v-btn
            color="white"
            variant="outlined"
            to="/customer/recipes"
            class="text-capitalize"
          >
            <v-icon left>mdi-home</v-icon>
            Accueil
          </v-btn>
          <v-btn
            color="white"
            variant="outlined"
            to="/customer/recipes/discover"
            class="text-capitalize"
          >
            <v-icon left>mdi-compass</v-icon>
            Découvrir
          </v-btn>
          <v-btn
            color="white"
            variant="outlined"
            to="/customer/recipes/my-recipes"
            class="text-capitalize"
          >
            <v-icon left>mdi-book-open-variant</v-icon>
            Mes recettes
          </v-btn>
          <v-btn
            color="white"
            to="/customer/recipes/create"
            class="text-capitalize"
          >
            <v-icon left>mdi-plus</v-icon>
            Créer
          </v-btn>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup>
// Pas de logique nécessaire pour ce composant de navigation
</script>

<style scoped>
.recipe-navigation {
  background: linear-gradient(45deg, #4CAF50, #8BC34A);
}
</style>