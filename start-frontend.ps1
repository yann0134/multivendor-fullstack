# Script PowerShell pour démarrer le frontend Vue.js
Write-Host "🎨 Démarrage du frontend Vue.js..." -ForegroundColor Green

# Aller dans le répertoire multivendor-frontend
Set-Location multivendor-frontend

# Vérifier que Node.js est installé
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js détecté: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    exit 1
}

# Vérifier que npm est installé
try {
    $npmVersion = npm --version
    Write-Host "✅ npm détecté: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ npm n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    exit 1
}

# Installer les dépendances si nécessaire
if (-not (Test-Path "node_modules")) {
    Write-Host "📦 Installation des dépendances..." -ForegroundColor Yellow
    npm install
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Erreur lors de l'installation des dépendances" -ForegroundColor Red
        exit 1
    }
}

# Démarrer l'application de développement
Write-Host "🌱 Démarrage de l'interface AgriMarket..." -ForegroundColor Green
Write-Host "🌐 L'interface sera disponible sur: http://localhost:3000" -ForegroundColor Cyan
Write-Host "🔗 API Backend: http://localhost:3026" -ForegroundColor Cyan
Write-Host ""
Write-Host "⏳ Démarrage en cours... (Appuyez sur Ctrl+C pour arrêter)" -ForegroundColor Yellow

npm run dev
