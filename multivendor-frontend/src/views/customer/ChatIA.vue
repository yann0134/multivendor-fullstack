<template>
  <v-container fluid class="pa-0 chat-page">
    <!-- En-tête professionnel -->
    <v-app-bar 
      color="white" 
      elevation="1" 
      height="80"
      class="chat-header"
    >
      <v-container class="d-flex align-center pa-0">
        <div class="d-flex align-center">
          <v-avatar 
            size="48" 
            color="primary" 
            class="mr-4"
          >
            <v-icon size="24" color="white">mdi-robot</v-icon>
          </v-avatar>
          <div>
            <h1 class="text-h6 font-weight-bold mb-0">Assistant IA AgriMarket</h1>
            <p class="text-caption text-grey-600 mb-0">
              Votre guide intelligent pour produits et recettes
            </p>
          </div>
        </div>
        
        <v-spacer></v-spacer>
        
        <div class="d-flex align-center">
          <!-- Statut de connexion -->
          <v-chip 
            :color="connectionStatus.color" 
            variant="tonal" 
            size="small"
            class="mr-3"
          >
            <v-icon 
              :icon="connectionStatus.icon" 
              size="16" 
              class="mr-1"
            ></v-icon>
            {{ connectionStatus.text }}
          </v-chip>
          
          <!-- Actions -->
          <v-btn
            icon="mdi-refresh"
            variant="text"
            size="small"
            @click="clearChat"
            class="mr-2"
          >
            <v-icon>mdi-refresh</v-icon>
            <v-tooltip activator="parent" location="bottom">
              Nouvelle conversation
            </v-tooltip>
          </v-btn>
          
          <v-btn
            icon="mdi-download"
            variant="text"
            size="small"
            @click="exportChat"
          >
            <v-icon>mdi-download</v-icon>
            <v-tooltip activator="parent" location="bottom">
              Exporter la conversation
            </v-tooltip>
          </v-btn>
        </div>
      </v-container>
    </v-app-bar>

    <!-- Zone principale de chat -->
    <v-container fluid class="chat-main-container">
      <v-row no-gutters class="fill-height">
        <!-- Zone de messages -->
        <v-col cols="12" md="9" class="chat-messages-container">
          <div class="chat-messages" ref="messagesContainer">
            <!-- Message de bienvenue amélioré -->
            <div v-if="messages.length === 0" class="welcome-section">
              <div class="welcome-content">
                <v-avatar size="80" color="primary" class="mb-4">
                  <v-icon size="40" color="white">mdi-robot</v-icon>
                </v-avatar>
                
                <h2 class="text-h5 font-weight-bold mb-3">
                  Bienvenue sur AgriMarket IA
                </h2>
                
                <p class="text-body-1 text-grey-700 mb-6">
                  Je suis votre assistant personnel pour découvrir nos produits biologiques, 
                  obtenir des conseils nutritionnels et planifier vos repas.
                </p>

                <!-- Capacités avec animations -->
                <div class="capabilities-grid">
                  <div 
                    v-for="(capability, index) in capabilities" 
                    :key="index"
                    class="capability-card"
                    :style="{ animationDelay: `${index * 0.1}s` }"
                  >
                    <v-icon 
                      :color="capability.color" 
                      size="32" 
                      class="mb-3"
                    >
                      {{ capability.icon }}
                    </v-icon>
                    <h3 class="text-subtitle-1 font-weight-bold mb-2">
                      {{ capability.title }}
                    </h3>
                    <p class="text-caption text-grey-600">
                      {{ capability.description }}
                    </p>
                  </div>
                </div>

                <!-- Questions suggérées -->
                <div class="suggested-questions mt-8">
                  <h3 class="text-subtitle-1 font-weight-bold mb-4">
                    Questions populaires
                  </h3>
                  <div class="questions-grid">
                    <v-chip
                      v-for="(question, index) in suggestedQuestions"
                      :key="index"
                      variant="outlined"
                      color="primary"
                      class="question-chip"
                      @click="sendSuggestedQuestion(question)"
                    >
                      {{ question }}
                    </v-chip>
                  </div>
                </div>
              </div>
            </div>

            <!-- Messages de conversation -->
            <div
              v-for="(message, index) in messages"
              :key="index"
              class="message-wrapper"
              :class="{ 
                'user-message': message.isUser, 
                'ai-message': !message.isUser,
                'waiting-message': message.isWaiting
              }"
            >
              <div class="message-bubble" :class="{ 'system': message.isSystem }">
                <!-- En-tête du message -->
                <div class="message-header">
                  <div class="d-flex align-center">
                    <v-avatar 
                      :color="message.isUser ? 'success' : 'primary'" 
                      size="32"
                      class="mr-3"
                    >
                      <v-icon 
                        :icon="message.isUser ? 'mdi-account' : 'mdi-robot'" 
                        size="16" 
                        color="white"
                      ></v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-subtitle-2 font-weight-bold">
                        {{ message.isUser ? 'Vous' : 'Assistant IA' }}
                      </div>
                      <div class="text-caption text-grey-500">
                        {{ formatTime(message.timestamp) }}
                      </div>
                    </div>
                  </div>
                  
                  <!-- Actions sur le message -->
                  <div class="message-actions">
                    <v-btn
                      icon="mdi-content-copy"
                      variant="text"
                      size="x-small"
                      @click="copyMessage(message.content)"
                    >
                      <v-icon size="16">mdi-content-copy</v-icon>
                    </v-btn>
                    <v-btn
                      v-if="!message.isUser"
                      icon="mdi-thumb-up"
                      variant="text"
                      size="x-small"
                      @click="rateMessage(index, true)"
                    >
                      <v-icon size="16">mdi-thumb-up</v-icon>
                    </v-btn>
                    <v-btn
                      v-if="!message.isUser"
                      icon="mdi-thumb-down"
                      variant="text"
                      size="x-small"
                      @click="rateMessage(index, false)"
                    >
                      <v-icon size="16">mdi-thumb-down</v-icon>
                    </v-btn>
                  </div>
                </div>

                <!-- Contenu du message -->
                <div class="message-content" v-html="formatMessage(message.content)"></div>

                <!-- Métriques du message -->
                <div v-if="!message.isUser" class="message-metrics">
                  <v-chip 
                    v-if="message.responseTime" 
                    size="x-small" 
                    variant="tonal" 
                    color="info"
                    class="mr-2"
                  >
                    {{ message.responseTime }}ms
                  </v-chip>
                  <v-chip 
                    v-if="message.tokens" 
                    size="x-small" 
                    variant="tonal" 
                    color="secondary"
                  >
                    {{ message.tokens }} tokens
                  </v-chip>
                </div>
              </div>
            </div>

            <!-- Indicateur de frappe amélioré -->
            <div v-if="isTyping" class="typing-indicator">
              <div class="message-bubble ai-message">
                <div class="message-header">
                  <v-avatar color="primary" size="32" class="mr-3">
                    <v-icon icon="mdi-robot" size="16" color="white"></v-icon>
                  </v-avatar>
                  <div>
                    <div class="text-subtitle-2 font-weight-bold">Assistant IA</div>
                    <div class="text-caption text-grey-500">En cours de rédaction...</div>
                  </div>
                </div>
                <div class="typing-animation">
                  <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Zone de saisie améliorée -->
          <div class="chat-input-section">
            <v-divider></v-divider>
            <div class="chat-input">
              <v-form @submit.prevent="sendMessage">
                <div class="input-container">
                  <!-- Zone de texte avec suggestions -->
                  <div class="text-input-wrapper">
                    <v-textarea
                      v-model="currentMessage"
                      placeholder="Posez votre question à l'assistant IA..."
                      variant="outlined"
                      rows="1"
                      auto-grow
                      hide-details
                      class="message-input"
                      :disabled="isLoading"
                      @keydown.enter.exact.prevent="sendMessage"
                      @keydown.enter.shift.exact="currentMessage += '\n'"
                      @input="onInputChange"
                    ></v-textarea>
                    
                    <!-- Suggestions en temps réel -->
                    <div v-if="showSuggestions && suggestions.length > 0" class="suggestions-dropdown">
                      <v-list density="compact">
                        <v-list-item
                          v-for="(suggestion, index) in suggestions"
                          :key="index"
                          @click="selectSuggestion(suggestion)"
                          class="suggestion-item"
                        >
                          <v-icon size="16" class="mr-2">mdi-lightbulb</v-icon>
                          {{ suggestion }}
                        </v-list-item>
                      </v-list>
                    </div>
                  </div>

                  <!-- Boutons d'action -->
                  <div class="input-actions">
                    <v-btn
                      icon="mdi-attachment"
                      variant="text"
                      size="small"
                      class="mr-2"
                      @click="attachFile"
                    >
                      <v-icon>mdi-attachment</v-icon>
                    </v-btn>
                    
                    <v-btn
                      type="submit"
                      color="primary"
                      :loading="isLoading"
                      :disabled="!currentMessage.trim()"
                      size="small"
                      class="send-button"
                    >
                      <v-icon left>mdi-send</v-icon>
                      Envoyer
                    </v-btn>
                  </div>
                </div>

                <!-- Instructions et raccourcis -->
                <div class="input-footer">
                  <div class="d-flex justify-space-between align-center">
                    <div class="text-caption text-grey-600">
                      <v-icon size="14" class="mr-1">mdi-keyboard</v-icon>
                      Entrée pour envoyer • Maj+Entrée pour nouvelle ligne
                    </div>
                    <div class="text-caption text-grey-600">
                      {{ currentMessage.length }}/2000 caractères
                    </div>
                  </div>
                </div>
              </v-form>
            </div>
          </div>
        </v-col>

        <!-- Panneau latéral avec informations -->
        <v-col cols="12" md="3" class="sidebar">
          <v-card class="sidebar-card" elevation="1">
            <!-- Statistiques -->
            <v-card-title class="text-subtitle-1 font-weight-bold">
              <v-icon class="mr-2">mdi-chart-line</v-icon>
              Statistiques
            </v-card-title>
            <v-card-text>
              <div class="stat-item">
                <div class="stat-value">{{ messages.filter(m => m.isUser).length }}</div>
                <div class="stat-label">Messages envoyés</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ averageResponseTime }}ms</div>
                <div class="stat-label">Temps de réponse moyen</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ sessionDuration }}</div>
                <div class="stat-label">Durée de session</div>
              </div>
            </v-card-text>

            <!-- Historique des conversations -->
            <v-card-title class="text-subtitle-1 font-weight-bold">
              <v-icon class="mr-2">mdi-history</v-icon>
              Historique
            </v-card-title>
            <v-card-text>
              <v-list density="compact">
                <v-list-item
                  v-for="(session, index) in chatHistory"
                  :key="index"
                  @click="switchToSession(session.id)"
                  class="history-item"
                >
                  <template v-slot:prepend>
                    <v-icon size="16">mdi-chat</v-icon>
                  </template>
                  <v-list-item-title class="text-caption">
                    {{ session.title }}
                  </v-list-item-title>
                  <v-list-item-subtitle class="text-caption">
                    {{ formatDate(session.date) }}
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-card-text>

            <!-- Paramètres -->
            <v-card-title class="text-subtitle-1 font-weight-bold">
              <v-icon class="mr-2">mdi-cog</v-icon>
              Paramètres
            </v-card-title>
            <v-card-text>
              <v-switch
                v-model="settings.autoScroll"
                label="Défilement automatique"
                density="compact"
                hide-details
                class="mb-2"
              ></v-switch>
              <v-switch
                v-model="settings.showMetrics"
                label="Afficher les métriques"
                density="compact"
                hide-details
                class="mb-2"
              ></v-switch>
              <v-switch
                v-model="settings.soundEnabled"
                label="Sons activés"
                density="compact"
                hide-details
              ></v-switch>
              
              <!-- Bouton pour supprimer toutes les conversations -->
              <v-divider class="my-3"></v-divider>
              <v-btn
                color="error"
                variant="outlined"
                size="small"
                block
                @click="showDeleteAllDialog = true"
                class="delete-all-btn"
              >
                <v-icon left>mdi-delete-sweep</v-icon>
                Supprimer toutes les conversations
              </v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    
    <!-- Dialog de confirmation pour supprimer toutes les conversations -->
    <v-dialog v-model="showDeleteAllDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h6">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Confirmer la suppression
        </v-card-title>
        
        <v-card-text>
          <p class="text-body-1 mb-3">
            Êtes-vous sûr de vouloir supprimer <strong>toutes vos conversations</strong> ?
          </p>
          <p class="text-body-2 text-grey-600">
            Cette action est irréversible. Tous vos messages et l'historique des conversations seront définitivement supprimés.
          </p>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="showDeleteAllDialog = false"
          >
            Annuler
          </v-btn>
          <v-btn
            color="error"
            variant="flat"
            :loading="isDeletingAll"
            @click="deleteAllConversations"
          >
            <v-icon left>mdi-delete-sweep</v-icon>
            Supprimer tout
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// Variables réactives principales
const messages = ref([])
const currentMessage = ref('')
const isLoading = ref(false)
const isTyping = ref(false)
const messagesContainer = ref(null)
const sessionStartTime = ref(new Date())
const chatHistory = ref([])
const showSuggestions = ref(false)
const suggestions = ref([])
const currentSessionId = ref(null)
const showDeleteAllDialog = ref(false)
const isDeletingAll = ref(false)

