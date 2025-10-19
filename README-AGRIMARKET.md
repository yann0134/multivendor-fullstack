# 🌱 AgriMarket - Plateforme de Commerce Agricole

## 📋 Description

AgriMarket est une plateforme de commerce électronique spécialisée dans la vente de produits agricoles locaux. Elle connecte les fermiers et producteurs locaux avec les consommateurs, favorisant l'économie locale et la consommation de produits frais et bio.

## 🎯 Fonctionnalités Principales

### 🛒 Pour les Clients
- **Catalogue de produits** : Fruits, légumes, viandes, produits laitiers, etc.
- **Filtres avancés** : Par type (animal/végétal), catégorie, prix, origine
- **Produits spéciaux** : Bio, locaux, de saison
- **Recherche intelligente** : Par nom, catégorie, fermier
- **Panier d'achat** : Gestion des commandes
- **Authentification OTP** : Sécurisée par email

### 🏪 Pour les Fermiers/Vendeurs
- **Gestion des produits** : Ajout, modification, suppression
- **Tableau de bord** : Statistiques de vente
- **Gestion des commandes** : Suivi des commandes clients
- **Profil fermier** : Informations sur la ferme et les méthodes de production

### 📦 Pour les Fournisseurs
- **Gestion de l'inventaire** : Stock et approvisionnement
- **Suivi des commandes** : Commandes des vendeurs
- **Rapports** : Statistiques de fourniture

## 🏗️ Architecture Technique

### Backend (Spring Boot)
- **Java 17** avec Spring Boot 3.x
- **Base de données** : MySQL
- **Authentification** : JWT + OTP par email
- **API REST** : Endpoints pour tous les services
- **Sécurité** : Spring Security avec CORS

### Frontend (Vue.js 3)
- **Vue.js 3** avec Composition API
- **Vuetify 3** : Framework UI Material Design
- **Pinia** : Gestion d'état
- **Vue Router** : Navigation
- **Axios** : Appels API

## 🚀 Installation et Démarrage

### Prérequis
- **Java 17+**
- **Maven 3.6+**
- **Node.js 16+**
- **MySQL 8.0+**
- **Git**

### 1. Cloner le projet
```bash
git clone <repository-url>
cd multivendor-fullstack
```

### 2. Configuration de la base de données
1. Créer une base de données MySQL nommée `multivendor`
2. Configurer les paramètres dans `multivendor/src/main/resources/application.properties`

### 3. Démarrage rapide
```powershell
# Démarrer tout l'écosystème
.\start-all.ps1

# Ou démarrer séparément
.\start-backend.ps1    # Backend Spring Boot
.\start-frontend.ps1   # Frontend Vue.js
```

### 4. Accès aux applications
- **Frontend** : http://localhost:3000
- **Backend API** : http://localhost:3026
- **Santé API** : http://localhost:3026/health

## 📊 Structure des Données

### Catégories de Produits
- **Produits Végétaux** 🌱
  - Fruits (pommes, bananes, oranges...)
  - Légumes (tomates, carottes, épinards...)
  - Céréales (riz, blé, maïs...)
  - Légumineuses (haricots, lentilles...)
  - Herbes & Épices
  - Noix & Graines
  - Tubercules

- **Produits Animaux** 🐄
  - Viandes (bœuf, porc, volaille...)
  - Produits Laitiers (lait, fromage, yaourt...)
  - Œufs
  - Poissons
  - Miel
  - Charcuterie

### Informations Produits
- **Générales** : Nom, description, prix, stock
- **Agricoles** : Origine, méthode de culture/élevage, saison
- **Qualité** : Bio, local, conditions de stockage
- **Nutrition** : Informations nutritionnelles, allergènes
- **Dates** : Récolte, expiration

## 🔐 Authentification

### Système OTP
1. **Inscription/Connexion** : L'utilisateur saisit son email
2. **Envoi OTP** : Un code à 6 chiffres est envoyé par email
3. **Validation** : L'utilisateur saisit le code reçu
4. **Accès** : JWT généré pour l'authentification

