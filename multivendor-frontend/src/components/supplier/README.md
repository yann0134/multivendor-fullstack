# Composant QuantityDisplay

## Description
Le composant `QuantityDisplay` affiche de manière simplifiée la quantité de stock pour les produits, en respectant la priorité admin/fournisseur.

## Fonctionnalités

### Affichage simplifié
- **Quantité unique** : Affiche uniquement la quantité pertinente (admin ou fournisseur)
- **Priorité admin** : Si `adminRequestedQuantity > 0`, affiche cette quantité
- **Fallback fournisseur** : Si admin = 0, affiche `supplierAvailableQuantity`
- **Indicateurs visuels** : Couleurs et icônes distinctes pour identifier la source

## Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `product` | Object | required | Objet produit contenant les informations de quantité |

## Structure des données produit

```javascript
{
  id: Number,
  adminRequestedQuantity: Number,    // Quantité demandée par l'admin
  supplierAvailableQuantity: Number  // Quantité disponible chez le fournisseur
}
```

## Logique de priorité

1. **Admin en priorité** : Si `adminRequestedQuantity > 0`, affiche cette quantité (orange)
2. **Fournisseur en fallback** : Si admin = 0, affiche `supplierAvailableQuantity` (bleu)
3. **Aucune quantité** : Si aucune quantité disponible, affiche "Aucune quantité" (gris)

## Exemple d'utilisation

```vue
<QuantityDisplay :product="product" />
```

## Affichage

- **Quantité admin** : Icône orange `mdi-account-tie` + quantité + "unités"
- **Quantité fournisseur** : Icône bleue `mdi-account` + quantité + "unités"
- **Aucune quantité** : Icône grise `mdi-alert-circle` + "Aucune quantité"

## Tests

Le composant inclut des tests unitaires couvrant :
- Affichage de la quantité admin
- Affichage de la quantité fournisseur
- Gestion du cas "aucune quantité"
