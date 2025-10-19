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
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="shippingAddress.firstName"
                          label="Prénom"
                          :rules="nameRules"
                          required
                        />
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="shippingAddress.lastName"
                          label="Nom"
                          :rules="nameRules"
                          required
                        />
                      </v-col>
                    </v-row>
                    
                    <v-text-field
                      v-model="shippingAddress.street"
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
                          v-model="shippingAddress.zipCode"
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
                    <p>{{ shippingAddress.firstName }} {{ shippingAddress.lastName }}</p>
                    <p>{{ shippingAddress.street }}</p>
                    <p>{{ shippingAddress.city }}, {{ shippingAddress.zipCode }}</p>
                    <p>{{ shippingAddress.mobile }}</p>
                  </div>
                  
                  <div class="mb-4">
                    <h3 class="text-h6 mb-2">Articles commandés</h3>
                    <v-list>
                      <v-list-item
                        v-for="item in cartStore.cartItems"
                        :key="item.id"
                      >
                        <v-list-item-title>{{ item.product.title }}</v-list-item-title>
                        <v-list-item-subtitle>
                          Quantité: {{ item.quantity }} | Prix: {{ (item.sellingPrice * item.quantity).toFixed(2) }}€
                        </v-list-item-subtitle>
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
              <span>Sous-total ({{ cartStore.totalItems }} articles)</span>
              <span>{{ cartStore.totalPrice.toFixed(2) }}€</span>
            </div>
            
            <div class="d-flex justify-space-between mb-2">
              <span>Livraison</span>
              <span>Gratuite</span>
            </div>
            
            <v-divider class="my-4" />
            
            <div class="d-flex justify-space-between text-h6">
              <span>Total</span>
              <span>{{ cartStore.totalPrice.toFixed(2) }}€</span>
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
  firstName: '',
  lastName: '',
  street: '',
  city: '',
  zipCode: '',
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
  return shippingAddress.value.firstName &&
         shippingAddress.value.lastName &&
         shippingAddress.value.street &&
         shippingAddress.value.city &&
         shippingAddress.value.zipCode &&
         shippingAddress.value.mobile
}

const placeOrder = async () => {
  loading.value = true
  try {
    const paymentLinkResponse = await orderStore.createOrder(shippingAddress.value, paymentMethod.value)
    
    // Rediriger vers le lien de paiement
    if (paymentLinkResponse.payment_link_url) {
      window.location.href = paymentLinkResponse.payment_link_url
    } else {
      // Rediriger vers la page de succès
      router.push('/customer/orders')
    }
  } catch (error) {
    console.error('Erreur lors de la création de la commande:', error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await cartStore.fetchCart()
  
  if (cartStore.cartItems.length === 0) {
    router.push('/customer/cart')
  }
})
</script>
