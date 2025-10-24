# Interface des Produits Livrés et Reçus

## Description
L'interface permet aux fournisseurs de visualiser les produits qu'ils ont expédiés et qui ont été acceptés par l'entrepôt.

## Composants

### 1. Collection.vue
**Page principale des produits livrés et reçus**
- Liste des produits expédiés et acceptés par l'entrepôt
- Filtrage par nom de produit
- Affichage des statuts d'envoi et de réception
- Statuts visuels avec couleurs et icônes

### 2. CollectionDetail.vue
**Page de détail d'une récupération**
- Informations complètes du produit
- Timeline de récupération
- Actions de confirmation
- Détails de la commande

### 3. CollectionStats.vue
**Composant de statistiques**
- Nombre de produits livrés et reçus
- Nombre de produits expédiés
- Nombre de produits non expédiés
- Statistiques en temps réel

### 4. CollectionTimeline.vue
**Timeline de récupération**
- Historique des étapes
- Dates importantes
- Statuts visuels

## Fonctionnalités

### Gestion des Statuts

#### Statuts d'Envoi
- **NOT_SHIPPED** : Non expédié (orange)
- **SHIPPED** : Expédié (vert)
- **IN_TRANSIT** : En transit (bleu)
- **DELIVERED** : Livré (vert)

#### Statuts de Réception
- **PENDING** : En attente (orange)
- **RECEIVED** : Reçu (vert)
- **REJECTED** : Rejeté (rouge)
- **PROCESSING** : En traitement (bleu)

### Actions Disponibles
- **Voir Détails** : Accéder à la page de détail
- **Filtrer** : Rechercher par nom de produit
- **Consulter** : Voir les statuts d'envoi et de réception

### Timeline de Livraison
1. **Produit créé** : Date d'ajout au système
2. **Produit approuvé** : Date de validation admin
3. **Produit expédié** : Date d'envoi par le fournisseur
4. **Produit reçu** : Date d'acceptation par l'entrepôt

## Structure des Données

```javascript
{
  id: Number,
  title: String,
  description: String,
  shipmentStatus: 'NOT_SHIPPED' | 'SHIPPED' | 'IN_TRANSIT' | 'DELIVERED',
  receptionStatus: 'PENDING' | 'RECEIVED' | 'REJECTED' | 'PROCESSING',
  supplierPrice: Number,
  createdAt: String,
  images: Array
}
```

## Navigation

### Routes
- `/supplier/collection` : Liste des produits livrés et reçus
- `/supplier/collection/:id` : Détail d'un produit

### Intégration Dashboard
- Carte "Récupérations" avec statistiques
- Bouton "Récupérations" dans les actions rapides
- Navigation directe depuis le dashboard

## API Endpoints

### Récupération des données
- `GET /api/products/delivered-and-received/supplier` : Produits livrés et reçus
- `GET /api/products/:id` : Détail d'un produit

## Tests

### Tests Unitaires
- Affichage de la liste des produits livrés et reçus
- Filtrage par recherche
- Gestion des statuts d'envoi et de réception
- Affichage des informations de livraison

### Tests d'Intégration
- Navigation entre les pages
- Affichage des statuts d'envoi et de réception
- Synchronisation avec l'API

## Utilisation

### Pour le Fournisseur
1. **Consulter** : Voir les produits livrés et reçus
2. **Suivre** : Consulter les statuts d'envoi et de réception
3. **Analyser** : Voir l'historique des livraisons

### Pour l'Administrateur
1. **Surveiller** : Voir les statistiques de livraison
2. **Gérer** : Modifier les statuts si nécessaire
3. **Analyser** : Consulter les rapports de livraison
