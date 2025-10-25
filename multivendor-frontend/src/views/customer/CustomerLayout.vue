<template>
  <v-app>
    <!-- Navigation latérale -->
    <v-navigation-drawer
      v-model="drawer"
      :temporary="!drawer"
      location="left"
      width="280"
      class="agri-slide-in agri-drawer-scroll"
    >
      <div class="pa-4">
        <div class="d-flex align-center mb-4">
          <v-icon size="32" color="primary" class="mr-2">🌱</v-icon>
          <span class="text-h6 font-weight-bold">AgriMarket</span>
        </div>
        <v-divider class="mb-4"></v-divider>
      </div>

      <v-list>
        <v-list-item 
          prepend-icon="mdi-home" 
          title="Accueil" 
          to="/"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-view-dashboard" 
          title="Mon Tableau de bord" 
          to="/customer"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-primary font-weight-bold">
          🛒 Shopping
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-package-variant" 
          title="Tous les Produits" 
          to="/customer/products"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-tag-multiple" 
          title="Catégories" 
          to="/customer/categories"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-chef-hat" 
          title="Recettes" 
          to="/customer/recipes"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-calendar-clock" 
          title="Planning de Repas" 
          to="/customer/meal-plans"
          class="mb-1"
        />
        
        <!--<v-list-item
          prepend-icon="mdi-account-group" 
          title="Nos Fermiers" 
          to="/customer/farmers"
          class="mb-1"
        />-->
        
        <v-list-item 
          prepend-icon="mdi-cart" 
          title="Mon Panier" 
          to="/customer/cart"
          class="mb-1"
        >
          <template v-slot:append>
            <v-badge 
              :content="cartItemCount" 
              :value="cartItemCount"
              color="red"
              size="small"
            />
          </template>
        </v-list-item>
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-primary font-weight-bold">
          📦 Mes Commandes
        </v-list-subheader>
        
        <v-list-item 
          v-if="authStore.isAuthenticated"
          prepend-icon="mdi-clipboard-list" 
          title="Mes Commandes" 
          to="/customer/orders"
          class="mb-1"
        />
        
        <v-divider v-if="authStore.isAuthenticated" class="my-2"></v-divider>
        
        <v-list-subheader v-if="authStore.isAuthenticated" class="text-primary font-weight-bold">
          👤 Mon Compte
        </v-list-subheader>
        
        <v-list-item 
          v-if="authStore.isAuthenticated"
          prepend-icon="mdi-account" 
          title="Mon Profil" 
          to="/customer/profile"
          class="mb-1"
        />
        
        <v-list-item 
          v-if="!authStore.isAuthenticated"
          prepend-icon="mdi-login" 
          title="Se connecter" 
          to="/login"
          class="mb-1"
        />
        
        <v-list-item 
          v-if="!authStore.isAuthenticated"
          prepend-icon="mdi-account-plus" 
          title="S'inscrire" 
          to="/register"
          class="mb-1"
        />
        
        <v-list-item 
          v-if="authStore.isAuthenticated"
          prepend-icon="mdi-logout" 
          title="Déconnexion" 
          @click="logout"
          class="mb-1"
        />
      </v-list>
    </v-navigation-drawer>

    <!-- Barre d'application -->
    <v-app-bar color="primary" dark elevation="2" class="agri-fade-in">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      
      <v-toolbar-title class="d-flex align-center">
        <v-icon size="24" class="mr-2">🌱</v-icon>
        <span class="text-h6">AgriMarket - Espace Client</span>
      </v-toolbar-title>
      
      <v-spacer />
      
      <!-- Barre de recherche -->
      <v-text-field
        v-model="searchQuery"
        placeholder="Rechercher des produits..."
        prepend-inner-icon="mdi-magnify"
        variant="outlined"
        density="compact"
        hide-details
        class="mr-4"
        style="max-width: 300px;"
        @keyup.enter="handleSearch"
      />
      
      <!-- Notifications -->
      <v-btn icon class="mr-2">
        <v-badge content="3" color="red" size="small">
          <v-icon>mdi-bell</v-icon>
        </v-badge>
      </v-btn>
      
      <!-- Panier -->
      <v-btn 
        icon 
        to="/customer/cart"
        class="mr-2"
      >
        <v-badge 
          :content="cartItemCount" 
          :value="cartItemCount"
          color="red"
        >
          <v-icon>mdi-cart</v-icon>
        </v-badge>
      </v-btn>
      
      <!-- Menu utilisateur -->
      <v-menu v-if="authStore.isAuthenticated">
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
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-title>Déconnexion</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
      
      <!-- Boutons de connexion/inscription pour les utilisateurs non authentifiés -->
      <template v-else>
        <v-btn 
          color="white" 
          variant="outlined" 
          to="/login"
          class="mr-2"
        >
          Se connecter
        </v-btn>
        <v-btn 
          color="white" 
          to="/register"
        >
          S'inscrire
        </v-btn>
      </template>
    </v-app-bar>

    <!-- Contenu principal -->
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const drawer = ref(false)
const searchQuery = ref('')

// État calculé
const cartItemCount = computed(() => cartStore.totalItems)

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
