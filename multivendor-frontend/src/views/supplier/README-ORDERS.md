# Interface Mes Commandes - Gestion des Expéditions

## Description
L'interface "Mes Commandes" permet aux fournisseurs de gérer leurs produits approuvés et de les marquer comme expédiés.

## Fonctionnalités

### 📦 Gestion des Produits
- **Affichage** : Liste des produits approuvés
- **Recherche** : Filtrage par nom de produit
- **Statuts** : Visualisation des statuts d'envoi et de réception
- **Actions** : Bouton d'expédition pour les produits non expédiés

### 🚚 Expédition des Produits

#### Bouton "Expédier"
- **Condition** : Visible uniquement pour les produits avec `shipmentStatus = 'NOT_SHIPPED'`
- **Action** : Change le statut à `'SHIPPED'`
- **Confirmation** : Demande de confirmation avant l'expédition
- **Endpoint** : `PUT /api/supply-orders/{supplyOrderId}/shipment-status`

#### Statuts d'Expédition
- **NOT_SHIPPED** : Non expédié (bouton "Expédier" visible)
- **SHIPPED** : Expédié (bouton "Expédié" désactivé)
- **DELIVERED** : Livré (bouton "Livré" désactivé)

### 🔄 Flux de Travail

1. **Produit Approuvé** : Le produit apparaît dans la liste avec le statut "Non expédié"
2. **Préparation** : Le fournisseur prépare le produit
3. **Expédition** : Clic sur "Expédier" → Confirmation → Statut changé à "Expédié"
4. **Suivi** : Le produit apparaît dans l'interface de récupération

## Structure des Données

### Produit
```javascript
{
  id: Number,
  title: String,
  description: String,
  shipmentStatus: 'NOT_SHIPPED' | 'SHIPPED' | 'DELIVERED',
  receptionStatus: 'PENDING' | 'RECEIVED' | 'REJECTED',
  supplierPrice: Number,
  createdAt: String,
  images: Array
}
```

### Requête d'Expédition
```javascript
PUT /api/supply-orders/{supplyOrderId}/shipment-status
{
  "status": "SHIPPED"
}
```

## Interface Utilisateur

### Colonnes du Tableau
- **Produit** : Nom, description, image
- **Quantité** : Affichage avec composant QuantityDisplay
- **Statut Envoi** : Badge coloré avec icône
- **Statut Réception** : Badge coloré avec icône
- **Prix Fournisseur** : Formaté en XOF
- **Date Commande** : Format français
- **Date Livraison** : Calculée (commande + 3 jours)
- **Actions** : Boutons d'expédition

### Modal de Confirmation
- **Titre** : "Confirmer l'Expédition" avec icône truck-delivery
- **Contenu** : 
  - Alerte d'information
  - Carte du produit avec image, nom, description et prix
  - Alerte d'avertissement
- **Actions** :
  - Bouton "Annuler" (gris, text)
  - Bouton "Confirmer l'Expédition" (vert, flat) avec loading

### Boutons d'Action

#### Bouton "Expédier"
- **Couleur** : Vert (success)
- **Icône** : mdi-truck-delivery
- **Condition** : `shipmentStatus === 'NOT_SHIPPED'`
- **Action** : Confirmation → Appel API → Mise à jour du statut

#### Bouton "Expédié"
- **Couleur** : Bleu (info)
- **Icône** : mdi-check-circle
- **Condition** : `shipmentStatus === 'SHIPPED'`
- **État** : Désactivé (lecture seule)

#### Bouton "Livré"
- **Couleur** : Vert (success)
- **Icône** : mdi-truck-check
- **Condition** : `shipmentStatus === 'DELIVERED'`
- **État** : Désactivé (lecture seule)

## Gestion des Erreurs

### Modal de Confirmation d'Expédition
```javascript
// Variables réactives pour le modal
const showShipmentModal = ref(false)
const selectedProduct = ref(null)

// Ouvrir le modal de confirmation
const confirmShipment = (item) => {
  selectedProduct.value = item
  showShipmentModal.value = true
}

// Annuler l'expédition
const cancelShipment = () => {
  showShipmentModal.value = false
  selectedProduct.value = null
}

// Confirmer l'expédition
const confirmShipmentAction = () => {
  if (selectedProduct.value) {
    updateShipmentStatus(selectedProduct.value.id, 'SHIPPED')
    showShipmentModal.value = false
    selectedProduct.value = null
  }
}
```

### Gestion des Erreurs API
- **Succès** : Notification de confirmation
- **Erreur** : Affichage du message d'erreur
- **Loading** : Indicateur de chargement pendant la requête

## Tests

### Tests Unitaires
- Affichage des boutons selon le statut
- Confirmation avant expédition
- Gestion des erreurs
- Calcul des dates de livraison

### Tests d'Intégration
- Appel API d'expédition
- Mise à jour de l'interface
- Synchronisation des statuts

## Utilisation

### Pour le Fournisseur
1. **Consulter** : Voir les produits approuvés
2. **Préparer** : Organiser les produits à expédier
3. **Expédier** : Marquer comme expédié via le bouton
4. **Suivre** : Voir les statuts dans l'interface de récupération

### Workflow Complet
1. **Produit créé** → Statut : `NOT_SHIPPED`
2. **Produit approuvé** → Visible dans "Mes Commandes"
3. **Fournisseur expédie** → Statut : `SHIPPED`
4. **Entrepôt reçoit** → Statut : `DELIVERED`
5. **Produit visible** → Dans l'interface de récupération

## API Endpoints

### Récupération des Données
- `GET /api/supply-orders/supplier/expedier` : Produits expédiés par le fournisseur

### Mise à Jour des Statuts
- `PUT /api/products/{productId}/shipment-status` : Changer le statut d'expédition d'un produit

## Notifications

### Succès
- ✅ "Produit expédié avec succès !"

### Erreur
- ❌ "Erreur lors de l'expédition: [message d'erreur]"

## Sécurité

### Autorisation
- Endpoint protégé par `ROLE_SUPPLIER`
- Vérification de l'identité du fournisseur
- Validation des permissions

### Validation
- Confirmation obligatoire avant expédition
- Vérification du statut actuel
- Gestion des erreurs de réseau
