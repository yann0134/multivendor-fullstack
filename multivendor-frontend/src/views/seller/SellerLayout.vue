<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      permanent
      location="left"
    >
      <v-list>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Tableau de bord" to="/seller" />
        <v-list-item prepend-icon="mdi-package-variant" title="Mes Produits" to="/seller/products" />
        <v-list-item prepend-icon="mdi-clipboard-list" title="Commandes" to="/seller/orders" />
        <v-list-item prepend-icon="mdi-chart-line" title="Rapports" to="/seller/reports" />
        <v-list-item prepend-icon="mdi-account" title="Profil" to="/seller/profile" />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="success" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>Espace Vendeur</v-toolbar-title>
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
