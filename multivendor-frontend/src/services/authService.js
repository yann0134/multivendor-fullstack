import api from './api'

// Service d'authentification pour le système OTP
export const authService = {
  // Envoyer un OTP pour connexion/inscription
  async sendOtp(email, role = 'ROLE_CUSTOMER') {
    try {
      const response = await api.post('/auth/sent/login-signup-otp', {
        email: role === 'ROLE_SELLER' ? `seller_${email}` : email,
        role: role
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'envoi de l\'OTP:', error)
      throw error
    }
  },

  // Inscription avec OTP
  async signup(userData) {
    try {
      const response = await api.post('/auth/signup', {
        email: userData.email,
        fullName: userData.fullName,
        otp: userData.otp
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'inscription:', error)
      throw error
    }
  },

  // Connexion avec OTP
  async login(loginData) {
    try {
      const response = await api.post('/auth/signing', {
        email: loginData.role === 'ROLE_SELLER' ? `seller_${loginData.email}` : loginData.email,
        otp: loginData.otp
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la connexion:', error)
      throw error
    }
  },

  // Vérifier si l'utilisateur est connecté
  isAuthenticated() {
    return !!localStorage.getItem('jwt_token')
  },

  // Obtenir le token JWT
  getToken() {
    return localStorage.getItem('jwt_token')
  },

  // Obtenir les informations utilisateur
  getUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },

  // Déconnexion
  logout() {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('user')
  },

  // Sauvegarder les données d'authentification
  saveAuthData(token, user) {
    localStorage.setItem('jwt_token', token)
    localStorage.setItem('user', JSON.stringify(user))
  }
}

export default authService
