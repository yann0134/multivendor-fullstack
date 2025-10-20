<template>
  <div class="auth-container">
    <!-- Background avec pattern agricole -->
    <div class="auth-background">
      <div class="auth-pattern"></div>
    </div>

    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center" class="pa-4">
        <v-col cols="12" sm="10" md="8" lg="6" xl="5">
          <!-- Carte principale -->
          <v-card class="auth-card agri-fade-in" elevation="24">
            <!-- En-tête avec logo -->
            <div class="auth-header">
              <div class="auth-logo">
                <v-icon size="48" color="primary">🌱</v-icon>
                <h1 class="auth-title">AgriMarket</h1>
                <p class="auth-subtitle">Rejoignez notre communauté agricole</p>
              </div>
            </div>

            <v-card-text class="pa-8">
              <v-form @submit.prevent="handleSubmit" class="auth-form">
                <!-- Sélection du rôle avec design moderne -->
                <div v-if="!otpSent" class="role-selection mb-6">
                  <h3 class="text-h6 mb-4 text-center">Choisissez votre rôle</h3>
                  <v-row>
                    <v-col 
                      v-for="role in roles" 
                      :key="role.value" 
                      cols="12" 
                      sm="6" 
                      md="4"
                      class="role-option"
                    >
                      <v-card
                        :class="['role-card', { 'role-card--selected': form.role === role.value }]"
                        @click="form.role = role.value"
                        elevation="2"
                        hover
                      >
                        <v-card-text class="text-center pa-4">
                          <v-icon :color="form.role === role.value ? 'primary' : 'grey'" size="32" class="mb-2">
                            {{ role.icon }}
                          </v-icon>
                          <div class="text-body-2 font-weight-medium">{{ role.label }}</div>
                          <div class="text-caption text-grey-600 mt-1">{{ role.description }}</div>
                        </v-card-text>
                      </v-card>
                    </v-col>
                  </v-row>
                </div>

                <!-- Informations personnelles -->
                <v-text-field
                  v-model="form.fullName"
                  label="Nom complet"
                  prepend-inner-icon="mdi-account"
                  :rules="nameRules"
                  :disabled="otpSent"
                  variant="outlined"
                  class="auth-input"
                  required
                />
                
                <v-text-field
                  v-model="form.email"
                  label="Adresse email"
                  prepend-inner-icon="mdi-email"
                  type="email"
                  :rules="emailRules"
                  :disabled="otpSent"
                  variant="outlined"
                  class="auth-input"
                  required
                />

                <!-- OTP Field avec design moderne -->
                <v-text-field
                  v-if="otpSent"
                  v-model="form.otp"
                  label="Code de vérification"
                  name="otp"
                  prepend-inner-icon="mdi-key"
                  type="text"
                  :rules="otpRules"
                  placeholder="Entrez le code à 6 chiffres"
                  variant="outlined"
                  class="auth-input"
                  required
                />

                <!-- Messages d'information avec design moderne -->
                <v-alert
                  v-if="otpSent"
                  type="info"
                  variant="tonal"
                  class="mb-4 agri-alert"
                  border="start"
                >
                  <template v-slot:prepend>
                    <v-icon>mdi-information</v-icon>
                  </template>
                  <div>
                    <strong>Code envoyé !</strong><br>
                    Un code de vérification a été envoyé à <strong>{{ form.email }}</strong>
                  </div>
                </v-alert>

                <!-- Bouton principal -->
                <v-btn
                  type="submit"
                  color="primary"
                  size="large"
                  class="auth-button mt-6"
                  block
                  :loading="loading"
                  :disabled="!form.email || !form.fullName || (!otpSent && !form.role)"
                >
                  <v-icon left>{{ otpSent ? 'mdi-account-plus' : 'mdi-send' }}</v-icon>
                  {{ otpSent ? 'Finaliser l\'inscription' : 'Envoyer le code' }}
                </v-btn>

                <!-- Bouton pour renvoyer l'OTP -->
                <v-btn
                  v-if="otpSent"
                  variant="text"
                  color="primary"
                  class="mt-3"
                  block
                  :loading="loading"
                  @click="resendOtp"
                >
                  <v-icon left>mdi-refresh</v-icon>
                  Renvoyer le code
                </v-btn>
              </v-form>
            </v-card-text>

            <!-- Footer avec lien de connexion -->
            <v-card-actions class="pa-6 pt-0">
              <v-divider class="mb-4"></v-divider>
              <div class="text-center w-100">
                <span class="text-body-2 text-grey-600 mr-2">Déjà un compte ?</span>
                <router-link to="/login" class="auth-link">
                  Se connecter
                </router-link>
              </div>
            </v-card-actions>
          </v-card>

          <!-- Informations supplémentaires -->
          <div class="auth-info mt-6 text-center">
            <p class="text-body-2 text-grey-600">
              🌱 Plateforme agricole connectée • 🔒 Sécurisé par OTP • 📱 Multi-rôles
            </p>
          </div>
        </v-col>
      </v-row>
    </v-container>
    
    <!-- Notification Snackbar -->
    <NotificationSnackbar
      v-model="notification.show"
      :message="notification.message"
      :type="notification.type"
      :timeout="notification.timeout"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'
