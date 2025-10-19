# Script pour démarrer le frontend avec les fonctionnalités agricoles
Write-Host "🌱 Démarrage d'AgriMarket Frontend..." -ForegroundColor Green

# Aller dans le dossier frontend
cd multivendor-frontend

# Installer les dépendances si nécessaire
if (!(Test-Path "node_modules")) {
    Write-Host "📦 Installation des dépendances..." -ForegroundColor Yellow
    npm install
}

# Démarrer le serveur de développement
Write-Host "🚀 Démarrage du serveur de développement..." -ForegroundColor Green
Write-Host "🌐 L'application sera disponible sur http://localhost:3000" -ForegroundColor Cyan
Write-Host "📱 Interface agricole complète avec:" -ForegroundColor Yellow
Write-Host "   - Page d'accueil agricole" -ForegroundColor White
Write-Host "   - Navigation par catégories" -ForegroundColor White
Write-Host "   - Liste des fermiers" -ForegroundColor White
Write-Host "   - Produits agricoles" -ForegroundColor White
Write-Host "   - Panier et commandes" -ForegroundColor White

npm run dev