// Store d'authentification
const authStore = useAuthStore()

// Informations utilisateur depuis le store
const userInfo = computed(() => ({
  userId: authStore.user?.id || null,
  userEmail: authStore.user?.email || null,
  username: authStore.user?.username || authStore.user?.email || null
}))

// Configuration de l'API
const CHAT_API_URL = 'http://localhost:8866/chat'
const CHAT_SESSION_API_URL = 'http://localhost:3026/api/chat'

// Paramètres utilisateur
const settings = ref({
  autoScroll: true,
  showMetrics: true,
  soundEnabled: false
})

// Données statiques
const capabilities = ref([
  {
    icon: 'mdi-package-variant',
    title: 'Recherche de Produits',
    description: 'Trouvez des produits biologiques, comparez les prix et découvrez les nouveautés',
    color: 'primary'
  },
  {
    icon: 'mdi-chef-hat',
    title: 'Recettes Personnalisées',
    description: 'Obtenez des suggestions de recettes adaptées à vos produits préférés',
    color: 'success'
  },
  {
    icon: 'mdi-calendar-clock',
    title: 'Planning de Repas',
    description: 'Planifiez vos repas de la semaine avec des conseils nutritionnels',
    color: 'info'
  },
  {
    icon: 'mdi-heart-pulse',
    title: 'Conseils Nutritionnels',
    description: 'Recevez des conseils sur l\'équilibre alimentaire et la santé',
    color: 'warning'
  }
])

