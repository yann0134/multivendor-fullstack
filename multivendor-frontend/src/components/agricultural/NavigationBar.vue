<template>
  <v-app-bar color="primary" dark elevation="2">
    <!-- Logo et titre -->
    <v-app-bar-title class="d-flex align-center">
      <v-icon size="32" class="mr-2">🌱</v-icon>
      <span class="text-h6 font-weight-bold">AgriMarket</span>
    </v-app-bar-title>

    <!-- Navigation principale -->
    <v-spacer></v-spacer>
    
    <!-- Menu de navigation -->
    <v-btn 
      v-for="item in navItems" 
      :key="item.name"
      :to="item.to"
      variant="text"
      class="mx-1"
    >
      <v-icon class="mr-1">{{ item.icon }}</v-icon>
      {{ item.name }}
    </v-btn>

    <!-- Barre de recherche -->
    <v-text-field
      v-model="searchQuery"
      placeholder="Rechercher des produits..."
      prepend-inner-icon="mdi-magnify"
      variant="outlined"
      density="compact"
      hide-details
      class="mx-4"
      style="max-width: 300px;"
      @keyup.enter="handleSearch"
    />

    <!-- Menu utilisateur -->
    <v-menu v-if="isAuthenticated">
      <template v-slot:activator="{ props }">
        <v-btn icon v-bind="props">
          <v-avatar size="32" color="white">
            <v-icon>mdi-account</v-icon>
          </v-avatar>
        </v-btn>
      </template>
      
      <v-list>
        <v-list-item>
          <v-list-item-title>Mon Profil</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>Mes Commandes</v-list-item-title>
        </v-list-item>
        <v-list-item @click="logout">
          <v-list-item-title>Déconnexion</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>

    <!-- Boutons d'authentification -->
    <template v-else>
      <v-btn 
        to="/login" 
        variant="outlined" 
        color="white"
        class="mr-2"
      >
        Connexion
      </v-btn>
      <v-btn 
        to="/register" 
        color="white"
      >
        Inscription
      </v-btn>
    </template>

    <!-- Panier -->
    <v-btn 
      icon 
      to="/customer/cart"
      class="ml-2"
    >
      <v-badge 
        :content="cartItemCount" 
        :value="cartItemCount"
        color="red"
      >
        <v-icon>mdi-cart</v-icon>
      </v-badge>
    </v-btn>
  </v-app-bar>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const searchQuery = ref('')

// État calculé
const isAuthenticated = computed(() => authStore.isAuthenticated)
const cartItemCount = computed(() => cartStore.totalItems)

// Items de navigation
const navItems = [
  { name: 'Accueil', to: '/', icon: 'mdi-home' },
  { name: 'Produits', to: '/customer/products', icon: 'mdi-package-variant' },
  { name: 'Catégories', to: '/customer/categories', icon: 'mdi-tag-multiple' },
  { name: 'Fermiers', to: '/customer/farmers', icon: 'mdi-account-group' },
  { name: 'À Propos', to: '/about', icon: 'mdi-information' }
]

// Méthodes
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/customer/products',
      query: { search: searchQuery.value }
    })
  }
}

const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.v-app-bar {
  background: linear-gradient(135deg, #4CAF50, #2E7D32) !important;
}

.v-text-field {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.v-text-field :deep(.v-field__input) {
  color: white;
}

.v-text-field :deep(.v-field__input)::placeholder {
  color: rgba(255, 255, 255, 0.7);
}
</style>