### Rôles Utilisateurs
- **ROLE_CUSTOMER** : Client final
- **ROLE_SELLER** : Fermier/Vendeur
- **ROLE_SUPPLIER** : Fournisseur
- **ROLE_DELIVERY** : Livreur
- **ROLE_ADMIN** : Administrateur

## 📱 Interface Utilisateur

### Page d'Accueil
- **Bannière** : Message de bienvenue
- **Catégories** : Navigation par type de produit
- **Produits vedette** : Meilleurs produits
- **Nouveautés** : Derniers ajouts
- **Statistiques** : Compteurs personnels

### Catalogue Produits
- **Recherche** : Barre de recherche intelligente
- **Filtres** : Type, catégorie, prix, bio, local
- **Affichage** : Grille ou liste
- **Pagination** : Navigation par pages
- **Tri** : Par prix, nom, nouveauté, popularité

### Fiche Produit
- **Images** : Galerie de photos
- **Informations** : Description complète
- **Détails agricoles** : Origine, méthode, saison
- **Prix** : Prix de vente et remises
- **Stock** : Disponibilité
- **Actions** : Ajouter au panier

## 🛠️ API Endpoints

### Authentification
- `POST /auth/sent/login-signup-otp` - Envoyer OTP
- `POST /auth/signup` - Inscription
- `POST /auth/signing` - Connexion
- `GET /users/profile` - Profil utilisateur

### Catégories
- `GET /api/categories` - Toutes les catégories
- `GET /api/categories/type/{type}` - Par type
- `GET /api/categories/{id}/subcategories` - Sous-catégories

### Produits
- `GET /api/products` - Tous les produits (paginé)
- `GET /api/products/{id}` - Produit par ID
- `GET /api/products/category/{id}` - Par catégorie
- `GET /api/products/search` - Recherche
- `GET /api/products/featured` - Produits vedette
- `GET /api/products/new` - Nouveaux produits

### Santé
- `GET /health` - Santé de l'application
- `GET /health/database` - Santé de la base de données

## 🔧 Configuration

### Backend (application.properties)
```properties
# Base de données
spring.datasource.url=jdbc:mysql://51.195.11.202:3308/multivendor
spring.datasource.username=root
spring.datasource.password=root

# Email (désactivé en développement)
spring.mail.enabled=false

# Serveur
server.port=3026
```

### Frontend (services/api.js)
```javascript
const API_BASE_URL = 'http://localhost:3026'
```

## 📈 Développement

### Ajout de nouvelles catégories
1. Modifier `DataInitializationService.java`
2. Ajouter les sous-catégories
3. Redémarrer l'application

### Ajout de nouveaux produits
1. Utiliser l'API `/api/products` (POST)
2. Ou modifier `DemoDataService.java`
3. Redémarrer l'application

### Personnalisation de l'interface
1. Modifier les composants Vue.js dans `multivendor-frontend/src/`
2. Styles dans les sections `<style scoped>`
3. Hot reload automatique en développement

## 🐛 Dépannage

### Backend ne démarre pas
- Vérifier Java 17+ installé
- Vérifier MySQL accessible
- Vérifier les ports 3026 libre
- Consulter les logs Maven

### Frontend ne démarre pas
- Vérifier Node.js 16+ installé
- Exécuter `npm install`
- Vérifier le port 3000 libre
- Consulter les logs npm

### Problèmes de base de données
- Vérifier la connexion MySQL
- Vérifier les credentials
- Consulter les logs Spring Boot

### Problèmes d'API
- Vérifier que le backend est démarré
- Tester avec `curl http://localhost:3026/health`
- Vérifier les CORS dans le backend

## 📞 Support

Pour toute question ou problème :
1. Consulter les logs d'application
2. Vérifier la configuration
3. Tester les endpoints API
4. Consulter la documentation Spring Boot et Vue.js

## 🎉 Fonctionnalités Futures

- [ ] Paiement en ligne
- [ ] Géolocalisation des fermes
- [ ] Chat en temps réel
- [ ] Notifications push
- [ ] Application mobile
- [ ] Système de livraison
- [ ] Évaluations et avis
- [ ] Programmes de fidélité

---

**AgriMarket** - Connecter les producteurs locaux aux consommateurs 🌱
