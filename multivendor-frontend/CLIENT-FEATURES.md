# Fonctionnalités Client - Multivendor E-commerce

## 🎯 Vue d'ensemble

Ce document décrit les fonctionnalités implémentées pour l'interface client de l'application multivendor e-commerce. Toutes les fonctionnalités sont conçues pour afficher uniquement les produits ayant le statut "RECEIVED_BY_WAREHOUSE" (reçus par l'entrepôt).

## 🛍️ Fonctionnalités Principales

### 1. **Dashboard Client** (`/customer`)
- **Vue d'ensemble** : Page d'accueil personnalisée avec statistiques
- **Produits en vedette** : Affichage des produits populaires (filtrés par statut)
- **Nouveaux produits** : Derniers produits ajoutés (filtrés par statut)
- **Catégories** : Navigation rapide vers les catégories
- **Statistiques personnelles** : Nombre de produits vus, commandes, avis
- **Actions rapides** : Liens vers les principales fonctionnalités

### 2. **Catalogue Produits** (`/customer/products`)
- **Recherche intelligente** : Barre de recherche avec filtres avancés
- **Filtres disponibles** :
  - Type de produit (Végétal/Animal)
  - Catégorie et sous-catégorie
  - Produits bio et locaux
  - Plage de prix
  - Tri (nouveautés, prix, popularité)
- **Affichage** : Mode grille ou liste
- **Pagination** : Navigation par pages
- **Images par défaut** : Images automatiques selon le type de produit

### 3. **Détail Produit** (`/customer/product/:id`)
- **Galerie d'images** : Affichage des images avec lightbox
- **Informations complètes** : Description, prix, stock, origine
- **Caractéristiques agricoles** : Méthode de culture, saison, poids
- **Gestion du panier** : Ajout avec sélection de taille/couleur
- **Avis clients** : Système de notation et commentaires
- **Stock intelligent** : Affichage de la disponibilité

### 4. **Panier** (`/customer/cart`)
- **Gestion des articles** : Ajout, modification, suppression
- **Calcul automatique** : Sous-total, remises, total
- **Codes promo** : Application de coupons de réduction
- **Validation** : Vérification de la disponibilité des produits

### 5. **Commande** (`/customer/checkout`)
- **Processus en 3 étapes** :
  1. Adresse de livraison
  2. Méthode de paiement (Stripe/Razorpay)
  3. Confirmation
- **Validation** : Vérification des données
- **Paiement sécurisé** : Intégration avec les passerelles de paiement

### 6. **Historique des Commandes** (`/customer/orders`)
- **Liste des commandes** : Historique complet avec statuts
- **Détails de commande** : Informations détaillées par commande
- **Suivi** : Timeline de l'état de la commande
- **Actions** : Annulation (si possible)

### 7. **Catégories** (`/customer/categories`)
- **Navigation par catégories** : Organisation des produits
- **Sous-catégories** : Classification fine des produits
- **Produits par catégorie** : Affichage filtré des produits

### 8. **Agriculteurs** (`/customer/farmers`)
- **Profils des producteurs** : Informations sur les fournisseurs
- **Spécialités** : Domaines d'expertise de chaque agriculteur
- **Produits par agriculteur** : Catalogue spécifique à chaque producteur
- **Système de favoris** : Sauvegarde des agriculteurs préférés

### 9. **Profil Client** (`/customer/profile`)
- **Informations personnelles** : Gestion du profil
- **Adresses** : Gestion des adresses de livraison
- **Statistiques** : Données personnelles d'utilisation
- **Préférences** : Configuration des notifications et affichage

## 🔧 Fonctionnalités Techniques

### **Filtrage des Produits**
- **Statut requis** : Seuls les produits avec le statut `RECEIVED_BY_WAREHOUSE` sont affichés
- **Filtrage automatique** : Appliqué à tous les endpoints de produits
- **Performance** : Optimisation des requêtes avec pagination

### **Images par Défaut**
- **Configuration intelligente** : Images selon le type et la catégorie
- **Types supportés** :
  - Végétal : Fruits, légumes, céréales, épices
  - Animal : Viande, poisson, lait, œufs
- **Fallback** : Image par défaut générale si aucune correspondance

### **Gestion d'État**
- **Stores Pinia** : Gestion centralisée de l'état
- **Réactivité** : Mise à jour automatique de l'interface
- **Persistance** : Sauvegarde des préférences utilisateur

### **API Integration**
- **Endpoints complets** : Tous les endpoints backend implémentés
- **Gestion d'erreurs** : Traitement des erreurs avec messages utilisateur
- **Authentification** : JWT avec refresh automatique

## 📱 Interface Utilisateur

### **Design System**
- **Vuetify 3** : Composants modernes et accessibles
- **Responsive** : Adaptation mobile/tablette/desktop
- **Thème** : Couleurs cohérentes avec l'identité agricole
- **Icons** : Material Design Icons pour la cohérence

### **Expérience Utilisateur**
- **Navigation intuitive** : Menu clair et logique
- **Feedback visuel** : Loading states, animations, notifications
- **Accessibilité** : Support des lecteurs d'écran
- **Performance** : Chargement optimisé des images

## 🚀 Endpoints Implémentés

### **Authentification**
- `POST /auth/sent/login-signup-otp` - Envoi OTP
- `POST /auth/signup` - Inscription
- `POST /auth/signing` - Connexion
- `GET /users/profile` - Profil utilisateur

### **Produits**
- `GET /api/products` - Liste avec filtrage par statut
- `GET /api/products/{id}` - Détail produit
- `GET /api/products/category/{id}` - Par catégorie
- `GET /api/products/search` - Recherche
- `GET /api/products/featured` - Produits vedette
- `GET /api/products/new` - Nouveaux produits
- `GET /api/products/organic` - Produits bio
- `GET /api/products/local` - Produits locaux

### **Panier**
- `GET /api/cart` - Récupérer le panier
- `PUT /api/cart/add` - Ajouter un produit
- `PUT /api/cart/item/{id}` - Modifier quantité
- `DELETE /api/cart/item/{id}` - Supprimer article

### **Commandes**
- `POST /api/orders` - Créer commande
- `GET /api/orders/user` - Historique client
- `GET /api/orders/{id}` - Détail commande
- `PUT /api/orders/{id}/cancel` - Annuler commande

### **Avis**
- `GET /api/products/{id}/reviews` - Avis d'un produit
- `POST /api/products/{id}/reviews` - Créer avis
- `PATCH /api/reviews/{id}` - Modifier avis
- `DELETE /api/reviews/{id}` - Supprimer avis

### **Catégories**
- `GET /api/categories` - Toutes les catégories
- `GET /api/categories/{id}/subcategories` - Sous-catégories

## 🎨 Images par Défaut

### **Configuration**
- **Fichier** : `src/utils/defaultImages.js`
- **Types supportés** : Végétal, Animal
- **Catégories** : Fruits, légumes, céréales, épices, viande, poisson, lait, œufs
- **Sources** : Unsplash avec paramètres optimisés

### **Utilisation**
```javascript
import { getDefaultProductImage } from '@/utils/defaultImages'

// Obtenir une image par défaut pour un produit
const imageUrl = getDefaultProductImage(product)
```

## 🔒 Sécurité

### **Authentification**
- **JWT** : Tokens sécurisés avec expiration
- **Rôles** : Contrôle d'accès basé sur les rôles
- **Refresh** : Renouvellement automatique des tokens

### **Validation**
- **Frontend** : Validation des formulaires
- **Backend** : Validation des données côté serveur
- **Sanitisation** : Nettoyage des entrées utilisateur

## 📊 Performance

### **Optimisations**
- **Lazy Loading** : Chargement différé des images
- **Pagination** : Limitation du nombre d'éléments
- **Cache** : Mise en cache des données fréquentes
- **Compression** : Images optimisées

### **Métriques**
- **Temps de chargement** : < 2s pour les pages principales
- **Images** : Compression automatique
- **Bundle** : Code splitting pour réduire la taille

## 🧪 Tests

### **Couverture**
- **Composants** : Tests unitaires des composants Vue
- **Stores** : Tests des stores Pinia
- **Services** : Tests des services API
- **E2E** : Tests end-to-end des parcours utilisateur

### **Outils**
- **Vitest** : Framework de test
- **Vue Test Utils** : Utilitaires de test Vue
- **Cypress** : Tests E2E

## 🚀 Déploiement

### **Build**
```bash
npm run build
```

### **Variables d'environnement**
```env
VITE_API_BASE_URL=http://localhost:3026
VITE_APP_NAME=AgriMarket
```

### **Production**
- **Serveur** : Nginx avec compression
- **CDN** : Images servies via CDN
- **SSL** : Certificats HTTPS
- **Monitoring** : Surveillance des performances

## 📝 Notes de Développement

### **Architecture**
- **Vue 3** : Composition API
- **Pinia** : Gestion d'état
- **Vuetify 3** : UI Framework
- **Vue Router** : Navigation

### **Structure**
```
src/
├── components/customer/     # Composants client
├── views/customer/          # Vues client
├── stores/                  # Stores Pinia
├── services/                # Services API
├── utils/                   # Utilitaires
└── router/                  # Configuration des routes
```

### **Conventions**
- **Nommage** : PascalCase pour les composants
- **Fichiers** : kebab-case pour les fichiers
- **Variables** : camelCase
- **Constantes** : UPPER_SNAKE_CASE

## 🔄 Mises à Jour Futures

### **Fonctionnalités prévues**
- **Notifications push** : Alertes en temps réel
- **Chat support** : Assistance client
- **Recommandations** : IA pour suggestions
- **Gamification** : Système de points et badges

### **Améliorations techniques**
- **PWA** : Application web progressive
- **Offline** : Mode hors ligne
- **Internationalisation** : Support multilingue
- **Accessibilité** : Amélioration WCAG

---

*Ce document est maintenu à jour avec les évolutions de l'application.*
