import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Orders from '../Orders.vue'

describe('Orders.vue - Date Livraison', () => {
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
})