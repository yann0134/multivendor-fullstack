# Multivendor Frontend

Frontend Vue.js pour la plateforme e-commerce multivendor avec gestion multi-rôles.

## 🚀 Fonctionnalités

### Rôles Supportés
- **Client (ROLE_CUSTOMER)** : Achat de produits, gestion du panier, suivi des commandes
- **Vendeur (ROLE_SELLER)** : Gestion des produits, commandes, rapports de vente
- **Fournisseur (ROLE_SUPPLIER)** : Gestion des approvisionnements, produits en gros
- **Livreur (ROLE_DELIVERY)** : Gestion des tâches de livraison, gains
- **Entrepôt (ROLE_WAREHOUSE)** : Gestion de l'inventaire, commandes
- **Administrateur (ROLE_ADMIN)** : Gestion globale de la plateforme

### Technologies Utilisées
- **Vue.js 3** - Framework JavaScript
- **Vuetify 3** - Framework UI Material Design
- **Pinia** - Gestion d'état
- **Vue Router 4** - Routage
- **Axios** - Client HTTP
- **Vite** - Build tool

## 📁 Structure du Projet

```
src/
├── components/          # Composants réutilisables
│   ├── auth/           # Composants d'authentification
│   ├── common/         # Composants partagés
│   ├── customer/       # Composants client
│   ├── seller/         # Composants vendeur
│   ├── supplier/       # Composants fournisseur
│   ├── delivery/       # Composants livreur
│   ├── warehouse/      # Composants entrepôt
│   └── admin/          # Composants administrateur
├── views/              # Pages de l'application
│   ├── auth/           # Pages d'authentification
│   ├── customer/       # Interface client
│   ├── seller/         # Interface vendeur
│   ├── supplier/       # Interface fournisseur
│   ├── delivery/       # Interface livreur
│   ├── warehouse/      # Interface entrepôt
│   └── admin/          # Interface administrateur
├── stores/             # Stores Pinia
├── services/           # Services API
├── router/             # Configuration du routage
├── middleware/         # Middleware de sécurité
├── utils/              # Utilitaires
└── plugins/            # Plugins (Vuetify, etc.)
```

## 🛠️ Installation

```bash
# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm run dev

# Build pour la production
npm run build

# Prévisualiser le build
npm run preview
```

## 🔧 Configuration

### Variables d'environnement
Créer un fichier `.env` :
```
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Multivendor E-commerce
```

### Proxy API
Le projet est configuré pour rediriger les requêtes `/api/*` vers le backend Spring Boot sur le port 8080.

## 🎨 Interface par Rôle

### Client
- Catalogue de produits avec filtres
- Panier et commandes
- Suivi des livraisons
- Profil et adresses

### Vendeur
- Dashboard avec statistiques
- Gestion des produits
- Gestion des commandes
- Rapports de vente

### Fournisseur
- Dashboard fournisseur
- Gestion des produits en gros
- Commandes d'approvisionnement
- Rapports de performance

### Livreur
- Dashboard livreur
- Tâches de livraison
- Suivi GPS
- Gains et paiements

### Entrepôt
- Dashboard entrepôt
- Gestion de l'inventaire
- Traitement des commandes
- Rapports de stock

### Administrateur
- Dashboard global
- Gestion des utilisateurs
- Analytics de la plateforme
- Paramètres système

## 🔐 Sécurité

- Authentification JWT
- Gardes de navigation par rôle
- Intercepteurs HTTP pour les tokens
- Gestion automatique des erreurs 401

## 📱 Responsive Design

L'interface s'adapte automatiquement aux différentes tailles d'écran :
- Mobile (< 600px)
- Tablette (600px - 960px)
- Desktop (> 960px)

## 🚀 Déploiement

```bash
# Build de production
npm run build

# Les fichiers générés sont dans le dossier dist/
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT.
