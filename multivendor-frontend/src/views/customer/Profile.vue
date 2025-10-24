<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mon Profil</h1>
      </v-col>
    </v-row>

    <v-row>
      <!-- Informations personnelles -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-account</v-icon>
            Informations personnelles
          </v-card-title>
          <v-card-text>
            <v-form ref="profileForm">
              <v-text-field
                v-model="userProfile.fullName"
                label="Nom complet"
                variant="outlined"
                :rules="nameRules"
                class="mb-4"
              />
              
              <v-text-field
                v-model="userProfile.email"
                label="Email"
                variant="outlined"
                type="email"
                :rules="emailRules"
                class="mb-4"
                disabled
              />
              
              <v-text-field
                v-model="userProfile.phone"
                label="Téléphone"
                variant="outlined"
                :rules="phoneRules"
                class="mb-4"
              />
              
              <v-textarea
                v-model="userProfile.bio"
                label="Biographie"
                variant="outlined"
                rows="3"
                class="mb-4"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn 
              color="primary" 
              @click="updateProfile"
              :loading="updating"
            >
              Mettre à jour
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Adresses -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-map-marker</v-icon>
            Mes adresses
          </v-card-title>
          <v-card-text>
            <div v-if="addresses.length === 0" class="text-center py-4">
              <v-icon size="48" color="grey">mdi-map-marker-outline</v-icon>
              <p class="text-grey mt-2">Aucune adresse enregistrée</p>
            </div>
            
            <v-list v-else>
              <v-list-item
                v-for="address in addresses"
                :key="address.id"
                class="address-item"
              >
                <v-list-item-title>{{ address.firstName }} {{ address.lastName }}</v-list-item-title>
                <v-list-item-subtitle>
                  {{ address.street }}, {{ address.city }} {{ address.zipCode }}
                </v-list-item-subtitle>
                <template v-slot:append>
                  <v-btn
                    icon
                    size="small"
                    @click="editAddress(address)"
                  >
                    <v-icon>mdi-pencil</v-icon>
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
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useOrderStore } from '@/stores/orders'

const authStore = useAuthStore()
const orderStore = useOrderStore()

const userProfile = ref({
  fullName: '',
  email: '',
  phone: '',
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

const updating = ref(false)
const saving = ref(false)

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
  updating.value = true
  try {
    // Appel API pour mettre à jour le profil
    await new Promise(resolve => setTimeout(resolve, 1000))
    // Mettre à jour le store d'authentification
    authStore.user = { ...authStore.user, ...userProfile.value }
  } catch (error) {
    console.error('Erreur lors de la mise à jour du profil:', error)
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

const saveAddress = async () => {
  saving.value = true
  try {
    // Appel API pour sauvegarder l'adresse
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (editingAddress.value) {
      const index = addresses.value.findIndex(addr => addr.id === newAddress.value.id)
      if (index !== -1) {
        addresses.value[index] = { ...newAddress.value }
      }
    } else {
      addresses.value.push({
        ...newAddress.value,
        id: Date.now() // ID temporaire
      })
    }
    
    addressDialog.value = false
  } catch (error) {
    console.error('Erreur lors de la sauvegarde de l\'adresse:', error)
  } finally {
    saving.value = false
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

onMounted(async () => {
  try {
    // Charger les données du profil
    if (authStore.user) {
      userProfile.value = {
        fullName: authStore.user.fullName || '',
        email: authStore.user.email || '',
        phone: authStore.user.phone || '',
        bio: authStore.user.bio || ''
      }
    }
    
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
.address-item {
  border-bottom: 1px solid #e0e0e0;
}

.address-item:last-child {
  border-bottom: none;
}
</style>