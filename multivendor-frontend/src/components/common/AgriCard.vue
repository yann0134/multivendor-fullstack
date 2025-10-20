<template>
  <div 
    :class="[
      'agri-card',
      `agri-card--${variant}`,
      { 'agri-card--hover': hover },
      { 'agri-card--clickable': clickable }
    ]"
    @click="handleClick"
  >
    <!-- Header -->
    <div v-if="$slots.header || title || subtitle" class="agri-card-header">
      <slot name="header">
        <h3 v-if="title" class="agri-card-title">{{ title }}</h3>
        <p v-if="subtitle" class="agri-card-subtitle">{{ subtitle }}</p>
      </slot>
    </div>

    <!-- Content -->
    <div class="agri-card-content">
      <slot />
    </div>

    <!-- Actions -->
    <div v-if="$slots.actions" class="agri-card-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'AgriCard',
  props: {
    title: {
      type: String,
      default: ''
    },
    subtitle: {
      type: String,
      default: ''
    },
    variant: {
      type: String,
      default: 'default',
      validator: value => ['default', 'elevated', 'outlined', 'filled'].includes(value)
    },
    hover: {
      type: Boolean,
      default: true
    },
    clickable: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
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
.agri-card {
  position: relative;
  overflow: hidden;
}

.agri-card--elevated {
  box-shadow: var(--shadow-lg);
}

.agri-card--outlined {
  box-shadow: none;
  border: 2px solid var(--gray-200);
}

.agri-card--filled {
  background: var(--gray-50);
  border: 1px solid var(--gray-200);
}

.agri-card--clickable {
  cursor: pointer;
}

.agri-card--clickable:hover {
  transform: translateY(-2px);
}

.agri-card-content {
  flex: 1;
}

.agri-card-actions {
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--gray-200);
  display: flex;
  gap: var(--space-2);
  justify-content: flex-end;
}
</style>
