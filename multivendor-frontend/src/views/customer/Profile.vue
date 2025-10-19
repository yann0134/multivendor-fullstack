<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mon Profil</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title>Informations personnelles</v-card-title>
          <v-card-text>
            <v-form ref="profileForm">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profile.fullName"
                    label="Nom complet"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profile.email"
                    label="Email"
                    type="email"
                    :rules="emailRules"
                    required
                    readonly
                  />
                </v-col>
              </v-row>
              
              <v-text-field
                v-model="profile.mobile"
                label="Téléphone"
                :rules="phoneRules"
                required
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="updateProfile" :loading="loading">
              Sauvegarder
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Statistiques</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Commandes totales</span>
              <span>{{ stats.totalOrders }}</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
              <span>Montant total</span>
              <span>{{ stats.totalSpent.toFixed(2) }}€</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
              <span>Membre depuis</span>
              <span>{{ stats.memberSince }}</span>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Adresses -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>
            Mes adresses
            <v-spacer />
            <v-btn color="primary" @click="showAddAddress = true">
              Ajouter une adresse
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="address in addresses"
                :key="address.id"
              >
                <v-list-item-title>{{ address.firstName }} {{ address.lastName }}</v-list-item-title>
                <v-list-item-subtitle>
                  {{ address.street }}, {{ address.city }} {{ address.zipCode }}
                </v-list-item-subtitle>
                <template v-slot:append>
                  <v-btn icon @click="editAddress(address)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn icon @click="deleteAddress(address.id)">
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog pour ajouter/modifier une adresse -->
    <v-dialog v-model="showAddAddress" max-width="600">
      <v-card>
        <v-card-title>
          {{ editingAddress ? 'Modifier l\'adresse' : 'Ajouter une adresse' }}
        </v-card-title>
        <v-card-text>
          <v-form ref="addressForm">
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.firstName"
                  label="Prénom"
                  :rules="nameRules"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.lastName"
                  label="Nom"
                  :rules="nameRules"
                  required
                />
              </v-col>
            </v-row>
            
            <v-text-field
              v-model="newAddress.street"
              label="Adresse"
              :rules="streetRules"
              required
            />
            
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.city"
                  label="Ville"
                  :rules="cityRules"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.zipCode"
                  label="Code postal"
                  :rules="zipRules"
                  required
                />
              </v-col>
            </v-row>
            
            <v-text-field
              v-model="newAddress.mobile"
              label="Téléphone"
              :rules="phoneRules"
              required
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn variant="outlined" @click="showAddAddress = false">
            Annuler
          </v-btn>
          <v-spacer />
          <v-btn color="primary" @click="saveAddress" :loading="addressLoading">
            {{ editingAddress ? 'Modifier' : 'Ajouter' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const profile = ref({
  fullName: '',
  email: '',
  mobile: ''
})

const loading = ref(false)
const addresses = ref([])
const showAddAddress = ref(false)
const editingAddress = ref(null)
const addressLoading = ref(false)

const newAddress = ref({
  firstName: '',
  lastName: '',
  street: '',
  city: '',
  zipCode: '',
  mobile: ''
})

const stats = ref({
  totalOrders: 0,
  totalSpent: 0,
  memberSince: ''
})

const nameRules = [
  v => !!v || 'Champ requis',
  v => v.length >= 2 || 'Minimum 2 caractères'
]

const emailRules = [
  v => !!v || 'Email requis',
  v => /.+@.+\..+/.test(v) || 'Email invalide'
]

const phoneRules = [
  v => !!v || 'Téléphone requis',
  v => /^[0-9+\-\s()]+$/.test(v) || 'Format de téléphone invalide'
]

const streetRules = [
  v => !!v || 'Adresse requise'
]

const cityRules = [
  v => !!v || 'Ville requise'
]

const zipRules = [
  v => !!v || 'Code postal requis',
  v => /^\d{5}$/.test(v) || 'Code postal invalide'
]

const updateProfile = async () => {
  loading.value = true
  try {
    // Mettre à jour le profil
    // await userService.updateProfile(profile.value)
    console.log('Profil mis à jour')
  } catch (error) {
    console.error('Erreur lors de la mise à jour du profil:', error)
  } finally {
    loading.value = false
  }
}

const editAddress = (address) => {
  editingAddress.value = address
  newAddress.value = { ...address }
  showAddAddress.value = true
}

const deleteAddress = async (addressId) => {
  try {
    // await addressService.deleteAddress(addressId)
    addresses.value = addresses.value.filter(addr => addr.id !== addressId)
  } catch (error) {
    console.error('Erreur lors de la suppression de l\'adresse:', error)
  }
}

const saveAddress = async () => {
  addressLoading.value = true
  try {
    if (editingAddress.value) {
      // Modifier l'adresse existante
      const index = addresses.value.findIndex(addr => addr.id === editingAddress.value.id)
      if (index !== -1) {
        addresses.value[index] = { ...newAddress.value }
      }
    } else {
      // Ajouter une nouvelle adresse
      addresses.value.push({ ...newAddress.value, id: Date.now() })
    }
    
    showAddAddress.value = false
    editingAddress.value = null
    newAddress.value = {
      firstName: '',
      lastName: '',
      street: '',
      city: '',
      zipCode: '',
      mobile: ''
    }
  } catch (error) {
    console.error('Erreur lors de la sauvegarde de l\'adresse:', error)
  } finally {
    addressLoading.value = false
  }
}

onMounted(() => {
  // Charger les données du profil
  if (authStore.user) {
    profile.value = { ...authStore.user }
  }
  
  // Charger les adresses
  // addresses.value = await addressService.getAddresses()
  
  // Calculer les statistiques
  stats.value.memberSince = new Date().getFullYear()
})
</script>
