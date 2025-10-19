<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      permanent
      location="left"
    >
      <v-list>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Tableau de bord" to="/delivery" />
        <v-list-item prepend-icon="mdi-truck" title="Mes Tâches" to="/delivery/tasks" />
        <v-list-item prepend-icon="mdi-currency-eur" title="Gains" to="/delivery/earnings" />
        <v-list-item prepend-icon="mdi-account" title="Profil" to="/delivery/profile" />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="info" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>Espace Livreur</v-toolbar-title>
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
