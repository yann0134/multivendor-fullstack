<template>
  <div 
    :class="[
      'agri-status',
      `agri-status--${type}`,
      `agri-status--${size}`,
      { 'agri-status--clickable': clickable }
    ]"
    @click="handleClick"
  >
    <v-icon 
      v-if="icon" 
      :size="iconSize" 
      class="agri-status-icon"
    >
      {{ icon }}
    </v-icon>
    <span class="agri-status-text">{{ text }}</span>
  </div>
</template>

<script>
export default {
  name: 'AgriStatus',
  props: {
    type: {
      type: String,
      required: true,
      validator: value => [
        'pending', 'processing', 'completed', 'rejected', 
        'warning', 'success', 'error', 'info', 'neutral'
      ].includes(value)
    },
    text: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'medium',
      validator: value => ['small', 'medium', 'large'].includes(value)
    },
    clickable: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  computed: {
    iconSize() {
      const sizes = {
        small: 12,
        medium: 14,
        large: 16
      }
      return sizes[this.size]
    }
  },
  methods: {
    handleClick() {
      if (this.clickable) {
        this.$emit('click')
      }
    }
  }
}
</script>

<style scoped>
.agri-status {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  border-radius: var(--radius-full);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  transition: all var(--transition-fast);
}

.agri-status--small {
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
}

.agri-status--medium {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
}

.agri-status--large {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
}

.agri-status--clickable {
  cursor: pointer;
}

.agri-status--clickable:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-sm);
}

/* Type styles */
.agri-status--pending {
  background: rgba(255, 152, 0, 0.1);
  color: var(--status-pending);
  border: 1px solid rgba(255, 152, 0, 0.2);
}

.agri-status--processing {
  background: rgba(33, 150, 243, 0.1);
  color: var(--status-processing);
  border: 1px solid rgba(33, 150, 243, 0.2);
}

.agri-status--completed {
  background: rgba(76, 175, 80, 0.1);
  color: var(--status-completed);
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.agri-status--rejected {
  background: rgba(244, 67, 54, 0.1);
  color: var(--status-rejected);
  border: 1px solid rgba(244, 67, 54, 0.2);
}

.agri-status--warning {
  background: rgba(255, 152, 0, 0.1);
  color: var(--warning-color);
  border: 1px solid rgba(255, 152, 0, 0.2);
}

.agri-status--success {
  background: rgba(76, 175, 80, 0.1);
  color: var(--success-color);
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.agri-status--error {
  background: rgba(244, 67, 54, 0.1);
  color: var(--error-color);
  border: 1px solid rgba(244, 67, 54, 0.2);
}

.agri-status--info {
  background: rgba(33, 150, 243, 0.1);
  color: var(--info-color);
  border: 1px solid rgba(33, 150, 243, 0.2);
}

.agri-status--neutral {
  background: rgba(158, 158, 158, 0.1);
  color: var(--gray-600);
  border: 1px solid rgba(158, 158, 158, 0.2);
}

.agri-status-icon {
  flex-shrink: 0;
}

.agri-status-text {
  white-space: nowrap;
}
</style>
