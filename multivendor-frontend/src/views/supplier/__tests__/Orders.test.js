import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Orders from '../Orders.vue'

// Mock de l'API
const mockApi = {
  get: vi.fn(),
  put: vi.fn()
}

// Données de test
const mockOrders = [
  {
    id: 1,
    title: 'Tomates Bio',
    description: 'Tomates biologiques fraîches',
    shipmentStatus: 'NOT_SHIPPED',
    receptionStatus: 'PENDING',
    supplierPrice: 1000,
    createdAt: '2025-10-21T18:03:34',
    images: []
  },
  {
    id: 2,
    title: 'Carottes',
    description: 'Carottes du jardin',
    shipmentStatus: 'SHIPPED',
    receptionStatus: 'RECEIVED',
    supplierPrice: 500,
    createdAt: '2025-10-20T15:30:00',
    images: []
  }
]

describe('Orders.vue', () => {
  it('affiche le bouton Expédier pour les produits non expédiés', () => {
    const wrapper = mount(Orders, {
      data() {
        return {
          orders: mockOrders,
          loading: false
        }
      }
    })
    
    expect(wrapper.text()).toContain('Expédier')
  })
  
  it('affiche le statut Expédié pour les produits expédiés', () => {
    const wrapper = mount(Orders, {
      data() {
        return {
          orders: mockOrders,
          loading: false
        }
      }
    })
    
    expect(wrapper.text()).toContain('Expédié')
  })
  
  it('calcule correctement la date de livraison', () => {
    const wrapper = mount(Orders)
    
    // Test avec une date de commande
    const orderDate = '2025-10-21T18:03:34'
    const result = wrapper.vm.formatDeliveryDate(orderDate)
    
    console.log('Test date livraison:', result)
    expect(result).toContain('24 oct. 2025')
  })
  
  it('gère les dates nulles', () => {
    const wrapper = mount(Orders)
    
    const result = wrapper.vm.formatDeliveryDate(null)
    expect(result).toBe('N/A')
  })
  
  it('gère les dates vides', () => {
    const wrapper = mount(Orders)
    
    const result = wrapper.vm.formatDeliveryDate('')
    expect(result).toBe('N/A')
  })
  
  it('ouvre le modal de confirmation pour l\'expédition', () => {
    const wrapper = mount(Orders, {
      data() {
        return {
          orders: mockOrders,
          loading: false,
          showShipmentModal: false,
          selectedProduct: null
        }
      }
    })
    
    const item = mockOrders[0]
    wrapper.vm.confirmShipment(item)
    
    expect(wrapper.vm.showShipmentModal).toBe(true)
    expect(wrapper.vm.selectedProduct).toBe(item)
  })
  
  it('ferme le modal lors de l\'annulation', () => {
    const wrapper = mount(Orders, {
      data() {
        return {
          orders: mockOrders,
          loading: false,
          showShipmentModal: true,
          selectedProduct: mockOrders[0]
        }
      }
    })
    
    wrapper.vm.cancelShipment()
    
    expect(wrapper.vm.showShipmentModal).toBe(false)
    expect(wrapper.vm.selectedProduct).toBe(null)
  })
})