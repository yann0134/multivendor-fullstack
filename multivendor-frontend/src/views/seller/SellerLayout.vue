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
          <v-icon size="32" color="success" class="mr-2">🛒</v-icon>
          <span class="text-h6 font-weight-bold">AgriMarket</span>
        </div>
        <v-divider class="mb-4"></v-divider>
        <div class="text-caption text-grey-600 mb-2">Espace Vendeur</div>
        <div class="text-body-2 font-weight-medium">{{ userEmail }}</div>
      </div>

      <v-list density="comfortable">
        <v-list-item 
          prepend-icon="mdi-view-dashboard" 
          title="Tableau de bord" 
          to="/seller"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-success font-weight-bold">
          🛍️ Gestion Boutique
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-package-variant" 
          title="Mes Produits" 
          to="/seller/products"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-plus-circle" 
          title="Ajouter Produit" 
          to="/seller/products/add"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-image-multiple" 
          title="Galerie Images" 
          to="/seller/products/images"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-tag-multiple" 
          title="Catégories" 
          to="/seller/categories"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-success font-weight-bold">
          📦 Commandes & Ventes
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-clipboard-list" 
          title="Mes Commandes" 
          to="/seller/orders"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-cart" 
          title="Ventes en Cours" 
          to="/seller/sales"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-check-circle" 
          title="Commandes Terminées" 
          to="/seller/completed-orders"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-success font-weight-bold">
          💰 Finances & Analytics
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-currency-usd" 
          title="Revenus" 
          to="/seller/earnings"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-chart-line" 
          title="Rapports" 
          to="/seller/reports"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-chart-pie" 
          title="Statistiques" 
          to="/seller/analytics"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-success font-weight-bold">
          👤 Mon Compte
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-account" 
          title="Mon Profil" 
          to="/seller/profile"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-store" 
          title="Ma Boutique" 
          to="/seller/store"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-logout" 
          title="Déconnexion" 
          @click="logout"
          class="mb-1"
        />
      </v-list>
    </v-navigation-drawer>

    <!-- Barre d'application -->
    <v-app-bar color="success" dark elevation="2" class="agri-fade-in">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      
      <v-toolbar-title class="d-flex align-center">
        <v-icon size="24" class="mr-2">🛒</v-icon>
        <span class="text-h6">AgriMarket - Espace Vendeur</span>
      </v-toolbar-title>
      
      <v-spacer />
      
      <!-- Notifications ventes -->
      <v-btn icon class="mr-2">
        <v-badge content="3" color="orange" size="small">
        <v-icon>mdi-bell</v-icon>
        </v-badge>
      </v-btn>
      
      <!-- Menu utilisateur -->
      <v-menu>
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
            <v-list-item-title>Ma Boutique</v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-title>Déconnexion</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
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

const router = useRouter()
const authStore = useAuthStore()

const drawer = ref(false)

// État calculé
const userEmail = computed(() => authStore.user?.email || 'seller@agrimarket.com')

// Méthodes
const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>
