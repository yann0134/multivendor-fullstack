# Script PowerShell pour démarrer le backend Spring Boot
Write-Host "🚀 Démarrage du backend Spring Boot..." -ForegroundColor Green

# Aller dans le répertoire multivendor
Set-Location multivendor

# Vérifier que Maven est installé
try {
    $mavenVersion = mvn -version
    Write-Host "✅ Maven détecté" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    exit 1
}

# Nettoyer et compiler le projet
Write-Host "🧹 Nettoyage et compilation du projet..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors de la compilation" -ForegroundColor Red
    exit 1
}

# Démarrer l'application Spring Boot
Write-Host "🌱 Démarrage de l'application AgriMarket..." -ForegroundColor Green
Write-Host "📊 L'application sera disponible sur: http://localhost:3026" -ForegroundColor Cyan
Write-Host "🔍 API de santé: http://localhost:3026/health" -ForegroundColor Cyan
Write-Host "📚 API des catégories: http://localhost:3026/api/categories" -ForegroundColor Cyan
Write-Host "🛒 API des produits: http://localhost:3026/api/products" -ForegroundColor Cyan
Write-Host ""
Write-Host "⏳ Démarrage en cours... (Appuyez sur Ctrl+C pour arrêter)" -ForegroundColor Yellow

mvn spring-boot:run
