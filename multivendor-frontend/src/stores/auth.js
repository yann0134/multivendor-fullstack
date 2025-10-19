import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '@/services/authService'

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
      const response = await authService.login(loginData)
      const { jwt, role } = response
      
      token.value = jwt
      authService.saveAuthData(jwt, { email: otpEmail.value, role })
      
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
      const response = await authService.signup(userData)
      const { jwt, role } = response
      
      token.value = jwt
      authService.saveAuthData(jwt, { email: userData.email, role })
      
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
    const routes = {
      [USER_ROLES.CUSTOMER]: '/customer',
      [USER_ROLES.SELLER]: '/seller',
      [USER_ROLES.SUPPLIER]: '/supplier',
      [USER_ROLES.DELIVERY]: '/delivery',
      [USER_ROLES.WAREHOUSE]: '/warehouse',
      [USER_ROLES.ADMIN]: '/admin'
    }
    return routes[userRole] || '/'
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
    resetOtpState,
    getDashboardRoute,
    hasRole,
    hasAnyRole
  }
})
