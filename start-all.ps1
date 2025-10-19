# Script PowerShell pour démarrer l'application complète AgriMarket
Write-Host "🌱 Démarrage d'AgriMarket - Plateforme de Commerce Agricole" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Green

# Vérifier les prérequis
Write-Host "🔍 Vérification des prérequis..." -ForegroundColor Yellow

# Vérifier Java
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java détecté" -ForegroundColor Green
} catch {
    Write-Host "❌ Java n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "💡 Installez Java 17 ou plus récent" -ForegroundColor Yellow
    exit 1
}

# Vérifier Maven
try {
    $mavenVersion = mvn -version
    Write-Host "✅ Maven détecté" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "💡 Installez Apache Maven" -ForegroundColor Yellow
    exit 1
}

# Vérifier Node.js
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js détecté: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "💡 Installez Node.js 16 ou plus récent" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "🚀 Démarrage des services..." -ForegroundColor Green

# Démarrer le backend en arrière-plan
Write-Host "📡 Démarrage du backend Spring Boot..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-File", "start-backend.ps1" -WindowStyle Minimized

# Attendre un peu pour que le backend démarre
Write-Host "⏳ Attente du démarrage du backend (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier que le backend est accessible
Write-Host "🔍 Vérification du backend..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:3026/health" -Method GET -TimeoutSec 10
    Write-Host "✅ Backend opérationnel: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Backend pas encore prêt, démarrage du frontend quand même..." -ForegroundColor Yellow
}

# Démarrer le frontend
Write-Host "🎨 Démarrage du frontend Vue.js..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-File", "start-frontend.ps1" -WindowStyle Normal

Write-Host ""
Write-Host "🎉 AgriMarket est en cours de démarrage!" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Green
Write-Host "🌐 Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "📡 Backend API: http://localhost:3026" -ForegroundColor Cyan
Write-Host "🔍 Santé API: http://localhost:3026/health" -ForegroundColor Cyan
Write-Host "📚 Catégories API: http://localhost:3026/api/categories" -ForegroundColor Cyan
Write-Host "🛒 Produits API: http://localhost:3026/api/products" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 Fonctionnalités disponibles:" -ForegroundColor Yellow
Write-Host "   🌱 Produits agricoles (animaux et végétaux)" -ForegroundColor White
Write-Host "   🏪 Gestion des fermiers/vendeurs" -ForegroundColor White
Write-Host "   🛒 Panier d'achat" -ForegroundColor White
Write-Host "   📦 Système de commandes" -ForegroundColor White
Write-Host "   🔐 Authentification OTP" -ForegroundColor White
Write-Host "   📱 Interface responsive" -ForegroundColor White
Write-Host ""
Write-Host "⏹️ Pour arrêter les services, fermez les fenêtres PowerShell" -ForegroundColor Red
Write-Host ""

# Attendre une entrée utilisateur
Read-Host "Appuyez sur Entrée pour continuer..."
