<template>
  <v-container fluid>
    <!-- En-tête -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-account-edit</v-icon>
            <span>👤 Mon Profil Fournisseur</span>
          </v-card-title>
          <v-card-subtitle>
            Mettez à jour vos informations personnelles et professionnelles
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Formulaire de mise à jour du profil -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>📝 Informations du Profil</v-card-title>
          <v-card-text>
            <v-form ref="profileForm" v-model="valid">
              <!-- Informations de base -->
              <v-row>
                <v-col cols="12">
                  <h3 class="text-h6 mb-4">Informations Personnelles</h3>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profileData.supplierName"
                    label="Nom du Fournisseur"
                    :rules="[rules.required]"
                    outlined
                    prepend-icon="mdi-account"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="6">
              <v-text-field
                    v-model="profileData.mobile"
                    label="Téléphone Mobile"
                    :rules="[rules.required, rules.phone]"
                    outlined
                    prepend-icon="mdi-phone"
                  ></v-text-field>
                </v-col>
              </v-row>

              <!-- Adresse de ramassage -->
              <v-row>
                <v-col cols="12">
                  <h3 class="text-h6 mb-4">📍 Adresse de Ramassage</h3>
                </v-col>
                
                <v-col cols="12">
              <v-text-field
                    v-model="profileData.pickupAddress.address"
                    label="Adresse Complète"
                    :rules="[rules.required]"
                    outlined
                    prepend-icon="mdi-map-marker"
                    placeholder="Ex: Douala, Cameroun"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="4">
              <v-text-field
                    v-model="profileData.pickupAddress.city"
                    label="Ville"
                    :rules="[rules.required]"
                    outlined
                    prepend-icon="mdi-city"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="4">
              <v-text-field
                    v-model="profileData.pickupAddress.state"
                    label="Région/État"
                    outlined
                    prepend-icon="mdi-map"
                  ></v-text-field>
      </v-col>
      
      <v-col cols="12" md="4">
                  <v-text-field
                    v-model="profileData.pickupAddress.pinCode"
                    label="Code Postal"
                    outlined
                    prepend-icon="mdi-postal"
                  ></v-text-field>
                </v-col>
              </v-row>

              <!-- Détails de l'entreprise -->
              <v-row>
                <v-col cols="12">
                  <h3 class="text-h6 mb-4">🏢 Informations de l'Entreprise</h3>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profileData.businessDetails.businessName"
                    label="Nom de l'Entreprise"
                    outlined
                    prepend-icon="mdi-domain"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profileData.businessDetails.businessEmail"
                    label="Email de l'Entreprise"
                    :rules="[rules.email]"
                    outlined
                    prepend-icon="mdi-email"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12">
                  <v-text-field
                    v-model="profileData.businessDetails.businessAddress"
                    label="Adresse de l'Entreprise"
                    outlined
                    prepend-icon="mdi-office-building"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profileData.businessDetails.businessMobile"
                    label="Téléphone de l'Entreprise"
                    outlined
                    prepend-icon="mdi-phone-business"
                  ></v-text-field>
                </v-col>
              </v-row>

              <!-- Boutons d'action -->
              <v-row class="mt-6">
                <v-col cols="12" class="d-flex justify-end">
                  <v-btn
                    color="primary"
                    large
                    :loading="loading"
                    :disabled="!valid"
                    @click="updateProfile"
                  >
                    <v-icon left>mdi-content-save</v-icon>
                    Sauvegarder le Profil
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Message de succès -->
    <v-snackbar
      v-model="showSuccess"
      color="success"
      timeout="3000"
    >
      <v-icon left>mdi-check-circle</v-icon>
      Profil mis à jour avec succès !
    </v-snackbar>

    <!-- Message d'erreur -->
    <v-snackbar
      v-model="showError"
      color="error"
      timeout="5000"
    >
      <v-icon left>mdi-alert-circle</v-icon>
      {{ errorMessage }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

// Variables réactives
const valid = ref(false)
const loading = ref(false)
const showSuccess = ref(false)
const showError = ref(false)
const errorMessage = ref('')

// Données du profil
const profileData = ref({
  supplierName: '',
  mobile: '',
  pickupAddress: {
    address: '',
    city: '',
    state: '',
    pinCode: ''
  },
  businessDetails: {
    businessName: '',
    businessAddress: '',
    businessEmail: '',
    businessMobile: ''
  }
})

// Règles de validation
const rules = {
  required: (value) => !!value || 'Ce champ est requis',
  email: (value) => {
    if (!value) return true
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || 'Email invalide'
  },
  phone: (value) => {
    if (!value) return true
    const pattern = /^[\+]?[0-9\s\-\(\)]{8,}$/
    return pattern.test(value) || 'Numéro de téléphone invalide'
  }
}

// Fonctions
const loadProfile = async () => {
  try {
    console.log('👤 Chargement du profil fournisseur...')
    
    // Ici vous pouvez ajouter un endpoint pour récupérer le profil
    // const response = await api.get('/api/supplier/profile')
    // profileData.value = response.data
    
    console.log('✅ Profil chargé')
  } catch (error) {
    console.error('❌ Erreur lors du chargement du profil:', error)
  }
}

const updateProfile = async () => {
  if (!valid.value) return
  
  loading.value = true
  
  try {
    console.log('💾 Mise à jour du profil fournisseur...')
    console.log('📝 Données à envoyer:', profileData.value)
    
    const response = await api.put('/api/products/supplier/update-profile', profileData.value)
    
    if (response.status === 200) {
      console.log('✅ Profil mis à jour avec succès:', response.data)
      showSuccess.value = true
      
      // Optionnel: recharger les données
      await loadProfile()
    }
  } catch (error) {
    console.error('❌ Erreur lors de la mise à jour du profil:', error)
    errorMessage.value = error.response?.data?.message || 'Erreur lors de la mise à jour du profil'
    showError.value = true
  } finally {
    loading.value = false
  }
}

// Cycle de vie
onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.v-card {
  border-radius: 12px;
}

.v-text-field {
  margin-bottom: 8px;
}
</style>