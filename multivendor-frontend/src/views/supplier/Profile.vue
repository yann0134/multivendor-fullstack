<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Profil Fournisseur</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title>Informations de l'entreprise</v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="profile.supplierName"
                label="Nom de l'entreprise"
                prepend-icon="mdi-domain"
              />
              
              <v-text-field
                v-model="profile.email"
                label="Email"
                prepend-icon="mdi-email"
                readonly
              />
              
              <v-text-field
                v-model="profile.mobile"
                label="Téléphone"
                prepend-icon="mdi-phone"
              />
              
              <v-text-field
                v-model="profile.GSTIN"
                label="GSTIN"
                prepend-icon="mdi-certificate"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="updateProfile">
              Sauvegarder
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Statut du compte</v-card-title>
          <v-card-text>
            <v-chip :color="getStatusColor(profile.accountStatus)">
              {{ profile.accountStatus }}
            </v-chip>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const profile = ref({
  supplierName: '',
  email: '',
  mobile: '',
  GSTIN: '',
  accountStatus: 'PENDING_VERIFICATION'
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING_VERIFICATION': 'orange',
    'ACTIVE': 'green',
    'SUSPENDED': 'red'
  }
  return colors[status] || 'grey'
}

const updateProfile = async () => {
  try {
    console.log('Profil mis à jour:', profile.value)
  } catch (error) {
    console.error('Erreur lors de la mise à jour:', error)
  }
}

onMounted(() => {
  if (authStore.user) {
    profile.value = { ...authStore.user }
  }
})
</script>
