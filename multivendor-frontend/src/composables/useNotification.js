import { ref } from 'vue'

const notification = ref({
  show: false,
  message: '',
  type: 'info',
  timeout: 5000
})

export const useNotification = () => {
  const showNotification = (message, type = 'info', timeout = 5000) => {
    notification.value = {
      show: true,
      message,
      type,
      timeout
    }
  }

  const showSuccess = (message) => {
    showNotification(message, 'success')
  }

  const showError = (message) => {
    showNotification(message, 'error')
  }

  const showWarning = (message) => {
    showNotification(message, 'warning')
  }

  const showInfo = (message) => {
    showNotification(message, 'info')
  }

  const hideNotification = () => {
    notification.value.show = false
  }

  return {
    notification,
    showNotification,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    hideNotification
  }
}
