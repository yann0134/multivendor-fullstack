import api from './api'

// Service d'authentification pour le système OTP
export const authService = {
  // Envoyer un OTP pour connexion/inscription
  async sendOtp(email, role = 'ROLE_CUSTOMER') {
    try {
      // Ajouter le préfixe approprié selon le rôle
      let prefixedEmail = email
      if (role === 'ROLE_SELLER') {
        prefixedEmail = `seller_${email}`
      } else if (role === 'ROLE_SUPPLIER') {
        prefixedEmail = `supplier_${email}`
      } else if (role === 'ROLE_DELIVERY') {
        prefixedEmail = `delivery_${email}`
      } else if (role === 'ROLE_WAREHOUSE') {
        prefixedEmail = `warehouse_${email}`
      } else if (role === 'ROLE_ADMIN') {
        prefixedEmail = `admin_${email}`
      }
      
      const response = await api.post('/auth/sent/login-signup-otp', {
        email: prefixedEmail,
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
      // Ajouter le préfixe approprié selon le rôle
      let prefixedEmail = userData.email
      if (userData.role === 'ROLE_SELLER') {
        prefixedEmail = `seller_${userData.email}`
      } else if (userData.role === 'ROLE_SUPPLIER') {
        prefixedEmail = `supplier_${userData.email}`
      } else if (userData.role === 'ROLE_DELIVERY') {
        prefixedEmail = `delivery_${userData.email}`
      } else if (userData.role === 'ROLE_WAREHOUSE') {
        prefixedEmail = `warehouse_${userData.email}`
      } else if (userData.role === 'ROLE_ADMIN') {
        prefixedEmail = `admin_${userData.email}`
      }
      
      const response = await api.post('/auth/signup', {
        email: prefixedEmail,
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
      // Ajouter le préfixe approprié selon le rôle
      let prefixedEmail = loginData.email
      if (loginData.role === 'ROLE_SELLER') {
        prefixedEmail = `seller_${loginData.email}`
      } else if (loginData.role === 'ROLE_SUPPLIER') {
        prefixedEmail = `supplier_${loginData.email}`
      } else if (loginData.role === 'ROLE_DELIVERY') {
        prefixedEmail = `delivery_${loginData.email}`
      } else if (loginData.role === 'ROLE_WAREHOUSE') {
        prefixedEmail = `warehouse_${loginData.email}`
      } else if (loginData.role === 'ROLE_ADMIN') {
        prefixedEmail = `admin_${loginData.email}`
      }
      
      const response = await api.post('/auth/signing', {
        email: prefixedEmail,
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
