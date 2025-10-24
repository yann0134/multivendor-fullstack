<template>
  <v-dialog v-model="isOpen" max-width="500" persistent>
    <v-card>
      <v-card-title class="text-h5 text-center pa-4">
        <v-icon color="primary" size="large" class="mr-2">mdi-account-circle</v-icon>
        Connexion requise
      </v-card-title>
      
      <v-card-text class="text-center pa-4">
        <p class="text-body-1 mb-4">
          Pour ajouter des produits à votre panier, veuillez vous connecter ou créer un compte.
        </p>
        
        <div class="d-flex flex-column gap-3">
          <v-btn
            color="primary"
            size="large"
            block
            to="/login"
            @click="closeModal"
            class="text-none"
          >
            <v-icon left>mdi-login</v-icon>
            Se connecter
          </v-btn>
          
          <v-btn
            color="secondary"
            size="large"
            block
            to="/register"
            @click="closeModal"
            class="text-none"
          >
            <v-icon left>mdi-account-plus</v-icon>
            Créer un compte
          </v-btn>
        </div>
      </v-card-text>
      
      <v-card-actions class="justify-center pa-4">
        <v-btn
          variant="text"
          @click="closeModal"
          class="text-none"
        >
          Continuer sans compte
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref } from 'vue'

const isOpen = ref(false)

const openModal = () => {
  isOpen.value = true
}

const closeModal = () => {
  isOpen.value = false
}

// Exposer les méthodes pour le parent
defineExpose({
  openModal,
  closeModal
})
</script>

<style scoped>
.gap-3 {
  gap: 12px;
}
</style>
