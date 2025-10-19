<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Profil Entrepôt</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title>Informations de l'entrepôt</v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="profile.warehouseName"
                label="Nom de l'entrepôt"
                prepend-icon="mdi-warehouse"
              />
              
              <v-text-field
                v-model="profile.capacity"
                label="Capacité (m³)"
                prepend-icon="mdi-cube-outline"
                type="number"
              />
              
              <v-text-field
                v-model="profile.currentStock"
                label="Stock actuel (m³)"
                prepend-icon="mdi-package-variant"
                type="number"
                readonly
              />
              
              <v-textarea
                v-model="profile.description"
                label="Description"
                prepend-icon="mdi-text"
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
          <v-card-title>Statut de l'entrepôt</v-card-title>
          <v-card-text>
            <v-chip :color="getStatusColor(profile.status)">
              {{ profile.status }}
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
  warehouseName: '',
  capacity: 0,
  currentStock: 0,
  description: '',
  status: 'ACTIVE'
})

const getStatusColor = (status) => {
  const colors = {
    'ACTIVE': 'green',
    'MAINTENANCE': 'orange',
    'CLOSED': 'red'
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
