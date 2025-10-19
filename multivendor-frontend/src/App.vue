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
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppBar from '@/components/common/AppBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const route = useRoute()
const authStore = useAuthStore()

const showAppBar = computed(() => {
  const hiddenRoutes = ['/login', '/register']
  return !hiddenRoutes.includes(route.path)
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
