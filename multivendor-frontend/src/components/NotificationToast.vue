<template>
  <v-snackbar
    v-model="notification.show"
    :color="getNotificationColor(notification.type)"
    :timeout="notification.timeout"
    location="top right"
    elevation="8"
    rounded
  >
    <div class="d-flex align-center">
      <v-icon 
        :icon="getNotificationIcon(notification.type)" 
        size="24"
        class="mr-3"
      />
      <span>{{ notification.message }}</span>
    </div>
    
    <template v-slot:actions>
      <v-btn
        icon="mdi-close"
        variant="text"
        size="small"
        @click="hideNotification"
      />
    </template>
  </v-snackbar>
</template>

<script setup>
import { useNotification } from '@/composables/useNotification'

const { notification, hideNotification } = useNotification()

const getNotificationColor = (type) => {
  switch (type) {
    case 'success': return 'success'
    case 'error': return 'error'
    case 'warning': return 'warning'
    case 'info': return 'info'
    default: return 'info'
  }
}

const getNotificationIcon = (type) => {
  switch (type) {
    case 'success': return 'mdi-check-circle'
    case 'error': return 'mdi-alert-circle'
    case 'warning': return 'mdi-alert'
    case 'info': return 'mdi-information'
    default: return 'mdi-information'
  }
}
</script>

<style scoped>
.v-snackbar {
  z-index: 9999;
}
</style>
