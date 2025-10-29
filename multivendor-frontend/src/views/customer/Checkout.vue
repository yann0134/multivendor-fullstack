<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Finaliser la commande</h1>
      </v-col>
    </v-row>

    <v-row>
      <!-- Formulaire de commande -->
      <v-col cols="12" md="8">
        <v-stepper v-model="step" class="elevation-0">
          <!-- Étape 1: Adresse de livraison -->
          <v-stepper-header>
            <v-stepper-item
              :complete="step > 1"
              :value="1"
              title="Adresse de livraison"
            />
            <v-divider />
            <v-stepper-item
              :complete="step > 2"
              :value="2"
              title="Méthode de paiement"
            />
            <v-divider />
            <v-stepper-item
              :value="3"
              title="Confirmation"
            />
          </v-stepper-header>

          <v-stepper-window>
            <v-stepper-window-item :value="1">
              <v-card flat>
                <v-card-title>Adresse de livraison</v-card-title>
                <v-card-text>
                  <v-form ref="addressForm">
                    <v-row>
                      <v-col cols="12">
                        <v-text-field
                          v-model="shippingAddress.name"
                          label="Nom Complet"
                          :rules="nameRules"
                          required
                        />
                      </v-col>
                    </v-row>
                    
                    <v-text-field
                      v-model="shippingAddress.address"
                      label="Adresse"
                      :rules="streetRules"
                      required
                    />
                    
                    <v-row>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="shippingAddress.city"
                          label="Ville"
                          :rules="cityRules"
                          required
                        />
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="shippingAddress.pinCode"
                          label="Code postal"
                          :rules="zipRules"
                          required
                        />
                      </v-col>
                    </v-row>
                    
                    <v-text-field
                      v-model="shippingAddress.mobile"
                      label="Téléphone"
                      :rules="phoneRules"
                      required
                    />
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer />
                  <v-btn color="primary" @click="nextStep">
                    Continuer
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-stepper-window-item>

            <v-stepper-window-item :value="2">
              <v-card flat>
                <v-card-title>Méthode de paiement</v-card-title>
                <v-card-text>
                  <v-radio-group v-model="paymentMethod">
                    <v-radio
                      label="Carte bancaire (Stripe)"
                      value="STRIPE"
                    />
                    <v-radio
                      label="Paiement en ligne (Razorpay)"
                      value="RAZORPAY"
                    />
                  </v-radio-group>
                </v-card-text>
                <v-card-actions>
                  <v-btn variant="outlined" @click="step = 1">
                    Retour
                  </v-btn>
                  <v-spacer />
                  <v-btn color="primary" @click="nextStep">
                    Continuer
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-stepper-window-item>

            <v-stepper-window-item :value="3">
              <v-card flat>
                <v-card-title>Confirmation de la commande</v-card-title>
                <v-card-text>
                  <div class="mb-4">
                    <h3 class="text-h6 mb-2">Adresse de livraison</h3>
                    <p>{{ shippingAddress.name }}</p>
                    <p>{{ shippingAddress.address }}</p>
                    <p>{{ shippingAddress.city }}, {{ shippingAddress.pinCode }}</p>
                    <p>{{ shippingAddress.mobile }}</p>
                  </div>
                  
                  <div class="mb-4">
                    <h3 class="text-h6 mb-2">Articles commandés</h3>
                    <v-list>
                      <v-list-item
                        v-for="item in cartStore.cartItems"
                        :key="item.id"
                        class="mb-2"
                      >
                        <template v-slot:prepend>
                          <v-avatar size="40" rounded>
                            <v-img
                              :src="getProductImage(item.product)"
                              :alt="item.product.title"
                            />
                          </v-avatar>
                        </template>
                        
                        <v-list-item-title class="text-h6">{{ item.product.title }}</v-list-item-title>
                        <v-list-item-subtitle class="text-body-2 text-grey">
                          {{ item.product.description }}
                        </v-list-item-subtitle>
                        
                        <template v-slot:append>
                          <div class="text-right">
                            <div class="text-caption text-grey">
                              {{ formatQuantity(item.quantity) }} × {{ formatPrice(item.product.sellingPrice) }}
                            </div>
                            <div class="text-h6 font-weight-bold text-primary">
                              {{ formatPrice(calculateItemTotal(item)) }}
                            </div>
                          </div>
                        </template>
                      </v-list-item>
                    </v-list>
                  </div>
                  
                  <div class="mb-4">
                    <h3 class="text-h6 mb-2">Méthode de paiement</h3>
                    <p>{{ paymentMethod === 'STRIPE' ? 'Carte bancaire (Stripe)' : 'Paiement en ligne (Razorpay)' }}</p>
                  </div>
                </v-card-text>
                <v-card-actions>
                  <v-btn variant="outlined" @click="step = 2">
                    Retour
                  </v-btn>
                  <v-spacer />
                  <v-btn
                    color="primary"
                    size="large"
                    @click="placeOrder"
                    :loading="loading"
                  >
                    Confirmer la commande
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-stepper-window-item>
          </v-stepper-window>
        </v-stepper>
      </v-col>
      
      <!-- Résumé de la commande -->
      <v-col cols="12" md="4">
        <v-card sticky>
          <v-card-title>Résumé de la commande</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span class="text-body-2">
                <v-icon size="small" class="mr-1">mdi-cart</v-icon>
                Sous-total ({{ cartStore.totalUnits }} unité{{ cartStore.totalUnits > 1 ? 's' : '' }})
              </span>
              <span class="text-h6 font-weight-bold text-primary">{{ formatPrice(cartStore.totalPrice) }}</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span class="text-body-2">
                <v-icon size="small" class="mr-1">mdi-truck-delivery</v-icon>
                Livraison
              </span>
              <span class="text-success font-weight-bold">
                <v-icon size="small" class="mr-1">mdi-check-circle</v-icon>
                Gratuite
              </span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between text-h5 font-weight-bold">
              <span>Total</span>
              <span class="text-primary">{{ formatPrice(cartStore.totalPrice) }}</span>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useOrderStore } from '@/stores/orders'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()