import NotificationSnackbar from '@/components/common/NotificationSnackbar.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { notification, showSuccess, showError } = useNotification()

const form = ref({
  role: 'ROLE_CUSTOMER',
  fullName: '',
  email: '',
  otp: ''
})

const loading = computed(() => authStore.loading)
const otpSent = computed(() => authStore.otpSent)

// Configuration des rôles avec icônes et descriptions selon le workflow AgriMarket
const roles = [
  { 
    value: 'ROLE_CUSTOMER', 
    label: 'Client', 
    icon: 'mdi-account',
    description: 'Achetez des produits frais'
  },
  { 
    value: 'ROLE_SUPPLIER', 
    label: 'Fournisseur', 
    icon: 'mdi-sprout',
    description: 'Fournissez vos produits agricoles'
  },
  { 
    value: 'ROLE_ADMIN', 
    label: 'Administrateur', 
    icon: 'mdi-shield-check',
    description: 'Validez et gérez la plateforme'
  },
  { 
    value: 'ROLE_WAREHOUSE', 
    label: 'Entrepôt', 
    icon: 'mdi-warehouse',
    description: 'Gérez le stock central'
  },
  { 
    value: 'ROLE_DELIVERY', 
    label: 'Agent/Livreur', 
    icon: 'mdi-truck',
    description: 'Récupérez et livrez les produits'
  }
]

const nameRules = [
  v => !!v || 'Nom requis',
  v => v.length >= 2 || 'Minimum 2 caractères'
]

const emailRules = [
  v => !!v || 'Email requis',
  v => /.+@.+\..+/.test(v) || 'Email invalide'
]

const otpRules = [
  v => !!v || 'Code OTP requis',
  v => v.length === 6 || 'Le code OTP doit contenir 6 chiffres'
]

onMounted(() => {
  // Récupérer le rôle depuis l'URL si présent
  if (route.query.role) {
    const roleMap = {
      'customer': 'ROLE_CUSTOMER',
      'supplier': 'ROLE_SUPPLIER',
      'admin': 'ROLE_ADMIN',
      'warehouse': 'ROLE_WAREHOUSE',
      'delivery': 'ROLE_DELIVERY'
    }
    form.value.role = roleMap[route.query.role] || 'ROLE_CUSTOMER'
  }
})

const handleSubmit = async () => {
  try {
    if (!otpSent.value) {
      // Étape 1: Envoyer l'OTP
      await authStore.sendOtp(form.value.email, form.value.role)
      showSuccess('Code OTP envoyé avec succès !')
    } else {
      // Étape 2: Finaliser l'inscription avec l'OTP
      const userData = {
        fullName: form.value.fullName,
        email: form.value.email,
        otp: form.value.otp
      }

      const result = await authStore.signup(userData)
      
      showSuccess('Inscription réussie !')
      
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

<style scoped>
.auth-container {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.auth-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.auth-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(76, 175, 80, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(33, 150, 243, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(255, 193, 7, 0.1) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(1deg); }
}

.auth-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.auth-header {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 2rem;
  border-radius: 24px 24px 0 0;
  text-align: center;
}

.auth-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.auth-title {
  font-size: 2rem;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.auth-subtitle {
  font-size: 1rem;
  opacity: 0.9;
  margin: 0;
}

.role-selection {
  margin-bottom: 2rem;
}

.role-option {
  padding: 0.5rem;
}

.role-card {
  transition: all 0.3s ease;
  border-radius: 16px;
  cursor: pointer;
  border: 2px solid transparent;
  min-height: 120px;
  display: flex;
  align-items: center;
}

.role-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.role-card--selected {
  border-color: var(--v-theme-primary);
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1) 0%, rgba(76, 175, 80, 0.05) 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.2);
}

.auth-input {
  margin-bottom: 1rem;
}

.auth-input :deep(.v-field) {
  border-radius: 12px;
}

.auth-button {
  border-radius: 12px;
  font-weight: 600;
  text-transform: none;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
  transition: all 0.3s ease;
}

.auth-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.auth-link {
  color: var(--v-theme-primary);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.auth-link:hover {
  color: var(--v-theme-primary-dark);
  text-decoration: underline;
}

.auth-info {
  opacity: 0.8;
}

.agri-alert {
  border-radius: 12px;
  border-left: 4px solid var(--v-theme-primary);
}

/* Responsive */
@media (max-width: 600px) {
  .auth-card {
    margin: 1rem;
    border-radius: 16px;
  }
  
  .auth-header {
    padding: 1.5rem;
    border-radius: 16px 16px 0 0;
  }
  
  .auth-title {
    font-size: 1.5rem;
  }
  
  .role-card {
    margin-bottom: 0.5rem;
    min-height: 100px;
  }
  
  .role-card .text-caption {
    font-size: 0.7rem;
  }
}
</style>
