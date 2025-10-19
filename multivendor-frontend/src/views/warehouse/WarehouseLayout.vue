<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      permanent
      location="left"
    >
      <v-list>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Tableau de bord" to="/warehouse" />
        <v-list-item prepend-icon="mdi-package-variant" title="Inventaire" to="/warehouse/inventory" />
        <v-list-item prepend-icon="mdi-clipboard-list" title="Commandes" to="/warehouse/orders" />
        <v-list-item prepend-icon="mdi-account" title="Profil" to="/warehouse/profile" />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="purple" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>Espace Entrepôt</v-toolbar-title>
      <v-spacer />
      <v-btn icon>
        <v-icon>mdi-bell</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'

const drawer = ref(true)
</script>
