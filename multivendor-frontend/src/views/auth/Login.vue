<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="elevation-12">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Connexion</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <v-form @submit.prevent="handleSubmit">
              <!-- Sélection du rôle -->
              <v-row v-if="!otpSent">
                <v-col cols="12">
                  <h3 class="text-h6 mb-4">Choisissez votre rôle</h3>
                  <v-radio-group v-model="form.role" inline>
                    <v-radio label="Client" value="ROLE_CUSTOMER" />
                    <v-radio label="Vendeur" value="ROLE_SELLER" />
                    <v-radio label="Fournisseur" value="ROLE_SUPPLIER" />
                    <v-radio label="Livreur" value="ROLE_DELIVERY" />
                  </v-radio-group>
                </v-col>
              </v-row>

              <v-divider v-if="!otpSent" class="my-4" />

              <!-- Email -->
              <v-text-field
                v-model="form.email"
                label="Email"
                name="email"
                prepend-icon="mdi-email"
                type="email"
                :rules="emailRules"
                :disabled="otpSent"
                required
              />

              <!-- OTP Field (affiché après envoi) -->
              <v-text-field
                v-if="otpSent"
                v-model="form.otp"
                label="Code OTP"
                name="otp"
                prepend-icon="mdi-key"
                type="text"
                :rules="otpRules"
                placeholder="Entrez le code reçu par email"
                required
              />

              <!-- Messages d'information -->
              <v-alert
                v-if="otpSent"
                type="info"
                variant="tonal"
                class="mb-4"
              >
                Un code OTP a été envoyé à {{ form.email }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                class="mt-4"
                block
                :loading="loading"
              >
                {{ otpSent ? 'Se connecter' : 'Envoyer le code OTP' }}
              </v-btn>

              <!-- Bouton pour renvoyer l'OTP -->
              <v-btn
                v-if="otpSent"
                variant="text"
                color="secondary"
                class="mt-2"
                block
                :loading="loading"
                @click="resendOtp"
              >
                Renvoyer le code
              </v-btn>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <router-link to="/register">
              Pas encore de compte ? S'inscrire
            </router-link>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    
    <!-- Notification Snackbar -->
    <NotificationSnackbar
      v-model="notification.show"
      :message="notification.message"
      :type="notification.type"
      :timeout="notification.timeout"
    />
  </v-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'
import NotificationSnackbar from '@/components/common/NotificationSnackbar.vue'

const router = useRouter()
const authStore = useAuthStore()
const { notification, showSuccess, showError } = useNotification()

const form = ref({
  email: '',
  role: 'ROLE_CUSTOMER',
  otp: ''
})

const loading = computed(() => authStore.loading)
const otpSent = computed(() => authStore.otpSent)

const emailRules = [
  v => !!v || 'Email requis',
  v => /.+@.+\..+/.test(v) || 'Email invalide'
]

const otpRules = [
  v => !!v || 'Code OTP requis',
  v => v.length === 6 || 'Le code OTP doit contenir 6 chiffres'
]

const handleSubmit = async () => {
  try {
    if (!otpSent.value) {
      // Étape 1: Envoyer l'OTP
      await authStore.sendOtp(form.value.email, form.value.role)
      showSuccess('Code OTP envoyé avec succès !')
    } else {
      // Étape 2: Se connecter avec l'OTP
      const result = await authStore.login({
        email: form.value.email,
        role: form.value.role,
        otp: form.value.otp
      })
      
      showSuccess('Connexion réussie !')
      
      // Redirection selon le rôle
      const dashboardRoute = authStore.getDashboardRoute(result.role)
      router.push(dashboardRoute)
    }
  } catch (error) {
    console.error('Erreur:', error)
    showError(error.message || 'Une erreur est survenue')
  }
}

const resendOtp = async () => {
  try {
    await authStore.sendOtp(form.value.email, form.value.role)
    showSuccess('Code OTP renvoyé avec succès !')
  } catch (error) {
    console.error('Erreur lors du renvoi:', error)
    showError(error.message || 'Erreur lors du renvoi du code')
  }
}
</script>
