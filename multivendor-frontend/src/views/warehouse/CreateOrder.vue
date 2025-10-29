<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-plus-circle</v-icon>
            Créer une Commande Entrepôt
          </v-card-title>
          
          <v-card-text>
            <v-form @submit.prevent="createOrder">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="form.userId"
                    label="ID Utilisateur"
                    type="number"
                    :rules="[v => !!v || 'ID utilisateur requis']"
                    required
                  />
                </v-col>
                
                <v-col cols="12">
                  <v-text-field
                    v-model="form.shippingAddress.name"
                    label="Nom Complet"
                    :rules="[v => !!v || 'Nom requis']"
                    readonly
                    disabled
                    hint="Nom de l'utilisateur connecté"
                    persistent-hint
                  />
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="form.shippingAddress.mobile"
                    label="Téléphone"
                    :rules="[v => !!v || 'Téléphone requis']"
                    required
                  />
                </v-col>
                
                <v-col cols="12">
                  <v-text-field
                    v-model="form.shippingAddress.address"
                    label="Adresse"
                    :rules="[v => !!v || 'Adresse requise']"
                    required
                  />
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="form.shippingAddress.city"
                    label="Ville"
                    :rules="[v => !!v || 'Ville requise']"
                    required
                  />
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="form.shippingAddress.pinCode"
                    label="Code Postal"
                    :rules="[v => !!v || 'Code postal requis']"
                    required
                  />
                </v-col>
              </v-row>
              
              <v-row>
                <v-col cols="12">
                  <v-btn
                    type="submit"
                    color="primary"
                    :loading="loading"
                    :disabled="loading"
                    block
                  >
                    <v-icon left>mdi-check</v-icon>
                    Créer la Commande
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/orders'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'

const router = useRouter()
const orderStore = useOrderStore()
const authStore = useAuthStore()
const { showNotification } = useNotification()

const loading = ref(false)

const form = ref({
  userId: 1,
  shippingAddress: {
    name: '',
    mobile: '',
    address: '',
    city: '',
    pinCode: ''
  }
})

// Récupérer le nom et l'ID de l'utilisateur connecté
onMounted(() => {
  if (authStore.user?.fullName) {
    form.value.shippingAddress.name = authStore.user.fullName
  }
  if (authStore.user?.id) {
    form.value.userId = authStore.user.id
  }
})

const createOrder = async () => {
  loading.value = true
  
  try {
    const response = await orderStore.createWarehouseOrder(
      form.value.userId,
      form.value.shippingAddress
    )
    
    showNotification('Commande créée avec succès !', 'success')
    
    // Rediriger vers la liste des commandes
    router.push('/warehouse/orders')
    
  } catch (error) {
    console.error('Erreur lors de la création de la commande:', error)
    showNotification('Erreur lors de la création de la commande', 'error')
  } finally {
    loading.value = false
  }
}
</script>
