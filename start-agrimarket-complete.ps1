# Script complet pour démarrer AgriMarket
Write-Host "🌱 Démarrage complet d'AgriMarket" -ForegroundColor Green

# 1. Démarrer le backend
Write-Host "`n1. Démarrage du backend Spring Boot..." -ForegroundColor Yellow
cd multivendor
Start-Process powershell -ArgumentList "-Command", "java -jar target/multivendor-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Write-Host "⏳ Attente du démarrage du backend (10 secondes)..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# Vérifier que le backend fonctionne
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:3026/health" -Method Get
    Write-Host "✅ Backend démarré: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur backend: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 Vérifiez que le JAR est compilé: mvn clean package -DskipTests" -ForegroundColor Cyan
}

# 2. Démarrer le frontend
Write-Host "`n2. Démarrage du frontend Vue.js..." -ForegroundColor Yellow
cd ..\multivendor-frontend

# Installer les dépendances si nécessaire
if (!(Test-Path "node_modules")) {
    Write-Host "📦 Installation des dépendances..." -ForegroundColor Cyan
    npm install
}

# Démarrer le serveur de développement
Write-Host "🚀 Démarrage du serveur de développement..." -ForegroundColor Green
Write-Host "`n🌐 AgriMarket sera disponible sur:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend:  http://localhost:3026" -ForegroundColor White

Write-Host "`n📱 Pages disponibles pour les clients:" -ForegroundColor Yellow
Write-Host "   🏠 Accueil: http://localhost:3000" -ForegroundColor White
Write-Host "   📊 Dashboard: http://localhost:3000/customer" -ForegroundColor White
Write-Host "   🛒 Produits: http://localhost:3000/customer/products" -ForegroundColor White
Write-Host "   🏷️ Catégories: http://localhost:3000/customer/categories" -ForegroundColor White
Write-Host "   👨‍🌾 Fermiers: http://localhost:3000/customer/farmers" -ForegroundColor White
Write-Host "   🛍️ Panier: http://localhost:3000/customer/cart" -ForegroundColor White
Write-Host "   📦 Commandes: http://localhost:3000/customer/orders" -ForegroundColor White
Write-Host "   👤 Profil: http://localhost:3000/customer/profile" -ForegroundColor White

Write-Host "`n🔐 Pour tester l'authentification:" -ForegroundColor Yellow
Write-Host "   1. Allez sur http://localhost:3000" -ForegroundColor White
Write-Host "   2. Cliquez sur 'Connexion'" -ForegroundColor White
Write-Host "   3. Entrez un email (ex: test@example.com)" -ForegroundColor White
Write-Host "   4. Vérifiez les logs du backend pour l'OTP" -ForegroundColor White
Write-Host "   5. Entrez l'OTP pour vous connecter" -ForegroundColor White

Write-Host "`n✨ AgriMarket est prêt!" -ForegroundColor Green
npm run dev
