<template>
  <v-app-bar color="primary" dark>
    <v-app-bar-nav-icon @click="toggleDrawer" />
    
    <v-toolbar-title>
      <router-link to="/" class="text-decoration-none text-white">
        Multivendor E-commerce
      </router-link>
    </v-toolbar-title>

    <v-spacer />

    <!-- Navigation selon le rôle -->
    <template v-if="authStore.isAuthenticated">
      <!-- Navigation Client -->
      <template v-if="authStore.hasRole('ROLE_CUSTOMER')">
        <v-btn to="/customer/products" text>Produits</v-btn>
        <v-btn to="/customer/cart" text>
          Panier
          <v-badge v-if="cartStore.totalItems > 0" :content="cartStore.totalItems" color="red">
            <v-icon>mdi-cart</v-icon>
          </v-badge>
          <v-icon v-else>mdi-cart</v-icon>
        </v-btn>
        <v-btn to="/customer/orders" text>Mes Commandes</v-btn>
      </template>

      <!-- Navigation Vendeur -->
      <template v-else-if="authStore.hasRole('ROLE_SELLER')">
        <v-btn to="/seller/products" text>Mes Produits</v-btn>
        <v-btn to="/seller/orders" text>Commandes</v-btn>
        <v-btn to="/seller/reports" text>Rapports</v-btn>
      </template>

      <!-- Navigation Fournisseur -->
      <template v-else-if="authStore.hasRole('ROLE_SUPPLIER')">
        <v-btn to="/supplier/products" text>Produits</v-btn>
        <v-btn to="/supplier/orders" text>Commandes</v-btn>
      </template>

      <!-- Navigation Livreur -->
      <template v-else-if="authStore.hasRole('ROLE_DELIVERY')">
        <v-btn to="/delivery/tasks" text>Mes Tâches</v-btn>
        <v-btn to="/delivery/earnings" text>Gains</v-btn>
      </template>

      <!-- Navigation Entrepôt -->
      <template v-else-if="authStore.hasRole('ROLE_WAREHOUSE')">
        <v-btn to="/warehouse/inventory" text>Inventaire</v-btn>
        <v-btn to="/warehouse/orders" text>Commandes</v-btn>
      </template>

      <!-- Navigation Administrateur -->
      <template v-else-if="authStore.hasRole('ROLE_ADMIN')">
        <v-btn to="/admin/users" text>Utilisateurs</v-btn>
        <v-btn to="/admin/analytics" text>Analytics</v-btn>
        <v-btn to="/admin/settings" text>Paramètres</v-btn>
      </template>

      <!-- Menu utilisateur -->
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props">
            <v-icon>mdi-account</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item>
            <v-list-item-title>{{ authStore.user?.fullName || 'Utilisateur' }}</v-list-item-title>
            <v-list-item-subtitle>{{ authStore.user?.role }}</v-list-item-subtitle>
          </v-list-item>
          <v-divider />
          <v-list-item @click="goToProfile">
            <v-list-item-title>Profil</v-list-item-title>
          </v-list-item>
          <v-list-item @click="logout">
            <v-list-item-title>Déconnexion</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>

    <!-- Navigation non connecté -->
    <template v-else>
      <v-btn to="/login" text>Connexion</v-btn>
      <v-btn to="/register" text>Inscription</v-btn>
    </template>
  </v-app-bar>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const toggleDrawer = () => {
  // Logique pour ouvrir/fermer le drawer
}

const goToProfile = () => {
  const role = authStore.user?.role
  if (role === 'ROLE_CUSTOMER') {
    router.push('/customer/profile')
  } else if (role === 'ROLE_SELLER') {
    router.push('/seller/profile')
  } else if (role === 'ROLE_SUPPLIER') {
    router.push('/supplier/profile')
  } else if (role === 'ROLE_DELIVERY') {
    router.push('/delivery/profile')
  } else if (role === 'ROLE_WAREHOUSE') {
    router.push('/warehouse/profile')
  } else if (role === 'ROLE_ADMIN') {
    router.push('/admin/settings')
  }
}

const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>
