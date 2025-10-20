<template>
  <div class="agri-nav-container">
    <!-- Top App Bar -->
    <v-app-bar :color="appBarColor" density="comfortable" flat class="agri-nav agri-fade-in">
      <v-app-bar-nav-icon @click="$emit('toggle-drawer')" />
      <v-toolbar-title class="d-flex align-center">
        <slot name="logo">
          <v-icon class="mr-2" color="primary">mdi-sprout</v-icon>
          <span class="font-weight-bold">AgriMarket</span>
        </slot>
      </v-toolbar-title>
      <v-spacer />
      <slot name="actions">
        <v-btn icon><v-icon>mdi-bell-outline</v-icon></v-btn>
        <v-btn icon><v-icon>mdi-account-circle</v-icon></v-btn>
      </slot>
    </v-app-bar>

    <!-- Navigation Drawer -->
    <v-navigation-drawer
      v-model="drawer"
      :location="drawerLocation"
      color="white"
      width="260"
      class="agri-slide-in"
    >
      <v-list density="comfortable">
        <v-list-item
          v-for="(item, index) in items"
          :key="index"
          :to="item.to"
          :title="item.title"
          :prepend-icon="item.icon"
          class="agri-nav-item"
        />
      </v-list>
    </v-navigation-drawer>
  </div>
</template>

<script>
export default {
  name: 'AgriNavigation',
  props: {
    items: {
      type: Array,
      default: () => []
    },
    appBarColor: {
      type: String,
      default: 'white'
    },
    drawerLocation: {
      type: String,
      default: 'left'
    },
    modelValue: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:modelValue', 'toggle-drawer'],
  computed: {
    drawer: {
      get() { return this.modelValue },
      set(v) { this.$emit('update:modelValue', v) }
    }
  }
}
</script>

<style scoped>
.agri-nav-container {
  position: relative;
}
</style>
