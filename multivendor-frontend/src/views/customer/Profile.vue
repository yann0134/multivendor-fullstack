<template>
  <v-container fluid>
    <!-- En-tête -->
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-3" color="primary">mdi-account-edit</v-icon>
            <span>👤 Mon Profil Client</span>
          </v-card-title>
          <v-card-subtitle>
            Mettez à jour vos informations personnelles et vos adresses
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <!-- Formulaire de mise à jour du profil -->
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
                    v-model="userProfile.fullName"
                    label="Nom complet"
                    :rules="[rules.required]"
                    outlined
                    prepend-icon="mdi-account"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="userProfile.mobile"
                    label="Téléphone Mobile"
                    :rules="[rules.required, rules.phone]"
                    outlined
                    prepend-icon="mdi-phone"
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12">
                  <v-text-field
                    v-model="userProfile.email"
                    label="Email"
                    type="email"
                    outlined
                    prepend-icon="mdi-email"
                    disabled
                  ></v-text-field>
                </v-col>
                
                <v-col cols="12">
                  <v-textarea
                    v-model="userProfile.bio"
                    label="Biographie"
                    outlined
                    rows="3"
                    prepend-icon="mdi-text"
                  ></v-textarea>
                </v-col>
              </v-row>

              <!-- Boutons d'action -->
              <v-row class="mt-6">
                <v-col cols="12" class="d-flex justify-end">
                  <v-btn
                    color="primary"
                    large
                    :loading="updating"
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

    <!-- Gestion des adresses -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>📍 Mes Adresses</v-card-title>
          <v-card-text>
            <div v-if="addresses.length === 0" class="text-center py-8">
              <v-icon size="64" color="grey">mdi-map-marker-outline</v-icon>
              <h3 class="text-h6 text-grey mt-4">Aucune adresse enregistrée</h3>
              <p class="text-grey">Ajoutez votre première adresse pour faciliter vos commandes</p>
            </div>
            
            <v-list v-else>
              <v-list-item
                v-for="address in addresses"
                :key="address.id"
                class="address-item mb-4"
              >
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-map-marker</v-icon>
                </template>
                
                <v-list-item-title class="text-h6">{{ address.firstName }} {{ address.lastName }}</v-list-item-title>
                <v-list-item-subtitle class="text-body-1">
                  {{ address.street }}, {{ address.city }} {{ address.zipCode }}
                </v-list-item-subtitle>
                <v-list-item-subtitle class="text-caption text-grey">
                  Téléphone: {{ address.mobile }}
                </v-list-item-subtitle>
                
                <template v-slot:append>
                  <v-btn
                    icon
                    size="small"
                    color="primary"
                    @click="editAddress(address)"
                  >
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn
                    icon
                    size="small"
                    color="error"
                    @click="deleteAddress(address)"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
          <v-card-actions>
            <v-btn 
              color="primary" 
              variant="outlined"
              @click="addAddress"
              prepend-icon="mdi-plus"
            >
              Ajouter une adresse
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Statistiques -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h5 mb-4">Mes Statistiques</h2>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="primary" class="mb-2">mdi-package-variant</v-icon>
          <h3 class="text-h6">Produits vus</h3>
          <p class="text-h4">{{ stats.productsViewed }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="success" class="mb-2">mdi-cart</v-icon>
          <h3 class="text-h6">Articles achetés</h3>
          <p class="text-h4">{{ stats.itemsPurchased }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="info" class="mb-2">mdi-clipboard-list</v-icon>
          <h3 class="text-h6">Commandes</h3>
          <p class="text-h4">{{ stats.totalOrders }}</p>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="48" color="warning" class="mb-2">mdi-star</v-icon>
          <h3 class="text-h6">Avis donnés</h3>
          <p class="text-h4">{{ stats.reviewsGiven }}</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Préférences -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-cog</v-icon>
            Préférences
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">Notifications</h3>
                <v-switch
                  v-model="preferences.emailNotifications"
                  label="Notifications par email"
                  color="primary"
                />
                <v-switch
                  v-model="preferences.smsNotifications"
                  label="Notifications SMS"
                  color="primary"
                />
                <v-switch
                  v-model="preferences.pushNotifications"
                  label="Notifications push"
                  color="primary"
                />
              </v-col>
              
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">Préférences de recherche</h3>
                <v-select
                  v-model="preferences.defaultSort"
                  label="Tri par défaut"
                  :items="sortOptions"
                  variant="outlined"
                />
                <v-select
                  v-model="preferences.itemsPerPage"
                  label="Articles par page"
                  :items="itemsPerPageOptions"
                  variant="outlined"
                />
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-btn 
              color="primary" 
              @click="savePreferences"
              :loading="saving"
            >
              Sauvegarder les préférences
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog pour ajouter/modifier une adresse -->
    <v-dialog v-model="addressDialog" max-width="600">
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
                  variant="outlined"
                  :rules="nameRules"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.lastName"
                  label="Nom"
                  variant="outlined"
                  :rules="nameRules"
                />
              </v-col>
            </v-row>
            
            <v-text-field
              v-model="newAddress.street"
              label="Adresse"
              variant="outlined"
              :rules="streetRules"
            />
            
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.city"
                  label="Ville"
                  variant="outlined"
                  :rules="cityRules"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newAddress.zipCode"
                  label="Code postal"
                  variant="outlined"
                  :rules="zipRules"
                />
              </v-col>
            </v-row>
            
            <v-text-field
              v-model="newAddress.mobile"
              label="Téléphone"
              variant="outlined"
              :rules="phoneRules"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn variant="outlined" @click="addressDialog = false">
            Annuler
          </v-btn>
          <v-spacer />
          <v-btn 
            color="primary" 
            @click="saveAddress"
            :loading="saving"
          >
            {{ editingAddress ? 'Modifier' : 'Ajouter' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Message de succès -->
    <v-snackbar
      v-model="showSuccess"
      color="success"
      timeout="3000"
    >
      <v-icon left>mdi-check-circle</v-icon>
      {{ editingAddress ? 'Adresse mise à jour avec succès !' : 'Profil mis à jour avec succès !' }}
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
import { useAuthStore } from '@/stores/auth'
import { useOrderStore } from '@/stores/orders'
import api from '@/services/api'

const authStore = useAuthStore()
const orderStore = useOrderStore()

// Variables réactives
const valid = ref(false)
const updating = ref(false)
const saving = ref(false)
const showSuccess = ref(false)
const showError = ref(false)
const errorMessage = ref('')

// Données du profil
const userProfile = ref({
  fullName: '',
  email: '',
  mobile: '',
  bio: ''
})

const addresses = ref([])
const stats = ref({
  productsViewed: 0,
  itemsPurchased: 0,
  totalOrders: 0,
  reviewsGiven: 0
})

const preferences = ref({
  emailNotifications: true,
  smsNotifications: false,
  pushNotifications: true,
  defaultSort: 'createdAt',
  itemsPerPage: 12
})

const addressDialog = ref(false)
const editingAddress = ref(false)
const newAddress = ref({
  firstName: '',
  lastName: '',
  street: '',
  city: '',
  zipCode: '',
  mobile: ''
})

const sortOptions = ref([
  { title: 'Nouveautés', value: 'createdAt' },
  { title: 'Prix croissant', value: 'sellingPrice' },
  { title: 'Prix décroissant', value: 'sellingPrice' },
  { title: 'Nom', value: 'title' }
])

const itemsPerPageOptions = ref([
  { title: '12 articles', value: 12 },
  { title: '24 articles', value: 24 },
  { title: '48 articles', value: 48 }
])

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

// Fonctions
const loadProfile = async () => {
  try {
    console.log('👤 Chargement du profil client...')
    
    // Récupérer le profil utilisateur
    const response = await api.get('/users/profile')
    userProfile.value = {
      fullName: response.data.fullName || '',
      email: response.data.email || '',
      mobile: response.data.mobile || '',
      bio: response.data.bio || ''
    }
    
    console.log('✅ Profil chargé:', userProfile.value)
  } catch (error) {
    console.error('❌ Erreur lors du chargement du profil:', error)
    // Utiliser les données du store en cas d'erreur
    if (authStore.user) {
      userProfile.value = {
        fullName: authStore.user.fullName || '',
        email: authStore.user.email || '',
        mobile: authStore.user.mobile || '',
        bio: authStore.user.bio || ''
      }
    }
  }
}

const updateProfile = async () => {
  if (!valid.value) return
  
  updating.value = true
  
  try {
    console.log('💾 Mise à jour du profil client...')
    console.log('📝 Données à envoyer:', userProfile.value)
    
    const response = await api.put('/users/profile', userProfile.value)
    
    if (response.status === 200) {
      console.log('✅ Profil mis à jour avec succès:', response.data)
      showSuccess.value = true
      
      // Mettre à jour le store d'authentification
      authStore.user = { ...authStore.user, ...userProfile.value }
      
      // Optionnel: recharger les données
      await loadProfile()
    }
  } catch (error) {
    console.error('❌ Erreur lors de la mise à jour du profil:', error)
    errorMessage.value = error.response?.data?.message || 'Erreur lors de la mise à jour du profil'
    showError.value = true
  } finally {
    updating.value = false
  }
}

const addAddress = () => {
  editingAddress.value = false
  newAddress.value = {
    firstName: '',
    lastName: '',
    street: '',
    city: '',
    zipCode: '',
    mobile: ''
  }
  addressDialog.value = true
}

const editAddress = (address) => {
  editingAddress.value = true
  newAddress.value = { ...address }
  addressDialog.value = true
}

const loadAddresses = async () => {
  try {
    console.log('📍 Chargement des adresses...')
    
    // Récupérer les adresses de l'utilisateur
    const response = await api.get('/users/addresses')
    addresses.value = response.data || []
    
    console.log('✅ Adresses chargées:', addresses.value)
  } catch (error) {
    console.error('❌ Erreur lors du chargement des adresses:', error)
    addresses.value = []
  }
}

const saveAddress = async () => {
  saving.value = true
  try {
    console.log('💾 Sauvegarde de l\'adresse...')
    console.log('📝 Données à envoyer:', newAddress.value)
    
    let response
    if (editingAddress.value) {
      // Mettre à jour une adresse existante
      response = await api.put(`/users/addresses/${newAddress.value.id}`, newAddress.value)
    } else {
      // Créer une nouvelle adresse
      response = await api.post('/users/addresses', newAddress.value)
    }
    
    if (response.status === 200 || response.status === 201) {
      console.log('✅ Adresse sauvegardée avec succès:', response.data)
      showSuccess.value = true
      
      // Mettre à jour la liste locale
      if (editingAddress.value) {
        const index = addresses.value.findIndex(addr => addr.id === newAddress.value.id)
        if (index !== -1) {
          addresses.value[index] = response.data
        }
      } else {
        addresses.value.push(response.data)
      }
      
      addressDialog.value = false
    }
  } catch (error) {
    console.error('❌ Erreur lors de la sauvegarde de l\'adresse:', error)
    errorMessage.value = error.response?.data?.message || 'Erreur lors de la sauvegarde de l\'adresse'
    showError.value = true
  } finally {
    saving.value = false
  }
}

const deleteAddress = async (address) => {
  try {
    console.log('🗑️ Suppression de l\'adresse:', address.id)
    
    const response = await api.delete(`/users/addresses/${address.id}`)
    
    if (response.status === 200) {
      console.log('✅ Adresse supprimée avec succès')
      showSuccess.value = true
      
      // Retirer de la liste locale
      const index = addresses.value.findIndex(addr => addr.id === address.id)
      if (index !== -1) {
        addresses.value.splice(index, 1)
      }
    }
  } catch (error) {
    console.error('❌ Erreur lors de la suppression de l\'adresse:', error)
    errorMessage.value = error.response?.data?.message || 'Erreur lors de la suppression de l\'adresse'
    showError.value = true
  }
}

const savePreferences = async () => {
  saving.value = true
  try {
    // Appel API pour sauvegarder les préférences
    await new Promise(resolve => setTimeout(resolve, 1000))
  } catch (error) {
    console.error('Erreur lors de la sauvegarde des préférences:', error)
  } finally {
    saving.value = false
  }
}

// Cycle de vie
onMounted(async () => {
  try {
    // Charger le profil utilisateur
    await loadProfile()
    
    // Charger les adresses
    await loadAddresses()
    
    // Charger les commandes pour les statistiques
    await orderStore.fetchUserOrders()
    stats.value.totalOrders = orderStore.orders.length
    
    // Simuler d'autres statistiques
    stats.value.productsViewed = Math.floor(Math.random() * 100)
    stats.value.itemsPurchased = Math.floor(Math.random() * 50)
    stats.value.reviewsGiven = Math.floor(Math.random() * 20)
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error)
  }
})
</script>

<style scoped>
.v-card {
  border-radius: 12px;
}

.v-text-field {
  margin-bottom: 8px;
}

.address-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 8px;
  padding: 16px;
  background-color: #fafafa;
}

.address-item:last-child {
  margin-bottom: 0;
}

.address-item:hover {
  background-color: #f5f5f5;
  border-color: #1976d2;
}

.v-list-item-title {
  font-weight: 600;
}

.v-list-item-subtitle {
  margin-top: 4px;
}
</style>