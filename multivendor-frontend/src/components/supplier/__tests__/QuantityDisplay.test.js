import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import QuantityDisplay from '../QuantityDisplay.vue'

describe('QuantityDisplay - Affichage Simplifié', () => {
  it('affiche uniquement la quantité admin si > 0', () => {
    const product = {
      id: 1,
      adminRequestedQuantity: 50,
      supplierAvailableQuantity: 100
    }
    
    const wrapper = mount(QuantityDisplay, {
      props: {
        product
      }
    })
    
    // Doit afficher uniquement la quantité admin
    expect(wrapper.text()).toContain('50')
    expect(wrapper.text()).toContain('unités')
    expect(wrapper.text()).not.toContain('Fournisseur:')
  })
  
  it('affiche uniquement la quantité fournisseur si admin = 0', () => {
    const product = {
      id: 1,
      adminRequestedQuantity: 0,
      supplierAvailableQuantity: 100
    }
    
    const wrapper = mount(QuantityDisplay, {
      props: {
        product
      }
    })
    
    // Doit afficher uniquement la quantité fournisseur
    expect(wrapper.text()).toContain('100')
    expect(wrapper.text()).toContain('unités')
    expect(wrapper.text()).not.toContain('Admin:')
  })
  
  it('affiche "Aucune quantité" si aucune quantité disponible', () => {
    const product = {
      id: 1,
      adminRequestedQuantity: 0,
      supplierAvailableQuantity: 0
    }
    
    const wrapper = mount(QuantityDisplay, {
      props: {
        product
      }
    })
    
    // Doit afficher le message d'erreur
    expect(wrapper.text()).toContain('Aucune quantité')
  })
})