import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '@/services/authService'
import { useCartStore } from './cart'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('jwt_token'))
  const loading = ref(false)
  const otpSent = ref(false)
  const otpEmail = ref('')

  const isAuthenticated = computed(() => !!token.value)

  // Rôles utilisateur
  const USER_ROLES = {
    ADMIN: 'ROLE_ADMIN',
    CUSTOMER: 'ROLE_CUSTOMER',
    SELLER: 'ROLE_SELLER',
    SUPPLIER: 'ROLE_SUPPLIER',
    DELIVERY: 'ROLE_DELIVERY',
    WAREHOUSE: 'ROLE_WAREHOUSE'
  }

  // Envoyer OTP pour connexion/inscription
  const sendOtp = async (email, role = 'ROLE_CUSTOMER') => {
    loading.value = true
    try {
      await authService.sendOtp(email, role)
      otpSent.value = true
      otpEmail.value = email
      return { success: true }
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Erreur d\'envoi OTP')
    } finally {
      loading.value = false
    }
  }

  // Connexion avec OTP
  const login = async (loginData) => {
    loading.value = true
    try {
      console.log('🔍 Login - Données envoyées:', loginData)
      const response = await authService.login(loginData)
      console.log('🔍 Login - Réponse reçue:', response)
      const { jwt, role } = response
      
      token.value = jwt
      
      // Définir le token dans l'instance API avant de récupérer le profil
      authService.setToken(jwt)
      
      // Récupérer les informations complètes de l'utilisateur
      try {
        const userProfile = await authService.getUserProfile()
        user.value = userProfile
        authService.saveAuthData(jwt, userProfile)
        console.log('🔍 Login - Profil utilisateur complet récupéré:', userProfile)
      } catch (profileError) {
        console.warn('⚠️ Impossible de récupérer le profil complet, utilisation des données de base')
        user.value = { email: otpEmail.value, role }
        authService.saveAuthData(jwt, { email: otpEmail.value, role })
      }
      
      console.log('🔍 Login - Utilisateur stocké:', user.value)
      
      // Initialiser le panier après connexion
      try {
        const cartStore = useCartStore()
        await cartStore.initializeCart()
        console.log('🛒 Panier initialisé après connexion')
      } catch (error) {
        console.warn('⚠️ Erreur lors de l\'initialisation du panier:', error)
      }
      
      // Réinitialiser l'état OTP
      otpSent.value = false
      otpEmail.value = ''
      
      return { success: true, role }
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Erreur de connexion')
    } finally {
      loading.value = false
    }
  }

  // Inscription avec OTP
  const signup = async (userData) => {
    loading.value = true
    try {
      console.log('🔍 Signup - Données envoyées:', userData)
      const response = await authService.signup(userData)
      console.log('🔍 Signup - Réponse reçue:', response)
      const { jwt, role } = response
      
      token.value = jwt
      
      // Définir le token dans l'instance API avant de récupérer le profil
      authService.setToken(jwt)
      
      // Récupérer les informations complètes de l'utilisateur
      try {
        const userProfile = await authService.getUserProfile()
        user.value = userProfile
        authService.saveAuthData(jwt, userProfile)
        console.log('🔍 Signup - Profil utilisateur complet récupéré:', userProfile)
      } catch (profileError) {
        console.warn('⚠️ Impossible de récupérer le profil complet, utilisation des données de base')
        user.value = { email: userData.email, role }
        authService.saveAuthData(jwt, { email: userData.email, role })
      }
      
      console.log('🔍 Signup - Utilisateur stocké:', user.value)
      
      // Initialiser le panier après inscription
      try {
        const cartStore = useCartStore()
        await cartStore.initializeCart()
        console.log('🛒 Panier initialisé après inscription')
      } catch (error) {
        console.warn('⚠️ Erreur lors de l\'initialisation du panier:', error)
      }
      
      // Réinitialiser l'état OTP
      otpSent.value = false
      otpEmail.value = ''
      
      return { success: true, role }
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Erreur d\'inscription')
    } finally {
      loading.value = false
    }
  }

  // Récupérer le profil utilisateur
  const fetchUserProfile = async () => {
    try {
      const response = await authService.getUser()
      user.value = response
    } catch (error) {
      console.error('Erreur lors de la récupération du profil:', error)
    }
  }

  // Initialiser l'utilisateur depuis le localStorage
  const initializeUser = async () => {
    // Recharger le token et l'utilisateur depuis le localStorage au rafraîchissement
    const storedToken = localStorage.getItem('jwt_token')
    if (storedToken) {
      token.value = storedToken
      
      // Définir le token dans l'instance API avant de récupérer le profil
      authService.setToken(storedToken)
      
      // Essayer de récupérer le profil complet depuis l'API
      try {
        const userProfile = await authService.getUserProfile()
        user.value = userProfile
        authService.saveAuthData(storedToken, userProfile)
        console.log('🔄 Profil utilisateur récupéré au chargement:', userProfile)
      } catch (error) {
        console.warn('⚠️ Impossible de récupérer le profil complet, utilisation des données locales')
        const userData = localStorage.getItem('user')
        if (userData) {
          try {
            user.value = JSON.parse(userData)
          } catch (parseError) {
            console.error('Erreur lors du parsing des données utilisateur:', parseError)
          }
        }
      }
    }
  }

  // Déconnexion
  const logout = () => {
    user.value = null
    token.value = null
    otpSent.value = false
    otpEmail.value = ''
    authService.logout()
  }

  // Réinitialiser l'état OTP
  const resetOtpState = () => {
    otpSent.value = false
    otpEmail.value = ''
  }

  // Redirection basée sur le rôle
  const getDashboardRoute = (userRole) => {
    console.log('🎯 getDashboardRoute appelé avec:', userRole)
    console.log('🎯 USER_ROLES disponibles:', USER_ROLES)
    
    const routes = {
      [USER_ROLES.CUSTOMER]: '/customer',
      [USER_ROLES.SELLER]: '/seller',
      [USER_ROLES.SUPPLIER]: '/supplier',
      [USER_ROLES.DELIVERY]: '/delivery',
      [USER_ROLES.WAREHOUSE]: '/warehouse',
      [USER_ROLES.ADMIN]: '/admin'
    }
    
    const route = routes[userRole] || '/'
    console.log('🎯 Route sélectionnée:', route)
    return route
  }

  // Vérifier les permissions
  const hasRole = (requiredRole) => {
    return user.value?.role === requiredRole
  }

  const hasAnyRole = (roles) => {
    return roles.includes(user.value?.role)
  }

  return {
    user,
    token,
    loading,
    isAuthenticated,
    otpSent,
    otpEmail,
    USER_ROLES,
    login,
    signup,
    logout,
    sendOtp,
    fetchUserProfile,
    initializeUser,
    resetOtpState,
    getDashboardRoute,
    hasRole,
    hasAnyRole
  }
})
