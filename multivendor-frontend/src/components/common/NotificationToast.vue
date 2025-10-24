<template>
  <v-snackbar
    v-model="show"
    :color="color"
    :timeout="timeout"
    location="top right"
  >
    <div class="d-flex align-center">
      <v-icon class="mr-3">{{ icon }}</v-icon>
      <span>{{ message }}</span>
    </div>
    
    <template v-slot:actions>
      <v-btn
        icon="mdi-close"
        variant="text"
        @click="show = false"
      />
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
    default: 'info', // success, error, warning, info
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  timeout: {
    type: Number,
    default: 4000
  }
})

const emit = defineEmits(['update:modelValue'])

const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const color = computed(() => {
  switch (props.type) {
    case 'success': return 'success'
    case 'error': return 'error'
    case 'warning': return 'warning'
    case 'info': return 'info'
    default: return 'info'
  }
})

const icon = computed(() => {
  switch (props.type) {
    case 'success': return 'mdi-check-circle'
    case 'error': return 'mdi-alert-circle'
    case 'warning': return 'mdi-alert'
    case 'info': return 'mdi-information'
    default: return 'mdi-information'
  }
})
</script>
