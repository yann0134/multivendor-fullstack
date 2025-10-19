<template>
  <v-footer class="bg-grey-lighten-1">
    <v-row justify="center" no-gutters>
      <v-col cols="12" md="4">
        <v-card-text class="text-center">
          <strong>Multivendor E-commerce</strong>
          <p>Plateforme e-commerce multi-vendeurs</p>
        </v-card-text>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card-text class="text-center">
          <h4>Liens utiles</h4>
          <v-list density="compact">
            <v-list-item to="/customer/products" title="Produits" />
            <v-list-item to="/seller" title="Devenir vendeur" />
            <v-list-item to="/supplier" title="Devenir fournisseur" />
          </v-list>
        </v-card-text>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card-text class="text-center">
          <h4>Support</h4>
          <v-list density="compact">
            <v-list-item title="Aide" />
            <v-list-item title="Contact" />
            <v-list-item title="CGV" />
          </v-list>
        </v-card-text>
      </v-col>
      
      <v-col cols="12">
        <v-divider />
        <v-card-text class="text-center">
          {{ new Date().getFullYear() }} — <strong>Multivendor E-commerce</strong>
        </v-card-text>
      </v-col>
    </v-row>
  </v-footer>
</template>

<script setup>
// Footer simple avec liens utiles
</script>
