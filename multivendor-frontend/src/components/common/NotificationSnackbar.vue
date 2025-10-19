<template>
  <v-snackbar
    v-model="show"
    :color="color"
    :timeout="timeout"
    location="top right"
  >
    <v-icon v-if="icon" class="mr-2">{{ icon }}</v-icon>
    {{ message }}
    
    <template v-slot:actions>
      <v-btn
        color="white"
        variant="text"
        @click="show = false"
      >
        Fermer
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  timeout: {
    type: Number,
    default: 5000
  }
})

const emit = defineEmits(['update:modelValue'])

const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const color = computed(() => {
  const colors = {
    success: 'success',
    error: 'error',
    warning: 'warning',
    info: 'info'
  }
  return colors[props.type] || 'info'
})

const icon = computed(() => {
  const icons = {
    success: 'mdi-check-circle',
    error: 'mdi-alert-circle',
    warning: 'mdi-alert',
    info: 'mdi-information'
  }
  return icons[props.type] || 'mdi-information'
})
</script>