const step = ref(1)
const loading = ref(false)
const paymentMethod = ref('STRIPE')

const shippingAddress = ref({
  name: '',
  address: '',
  city: '',
  pinCode: '',
  mobile: ''
})

const nameRules = [
  v => !!v || 'Champ requis',
  v => v.length >= 2 || 'Minimum 2 caractères'
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

const phoneRules = [
  v => !!v || 'Téléphone requis',
  v => /^[0-9+\-\s()]+$/.test(v) || 'Format de téléphone invalide'
]

const nextStep = () => {
  if (step.value === 1) {
    // Valider le formulaire d'adresse
    if (validateAddressForm()) {
      step.value = 2
    }
  } else if (step.value === 2) {
    step.value = 3
  }
}

const validateAddressForm = () => {
  // Validation simple - à améliorer avec VeeValidate
  return shippingAddress.value.name &&
         shippingAddress.value.address &&
         shippingAddress.value.city &&
         shippingAddress.value.pinCode &&
         shippingAddress.value.mobile
}

const placeOrder = async () => {
  console.log('🛒 Début de la création de commande...')
  console.log('📍 Adresse de livraison:', shippingAddress.value)
  console.log('💳 Méthode de paiement:', paymentMethod.value)
  
  loading.value = true
  try {
    console.log('📞 Appel de orderStore.createOrder...')
    
    // Le nouvel endpoint ne retourne plus de lien de paiement
    const response = await orderStore.createOrder(shippingAddress.value, paymentMethod.value)
    
    console.log('✅ Commande créée avec succès:', response)
    
    // Rediriger directement vers la page de succès
    router.push('/customer/orders')
    
    // Afficher un message de succès
    console.log('🔄 Redirection vers /customer/orders')
    
  } catch (error) {
    console.error('❌ Erreur lors de la création de la commande:', error)
    console.error('❌ Détails de l\'erreur:', error.message)
    
    // Afficher une notification d'erreur à l'utilisateur
    alert('Erreur lors de la création de la commande: ' + error.message)
  } finally {
    loading.value = false
    console.log('🏁 Fin du processus de création de commande')
  }
}

// Fonction de formatage des prix
const formatPrice = (price) => {
  if (!price) return '0 FCFA'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price).replace('XOF', 'FCFA')
}

// Fonction de formatage des quantités
const formatQuantity = (quantity) => {
  if (!quantity) return '0'
  return new Intl.NumberFormat('fr-FR').format(quantity)
}

// Calcul du total pour un article
const calculateItemTotal = (item) => {
  const unitPrice = item.product?.sellingPrice || 0
  return unitPrice * item.quantity
}

// Fonction pour récupérer l'image du produit
const getProductImage = (product) => {
  if (product?.images && product.images.length > 0) {
    return product.images[0].imageUrl || product.images[0].url || product.images[0]
  }
  // Image par défaut basée sur le type de produit
  if (product?.category?.type === 'ANIMAL') {
    return '/images/default-animal.jpg'
  } else if (product?.category?.type === 'VEGETAL') {
    return '/images/default-vegetable.jpg'
  }
  return '/images/default-product.jpg'
}

onMounted(async () => {
  console.log('🔄 Chargement de la page Checkout...')
  
  // Vérifier l'état de l'utilisateur
  console.log('👤 Utilisateur connecté:', authStore.user)
  console.log('🆔 ID utilisateur:', authStore.user?.id)
  
  await cartStore.fetchCart()
  console.log('🛒 Panier chargé:', cartStore.cartItems)
  console.log('📊 Nombre d\'articles dans le panier:', cartStore.cartItems.length)
  
  if (cartStore.cartItems.length === 0) {
    console.log('⚠️ Panier vide, redirection vers /customer/cart')
    router.push('/customer/cart')
  } else {
    console.log('✅ Panier contient des articles, page Checkout prête')
  }
})
</script>
