// Configuration des images par défaut pour les produits

export const DEFAULT_IMAGES = {
  // Images par défaut pour les produits végétaux
  VEGETAL: {
    FRUITS: 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&h=600&fit=crop',
    LEGUMES: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&h=600&fit=crop',
    CEREALES: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=800&h=600&fit=crop',
    EPICES: 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=800&h=600&fit=crop',
    DEFAULT: 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&h=600&fit=crop'
  },
  
  // Images par défaut pour les produits animaux
  ANIMAL: {
    VIANDE: 'https://images.unsplash.com/photo-1548550023-8bdb78b265c3?w=800&h=600&fit=crop',
    POISSON: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800&h=600&fit=crop',
    LAIT: 'https://images.unsplash.com/photo-1550583724-b2696b85b150?w=800&h=600&fit=crop',
    OEUFS: 'https://images.unsplash.com/photo-1518569656558-1f25e69d93d3?w=800&h=600&fit=crop',
    DEFAULT: 'https://images.unsplash.com/photo-1548550023-8bdb78b265c3?w=800&h=600&fit=crop'
  },
  
  // Image par défaut générale
  DEFAULT: 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&h=600&fit=crop'
}

// Fonction pour obtenir une image par défaut selon le type et la catégorie du produit
export const getDefaultProductImage = (product) => {
  if (!product) return DEFAULT_IMAGES.DEFAULT
  
  const productType = product.category?.type
  const categoryName = product.category?.name?.toLowerCase()
  
  if (productType === 'VEGETAL') {
    if (categoryName?.includes('fruit')) return DEFAULT_IMAGES.VEGETAL.FRUITS
    if (categoryName?.includes('légume') || categoryName?.includes('legume')) return DEFAULT_IMAGES.VEGETAL.LEGUMES
    if (categoryName?.includes('céréale') || categoryName?.includes('cereale')) return DEFAULT_IMAGES.VEGETAL.CEREALES
    if (categoryName?.includes('épice') || categoryName?.includes('epice')) return DEFAULT_IMAGES.VEGETAL.EPICES
    return DEFAULT_IMAGES.VEGETAL.DEFAULT
  }
  
  if (productType === 'ANIMAL') {
    if (categoryName?.includes('viande')) return DEFAULT_IMAGES.ANIMAL.VIANDE
    if (categoryName?.includes('poisson') || categoryName?.includes('poisson')) return DEFAULT_IMAGES.ANIMAL.POISSON
    if (categoryName?.includes('lait') || categoryName?.includes('laitier')) return DEFAULT_IMAGES.ANIMAL.LAIT
    if (categoryName?.includes('œuf') || categoryName?.includes('oeuf')) return DEFAULT_IMAGES.ANIMAL.OEUFS
    return DEFAULT_IMAGES.ANIMAL.DEFAULT
  }
  
  return DEFAULT_IMAGES.DEFAULT
}

// Fonction pour obtenir une image par défaut pour les agriculteurs
export const getDefaultFarmerImage = () => {
  return 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=300&fit=crop'
}

// Fonction pour obtenir une image par défaut pour les catégories
export const getDefaultCategoryImage = (categoryType) => {
  if (categoryType === 'VEGETAL') {
    return 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop'
  } else if (categoryType === 'ANIMAL') {
    return 'https://images.unsplash.com/photo-1548550023-8bdb78b265c3?w=400&h=300&fit=crop'
  }
  return 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop'
}
