<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      permanent
      location="left"
    >
      <v-list>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Tableau de bord" to="/admin" />
        <v-list-item prepend-icon="mdi-account-group" title="Utilisateurs" to="/admin/users" />
        <v-list-item prepend-icon="mdi-chart-line" title="Analytics" to="/admin/analytics" />
        <v-list-item prepend-icon="mdi-cog" title="Paramètres" to="/admin/settings" />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="red" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>Espace Administrateur</v-toolbar-title>
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
