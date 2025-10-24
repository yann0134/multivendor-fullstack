import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Collection from '../Collection.vue'

// Données de test pour les produits livrés et reçus
const mockCollections = [
  {
    id: 1,
    title: 'Tomates Bio',
    description: 'Tomates biologiques fraîches',
    shipmentStatus: 'SHIPPED',
    receptionStatus: 'RECEIVED',
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

describe('Collection.vue', () => {
  it('affiche la liste des produits livrés et reçus', () => {
    const wrapper = mount(Collection, {
      data() {
        return {
          collections: mockCollections,
          loading: false
        }
      }
    })
    
    expect(wrapper.text()).toContain('Tomates Bio')
    expect(wrapper.text()).toContain('Carottes')
  })
  
  it('filtre les produits par recherche', () => {
    const wrapper = mount(Collection, {
      data() {
        return {
          collections: mockCollections,
          loading: false,
          searchQuery: 'Tomates'
        }
      }
    })
    
    expect(wrapper.text()).toContain('Tomates Bio')
    expect(wrapper.text()).not.toContain('Carottes')
  })
  
  it('affiche les bons statuts d\'envoi et de réception', () => {
    const wrapper = mount(Collection, {
      data() {
        return {
          collections: mockCollections,
          loading: false
        }
      }
    })
    
    expect(wrapper.text()).toContain('Expédié')
    expect(wrapper.text()).toContain('Reçu')
  })
})