const suggestedQuestions = ref([
  "Combien de produits biologiques avez-vous ?",
  "Quelles sont vos meilleures recettes avec des légumes ?",
  "Pouvez-vous me suggérer un planning de repas pour la semaine ?",
  "Quels sont vos produits les plus populaires ?",
  "Comment conserver mes fruits et légumes ?",
  "Quels sont les bienfaits des produits bio ?"
])

// Computed properties
const connectionStatus = computed(() => {
  // Simulation du statut de connexion
  return {
    color: 'success',
    icon: 'mdi-circle',
    text: 'Connecté'
  }
})

const averageResponseTime = computed(() => {
  const aiMessages = messages.value.filter(m => !m.isUser && m.responseTime)
  if (aiMessages.length === 0) return 0
  return Math.round(aiMessages.reduce((sum, m) => sum + m.responseTime, 0) / aiMessages.length)
})

const sessionDuration = computed(() => {
  const now = new Date()
  const diff = now - sessionStartTime.value
  const minutes = Math.floor(diff / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// Méthodes principales
const sendMessage = async () => {
  if (!currentMessage.value.trim() || isLoading.value) return

  const startTime = Date.now()
  const messageToSend = currentMessage.value.trim()
  currentMessage.value = ''
  showSuggestions.value = false
  
  // Créer ou récupérer la session
  if (!currentSessionId.value) {
    await createOrGetSession()
  }
  
  // Ajouter le message utilisateur à la session
  const userMessage = await addMessageToSession(messageToSend, true)
  messages.value.push(userMessage)
  
  // Scroll vers le bas
  if (settings.value.autoScroll) {
    await scrollToBottom()
  }
  
  // Afficher l'indicateur de frappe avec message informatif
  isTyping.value = true
  isLoading.value = true
  
  // Ajouter un message informatif pour les réponses longues
  const waitingMessage = {
    content: '🤖 <strong>L\'IA traite votre demande...</strong><br><small>Les réponses complexes peuvent prendre plusieurs minutes. Veuillez patienter.</small>',
    isUser: false,
    timestamp: new Date(),
    isWaiting: true
  }
  messages.value.push(waitingMessage)

  try {
    console.log('🤖 Envoi de la question à l\'IA:', messageToSend)
    
    // Envoyer la requête avec session (sans timeout pour permettre des réponses longues)
    const response = await axios.post(`${CHAT_API_URL}/session`, {
      query: messageToSend,
      sessionId: currentSessionId.value,
      userId: userInfo.value.userId,
      userEmail: userInfo.value.userEmail
    })

    const responseTime = Date.now() - startTime
    const aiResponse = response.data.response || response.data
    
    // Remplacer le message d'attente par la vraie réponse
    const waitingIndex = messages.value.findIndex(m => m.isWaiting)
    if (waitingIndex !== -1) {
      messages.value.splice(waitingIndex, 1)
    }
    
    // Ajouter le message IA à la session
    const aiMessage = await addMessageToSession(aiResponse, false, responseTime, response.data.tokens)
    messages.value.push(aiMessage)
    
    console.log('✅ Réponse reçue de l\'IA:', aiResponse)

    // Jouer un son si activé
    if (settings.value.soundEnabled) {
      playNotificationSound()
    }

  } catch (error) {
    console.error('❌ Erreur lors de la communication avec l\'IA:', error)
    
    // Supprimer le message d'attente en cas d'erreur
    const waitingIndex = messages.value.findIndex(m => m.isWaiting)
    if (waitingIndex !== -1) {
      messages.value.splice(waitingIndex, 1)
    }
    
    const errorMessage = {
      content: getErrorMessage(error),
      isUser: false,
      timestamp: new Date(),
      responseTime: Date.now() - startTime
    }
    
    messages.value.push(errorMessage)
  } finally {
    isTyping.value = false
    isLoading.value = false
    if (settings.value.autoScroll) {
      await scrollToBottom()
    }
  }
}

const sendSuggestedQuestion = (question) => {
  currentMessage.value = question
  sendMessage()
}

const getErrorMessage = (error) => {
  if (error.code === 'ECONNREFUSED') {
    return '❌ <strong>Service IA indisponible</strong><br>Le service de chat IA n\'est pas accessible. Veuillez réessayer plus tard.'
  } else if (error.response?.status === 404) {
    return '❌ <strong>Endpoint non trouvé</strong><br>Le service de chat IA n\'est pas configuré correctement.'
  } else if (error.response?.status >= 500) {
    return '❌ <strong>Erreur serveur</strong><br>Une erreur interne s\'est produite. Veuillez réessayer.'
  } else if (error.code === 'ECONNABORTED') {
    return '❌ <strong>Connexion interrompue</strong><br>La connexion a été interrompue. Veuillez réessayer.'
  } else {
    return '❌ <strong>Erreur de communication</strong><br>Impossible de communiquer avec l\'assistant IA. Veuillez vérifier votre connexion.'
  }
}

const formatMessage = (content) => {
  return content.replace(/\n/g, '<br>')
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Nouvelles fonctionnalités
const clearChat = async () => {
  messages.value = []
  sessionStartTime.value = new Date()
  
  // Créer une nouvelle session
  await createOrGetSession()
  
  // Ajouter un message de bienvenue
  const welcomeMessage = {
    content: `Nouvelle conversation démarrée ! Comment puis-je vous aider aujourd'hui ?`,
    isUser: false,
    timestamp: new Date()
  }
  messages.value.push(welcomeMessage)
}

const exportChat = () => {
  const chatData = {
    session: {
      startTime: sessionStartTime.value,
      endTime: new Date(),
      duration: sessionDuration.value,
      messageCount: messages.value.length
    },
    messages: messages.value.map(m => ({
      content: m.content,
      isUser: m.isUser,
      timestamp: m.timestamp,
      responseTime: m.responseTime
    }))
  }
  
  const blob = new Blob([JSON.stringify(chatData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `chat-agrimarket-${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)
}

const copyMessage = async (content) => {
  try {
    await navigator.clipboard.writeText(content)
    // Afficher une notification de succès
    console.log('Message copié dans le presse-papiers')
  } catch (err) {
    console.error('Erreur lors de la copie:', err)
  }
}


const onInputChange = () => {
  // Générer des suggestions basées sur le texte saisi
  if (currentMessage.value.length > 2) {
    const input = currentMessage.value.toLowerCase()
    suggestions.value = suggestedQuestions.value.filter(q => 
      q.toLowerCase().includes(input)
    ).slice(0, 3)
    showSuggestions.value = suggestions.value.length > 0
  } else {
    showSuggestions.value = false
  }
}

const selectSuggestion = (suggestion) => {
  currentMessage.value = suggestion
  showSuggestions.value = false
}

const attachFile = () => {
  // Fonctionnalité d'attachement de fichiers (à implémenter)
  console.log('Attachement de fichier (fonctionnalité à venir)')
}


const playNotificationSound = () => {
  // Jouer un son de notification (à implémenter)
  console.log('Son de notification joué')
}

// Fonction pour nettoyer les anciennes données globales
const cleanupGlobalStorage = () => {
  // Supprimer les anciennes clés globales pour éviter les fuites de données
  localStorage.removeItem('agrimarket-chat-history')
  localStorage.removeItem('agrimarket-current-session')
  console.log('🧹 Anciennes données globales nettoyées')
}

// Nouvelles méthodes pour la gestion des sessions
const createOrGetSession = async () => {
  try {
    // Essayer de récupérer une session existante depuis le localStorage (spécifique à l'utilisateur)
    const sessionStorageKey = `agrimarket-current-session-${userInfo.value.userId || userInfo.value.userEmail}`
    const savedSessionId = localStorage.getItem(sessionStorageKey)
    if (savedSessionId) {
      const session = await loadSession(savedSessionId)
      if (session) {
        currentSessionId.value = savedSessionId
        return
      }
    }
    
    // Créer une nouvelle session
    const response = await axios.post(`${CHAT_SESSION_API_URL}/sessions`, {
      userId: userInfo.value.userId,
      userEmail: userInfo.value.userEmail
    })
    
    currentSessionId.value = response.data.sessionId
    localStorage.setItem(sessionStorageKey, currentSessionId.value)
    
    console.log('🆕 Nouvelle session créée:', currentSessionId.value)
  } catch (error) {
    console.error('❌ Erreur lors de la création de session:', error)
    // Fallback: utiliser un ID de session local
    currentSessionId.value = 'local_' + Date.now()
  }
}

const addMessageToSession = async (content, isUser, responseTime = null, tokens = null) => {
  const message = {
    content,
    isUser,
    timestamp: new Date(),
    responseTime,
    tokens
  }
  
  // Si on a une session valide, sauvegarder en base
  if (currentSessionId.value && !currentSessionId.value.startsWith('local_')) {
    try {
      await axios.post(`${CHAT_SESSION_API_URL}/sessions/${currentSessionId.value}/messages`, {
        content,
        isUser,
        responseTime,
        tokens
      })
    } catch (error) {
      console.error('❌ Erreur lors de la sauvegarde du message:', error)
    }
  }
  
  return message
}

const loadSession = async (sessionId) => {
  try {
    const response = await axios.get(`${CHAT_SESSION_API_URL}/sessions/${sessionId}`)
    const session = response.data
    
    // Charger les messages de la session
    messages.value = session.messages.map(msg => ({
      content: msg.content,
      isUser: msg.isUser,
      timestamp: new Date(msg.createdAt),
      responseTime: msg.responseTime,
      tokens: msg.tokens
    }))
    
    console.log('📖 Session chargée:', sessionId, 'avec', messages.value.length, 'messages')
    return session
  } catch (error) {
    console.error('❌ Erreur lors du chargement de la session:', error)
    return null
  }
}

const loadUserSessions = async () => {
  try {
    let response
    if (userInfo.value.userId) {
      response = await axios.get(`${CHAT_SESSION_API_URL}/sessions/user/${userInfo.value.userId}`)
    } else if (userInfo.value.userEmail) {
      response = await axios.get(`${CHAT_SESSION_API_URL}/sessions/email/${userInfo.value.userEmail}`)
    } else {
      return
    }
    
    chatHistory.value = response.data.map(session => ({
      id: session.sessionId,
      title: session.title,
      date: new Date(session.createdAt),
      messageCount: session.messageCount,
      duration: session.duration
    }))
    
    console.log('📚 Historique chargé:', chatHistory.value.length, 'sessions')
  } catch (error) {
    console.error('❌ Erreur lors du chargement de l\'historique:', error)
  }
}

const switchToSession = async (sessionId) => {
  const session = await loadSession(sessionId)
  if (session) {
    currentSessionId.value = sessionId
    localStorage.setItem('agrimarket-current-session', sessionId)
    sessionStartTime.value = new Date(session.createdAt)
    
    // Générer un titre si nécessaire
    if (session.title === 'Nouvelle conversation' && messages.value.length > 0) {
      try {
        await axios.post(`${CHAT_SESSION_API_URL}/sessions/${sessionId}/generate-title`)
      } catch (error) {
        console.error('❌ Erreur lors de la génération du titre:', error)
      }
    }
  }
}

const updateSessionTitle = async (title) => {
  if (currentSessionId.value && !currentSessionId.value.startsWith('local_')) {
    try {
      await axios.put(`${CHAT_SESSION_API_URL}/sessions/${currentSessionId.value}/title`, {
        title
      })
    } catch (error) {
      console.error('❌ Erreur lors de la mise à jour du titre:', error)
    }
  }
}

const rateMessage = async (index, isPositive) => {
  const message = messages.value[index]
  if (message && !message.isUser) {
    message.rating = isPositive ? 'positive' : 'negative'
    
    // Sauvegarder l'évaluation en base si possible
    if (currentSessionId.value && !currentSessionId.value.startsWith('local_')) {
      try {
        // Note: Cette fonctionnalité nécessiterait l'ID du message depuis la base
        console.log('⭐ Message évalué:', isPositive ? 'positif' : 'négatif')
      } catch (error) {
        console.error('❌ Erreur lors de l\'évaluation:', error)
      }
    }
  }
}

const deleteAllConversations = async () => {
  if (!userInfo.value.userId && !userInfo.value.userEmail) {
    console.error('❌ Impossible de supprimer: utilisateur non identifié')
    return
  }

  isDeletingAll.value = true
  
  try {
    console.log('🗑️ Suppression de toutes les conversations...')
    
    let response
    if (userInfo.value.userId) {
      // Supprimer par userId
      response = await axios.delete(`${CHAT_SESSION_API_URL}/sessions/user/${userInfo.value.userId}/all`)
    } else if (userInfo.value.userEmail) {
      // Supprimer par email
      response = await axios.delete(`${CHAT_SESSION_API_URL}/sessions/email/${userInfo.value.userEmail}/all`)
    }
    
    if (response && response.data) {
      console.log('✅ Suppression réussie:', response.data)
      
      // Réinitialiser l'interface
      messages.value = []
      chatHistory.value = []
      currentSessionId.value = null
      sessionStartTime.value = new Date()
      
      // Nettoyer le localStorage spécifique à l'utilisateur
      const historyStorageKey = `agrimarket-chat-history-${userInfo.value.userId || userInfo.value.userEmail}`
      const sessionStorageKey = `agrimarket-current-session-${userInfo.value.userId || userInfo.value.userEmail}`
      localStorage.removeItem(historyStorageKey)
      localStorage.removeItem(sessionStorageKey)
      
      // Fermer le dialog
      showDeleteAllDialog.value = false
      
      // Afficher un message de confirmation
      messages.value.push({
        content: '✅ <strong>Toutes vos conversations ont été supprimées avec succès.</strong><br><small>Vous pouvez maintenant commencer une nouvelle conversation.</small>',
        isUser: false,
        timestamp: new Date(),
        isSystem: true
      })
      
      // Scroll vers le bas
      await scrollToBottom()
    }
    
  } catch (error) {
    console.error('❌ Erreur lors de la suppression des conversations:', error)
    
    // Afficher un message d'erreur
    messages.value.push({
      content: '❌ <strong>Erreur lors de la suppression des conversations.</strong><br><small>Veuillez réessayer plus tard.</small>',
      isUser: false,
      timestamp: new Date(),
      isSystem: true
    })
    
    await scrollToBottom()
  } finally {
    isDeletingAll.value = false
  }
}

// Watchers
watch(messages, () => {
  // Sauvegarder l'historique localement avec l'ID utilisateur
  if (userInfo.value.userId || userInfo.value.userEmail) {
    const storageKey = `agrimarket-chat-history-${userInfo.value.userId || userInfo.value.userEmail}`
    localStorage.setItem(storageKey, JSON.stringify(messages.value))
  }
}, { deep: true })

// Lifecycle
onMounted(async () => {
  // Initialiser le store d'authentification
  try {
    authStore.initializeUser()
    console.log('👤 Utilisateur connecté:', {
      userId: userInfo.value.userId,
      email: userInfo.value.userEmail,
      username: userInfo.value.username,
      isAuthenticated: authStore.isAuthenticated
    })
    
    // Nettoyer les anciennes données globales (migration)
    cleanupGlobalStorage()
  } catch (e) {
    console.error('Erreur lors de l\'initialisation de l\'utilisateur:', e)
  }
  
  // Charger l'historique des sessions
  await loadUserSessions()
  
  // Essayer de charger la session courante (spécifique à l'utilisateur)
  const sessionStorageKey = `agrimarket-current-session-${userInfo.value.userId || userInfo.value.userEmail}`
  const savedSessionId = localStorage.getItem(sessionStorageKey)
  if (savedSessionId) {
    const session = await loadSession(savedSessionId)
    if (session) {
      currentSessionId.value = savedSessionId
      return
    }
  }
  
  // Fallback: charger l'historique local (spécifique à l'utilisateur)
  const historyStorageKey = `agrimarket-chat-history-${userInfo.value.userId || userInfo.value.userEmail}`
  const savedHistory = localStorage.getItem(historyStorageKey)
  if (savedHistory) {
    try {
      messages.value = JSON.parse(savedHistory)
    } catch (e) {
      console.error('Erreur lors du chargement de l\'historique local:', e)
    }
  }
  
  // Si pas d'historique, ajouter un message de bienvenue
  if (messages.value.length === 0) {
    const welcomeMessage = {
      content: `Bonjour ! Je suis votre assistant IA AgriMarket. Je peux vous aider à trouver des produits, suggérer des recettes, ou répondre à vos questions sur notre plateforme. Que puis-je faire pour vous aujourd'hui ?`,
      isUser: false,
      timestamp: new Date()
    }
    messages.value.push(welcomeMessage)
  }
})
</script>

<style scoped>
/* Layout principal */
.chat-page {
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.chat-header {
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.chat-main-container {
  height: calc(100vh - 80px);
  padding: 0;
}

.chat-messages-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Zone de messages */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  position: relative;
}

.chat-messages::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0%, transparent 100%);
  pointer-events: none;
  z-index: 1;
}

/* Section de bienvenue */
.welcome-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  padding: 40px 20px;
}

.welcome-content {
  text-align: center;
  max-width: 800px;
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Grille des capacités */
.capabilities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.capability-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: slideInUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

.capability-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Questions suggérées */
.suggested-questions {
  margin-top: 40px;
}

.questions-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.question-chip {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 20px;
}

.question-chip:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

/* Messages */
.message-wrapper {
  margin-bottom: 20px;
  display: flex;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 16px 20px;
  border-radius: 20px;
  position: relative;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.message-bubble:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.ai-message .message-bubble {
  background: white;
  color: #333;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-bottom-left-radius: 6px;
}

/* En-tête des messages */
.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.message-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.message-bubble:hover .message-actions {
  opacity: 1;
}

.message-content {
  line-height: 1.6;
  word-wrap: break-word;
  font-size: 15px;
}

.message-metrics {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

/* Indicateur de frappe */
.typing-indicator {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20px;
  animation: messageSlideIn 0.3s ease-out;
}

.typing-animation {
  padding: 8px 0;
}

.typing-dots {
  display: flex;
  align-items: center;
  gap: 6px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #1976d2;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* Zone de saisie */
.chat-input-section {
  background: white;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.chat-input {
  padding: 20px 24px;
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.text-input-wrapper {
  flex: 1;
  position: relative;
}

.message-input {
  font-size: 15px;
  border-radius: 24px;
}

.suggestions-dropdown {
  position: absolute;
  bottom: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  margin-bottom: 8px;
  z-index: 10;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.suggestion-item {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.suggestion-item:hover {
  background-color: #f5f5f5;
}

.input-actions {
  display: flex;
  align-items: center;
}

.send-button {
  border-radius: 20px;
  padding: 0 20px;
  height: 40px;
  font-weight: 600;
}

.input-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

/* Sidebar */
.sidebar {
  background: white;
  border-left: 1px solid rgba(0, 0, 0, 0.08);
  height: 100%;
  overflow-y: auto;
}

.sidebar-card {
  height: 100%;
  border-radius: 0;
}

.stat-item {
  text-align: center;
  padding: 16px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.history-item {
  cursor: pointer;
  transition: background-color 0.2s ease;
  border-radius: 8px;
  margin-bottom: 4px;
}

.history-item:hover {
  background-color: #f5f5f5;
}

/* Scrollbar personnalisée */
.chat-messages::-webkit-scrollbar,
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track,
.sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb,
.sidebar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover,
.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* Responsive */
@media (max-width: 960px) {
  .sidebar {
    display: none;
  }
  
  .chat-messages-container {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .chat-page {
    height: 100vh;
  }
  
  .chat-main-container {
    height: calc(100vh - 80px);
  }
  
  .message-bubble {
    max-width: 85%;
  }
  
  .capabilities-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .capability-card {
    padding: 20px;
  }
  
  .chat-input {
    padding: 16px;
  }
  
  .input-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .input-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .questions-grid {
    flex-direction: column;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .chat-messages {
    padding: 16px;
  }
  
  .welcome-content {
    padding: 20px 10px;
  }
  
  .capability-card {
    padding: 16px;
  }
  
  .message-bubble {
    max-width: 90%;
    padding: 12px 16px;
  }
}

/* Message d'attente */
.waiting-message .message-bubble {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  border: 2px dashed #f39c12;
  animation: pulse 2s infinite;
}

.waiting-message .message-bubble small {
  color: #856404;
  font-style: italic;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

/* Bouton de suppression */
.delete-all-btn {
  margin-top: 8px;
  transition: all 0.3s ease;
}

.delete-all-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

/* Message système */
.message-bubble.system {
  background: linear-gradient(135deg, #e8f5e8 0%, #c8e6c9 100%);
  border-left: 4px solid #4caf50;
  font-style: italic;
}

.message-bubble.system strong {
  color: #2e7d32;
}

/* Animations et transitions */
.v-btn {
  transition: all 0.3s ease;
}

.v-chip {
  transition: all 0.3s ease;
}

.v-card {
  transition: all 0.3s ease;
}

/* États de focus et hover */
.message-input:focus {
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
}

.send-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

/* Thème sombre (optionnel) */
@media (prefers-color-scheme: dark) {
  .chat-page {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  }
  
  .chat-messages {
    background: linear-gradient(180deg, #2d2d2d 0%, #1a1a1a 100%);
  }
  
  .ai-message .message-bubble {
    background: #3d3d3d;
    color: #ffffff;
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .capability-card {
    background: #3d3d3d;
    color: #ffffff;
  }
  
  .sidebar {
    background: #2d2d2d;
  }
}
</style>
