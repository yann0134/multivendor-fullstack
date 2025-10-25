<template>
  <v-app>
    <AppBar v-if="showAppBar" />
    <v-main>
      <router-view />
    </v-main>
    <AppFooter v-if="showFooter" />
  </v-app>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import AppBar from '@/components/common/AppBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()

// Initialiser le panier au démarrage si l'utilisateur est connecté
onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      await cartStore.initializeCart()
      console.log('🛒 Panier initialisé au démarrage de l\'application')
    } catch (error) {
      console.warn('⚠️ Erreur lors de l\'initialisation du panier au démarrage:', error)
    }
  }
})

const showAppBar = computed(() => {
  const authPages = ['/login', '/register']
  const rolePrefixes = ['/customer', '/seller', '/supplier', '/delivery', '/warehouse', '/admin']

  // Masquer la barre pour pages auth
  if (authPages.includes(route.path)) return false
  // Masquer la barre pour tous les espaces rôle
  if (rolePrefixes.some(p => route.path.startsWith(p))) return false
  // Masquer si connecté
  if (authStore.isAuthenticated.value) return false
  // Sinon afficher (pages publiques non-auth)
  return true
})

const showFooter = computed(() => {
  const hiddenRoutes = ['/login', '/register']
  return !hiddenRoutes.includes(route.path)
})
</script>

<style>
/* Styles globaux */
.v-application {
  font-family: 'Roboto', sans-serif;
}
</style>
