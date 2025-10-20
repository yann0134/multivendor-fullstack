<template>
  <div 
    :class="[
      'agri-metric',
      `agri-metric--${variant}`,
      { 'agri-metric--clickable': clickable }
    ]"
    @click="handleClick"
  >
    <!-- Icon -->
    <div 
      :class="[
        'agri-metric-icon',
        `agri-metric-icon--${iconColor}`
      ]"
    >
      <v-icon :size="iconSize" :color="iconColor">
        {{ icon }}
      </v-icon>
    </div>

    <!-- Value -->
    <div class="agri-metric-value">
      {{ formattedValue }}
    </div>

    <!-- Label -->
    <div class="agri-metric-label">
      {{ label }}
    </div>

    <!-- Trend -->
    <div v-if="trend" class="agri-metric-trend">
      <v-icon 
        :size="16" 
        :color="trendColor"
        class="agri-metric-trend-icon"
      >
        {{ trendIcon }}
      </v-icon>
      <span 
        :class="[
          'agri-metric-trend-text',
          `agri-metric-trend-text--${trendType}`
        ]"
      >
        {{ trendText }}
      </span>
    </div>

    <!-- Action -->
    <div v-if="$slots.action" class="agri-metric-action">
      <slot name="action" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'AgriMetric',
  props: {
    value: {
      type: [Number, String],
      required: true
    },
    label: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      required: true
    },
    iconColor: {
      type: String,
      default: 'primary'
    },
    iconSize: {
      type: [String, Number],
      default: 24
    },
    variant: {
      type: String,
      default: 'default',
      validator: value => ['default', 'success', 'warning', 'error', 'info'].includes(value)
    },
    trend: {
      type: Object,
      default: null
    },
    format: {
      type: String,
      default: 'number'
    },
    clickable: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  computed: {
    formattedValue() {
      if (this.format === 'currency') {
        return new Intl.NumberFormat('fr-FR', {
          style: 'currency',
          currency: 'XOF'
        }).format(this.value)
      }
      if (this.format === 'percentage') {
        return `${this.value}%`
      }
      return this.value
    },
    trendColor() {
      if (!this.trend) return 'gray'
      return this.trend.type === 'positive' ? 'success' : 
             this.trend.type === 'negative' ? 'error' : 'gray'
    },
    trendIcon() {
      if (!this.trend) return ''
      return this.trend.type === 'positive' ? 'mdi-trending-up' : 
             this.trend.type === 'negative' ? 'mdi-trending-down' : 'mdi-minus'
    },
    trendType() {
      return this.trend?.type || 'neutral'
    },
    trendText() {
      if (!this.trend) return ''
      return this.trend.text || `${this.trend.value}%`
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
.agri-metric--success .agri-metric-icon {
  background: rgba(76, 175, 80, 0.1);
  color: var(--success-color);
}

.agri-metric--warning .agri-metric-icon {
  background: rgba(255, 152, 0, 0.1);
  color: var(--warning-color);
}

.agri-metric--error .agri-metric-icon {
  background: rgba(244, 67, 54, 0.1);
  color: var(--error-color);
}

.agri-metric--info .agri-metric-icon {
  background: rgba(33, 150, 243, 0.1);
  color: var(--info-color);
}

.agri-metric-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-1);
  margin-top: var(--space-2);
  font-size: var(--text-xs);
}

.agri-metric-trend-text--positive {
  color: var(--success-color);
}

.agri-metric-trend-text--negative {
  color: var(--error-color);
}

.agri-metric-trend-text--neutral {
  color: var(--gray-500);
}

.agri-metric-action {
  margin-top: var(--space-3);
}
</style>
