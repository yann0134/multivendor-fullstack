# Interface de Récupération du Fournisseur

## Description
L'interface de récupération permet aux fournisseurs de gérer les produits prêts à être récupérés par l'entrepôt.

## Composants

### 1. Collection.vue
**Page principale de récupération**
- Liste des produits prêts pour la récupération
- Filtrage par nom de produit
- Actions de confirmation de récupération
- Statuts visuels avec couleurs et icônes

### 2. CollectionDetail.vue
**Page de détail d'une récupération**
- Informations complètes du produit
- Timeline de récupération
- Actions de confirmation
- Détails de la commande

### 3. CollectionStats.vue
**Composant de statistiques**
- Nombre de produits prêts
- Nombre de produits récupérés
- Statistiques en temps réel

### 4. CollectionTimeline.vue
**Timeline de récupération**
- Historique des étapes
- Dates importantes
- Statuts visuels

## Fonctionnalités

### Gestion des Statuts
- **READY_FOR_COLLECTION** : Prêt pour récupération (orange)
- **COLLECTED** : Récupéré (vert)
- **PENDING** : En attente (bleu)
- **CANCELLED** : Annulé (rouge)

### Actions Disponibles
- **Confirmer Récupération** : Marquer un produit comme récupéré
- **Voir Détails** : Accéder à la page de détail
- **Filtrer** : Rechercher par nom de produit

### Timeline de Récupération
1. **Produit créé** : Date d'ajout au système
2. **Produit approuvé** : Date de validation admin
3. **Récupération prévue** : Date de livraison (commande + 3 jours)
4. **Récupéré** : Date de confirmation de récupération

## Structure des Données

```javascript
{
  id: Number,
  title: String,
  description: String,
  collectionStatus: 'READY_FOR_COLLECTION' | 'COLLECTED' | 'PENDING' | 'CANCELLED',
  supplierPrice: Number,
  createdAt: String,
  images: Array
}
```

## Navigation

### Routes
- `/supplier/collection` : Liste des récupérations
- `/supplier/collection/:id` : Détail d'une récupération

### Intégration Dashboard
- Carte "Récupérations" avec statistiques
- Bouton "Récupérations" dans les actions rapides
- Navigation directe depuis le dashboard

## API Endpoints

### Récupération des données
- `GET /api/products/status/APPROVED/supplier` : Produits approuvés
- `GET /api/products/:id` : Détail d'un produit
- `PUT /api/products/:id/confirm-collection` : Confirmer récupération

## Tests

### Tests Unitaires
- Affichage de la liste des récupérations
- Filtrage par recherche
- Gestion des statuts
- Actions de confirmation

### Tests d'Intégration
- Navigation entre les pages
- Mise à jour des statuts
- Synchronisation avec l'API

## Utilisation

### Pour le Fournisseur
1. **Consulter** : Voir les produits prêts pour récupération
2. **Confirmer** : Marquer les produits comme récupérés
3. **Suivre** : Consulter l'historique des récupérations

### Pour l'Administrateur
1. **Surveiller** : Voir les statistiques de récupération
2. **Gérer** : Modifier les statuts si nécessaire
3. **Analyser** : Consulter les rapports de récupération
