<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Paramètres de la Plateforme</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Configuration générale</v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="settings.platformName"
                label="Nom de la plateforme"
                prepend-icon="mdi-store"
              />
              
              <v-text-field
                v-model="settings.commissionRate"
                label="Taux de commission (%)"
                prepend-icon="mdi-percent"
                type="number"
              />
              
              <v-text-field
                v-model="settings.supportEmail"
                label="Email de support"
                prepend-icon="mdi-email"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="saveSettings">
              Sauvegarder
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Paramètres de paiement</v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="settings.stripeKey"
                label="Clé Stripe"
                prepend-icon="mdi-credit-card"
                type="password"
              />
              
              <v-text-field
                v-model="settings.razorpayKey"
                label="Clé Razorpay"
                prepend-icon="mdi-credit-card"
                type="password"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="saveSettings">
              Sauvegarder
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const settings = ref({
  platformName: 'Multivendor E-commerce',
  commissionRate: 5,
  supportEmail: 'support@multivendor.com',
  stripeKey: '',
  razorpayKey: ''
})

const saveSettings = async () => {
  try {
    console.log('Paramètres sauvegardés:', settings.value)
  } catch (error) {
    console.error('Erreur lors de la sauvegarde:', error)
  }
}

onMounted(() => {
  // Charger les paramètres de la plateforme
})
</script>
