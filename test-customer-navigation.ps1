# Script pour tester la navigation client
Write-Host "🧪 Test de la navigation client AgriMarket" -ForegroundColor Green

# Vérifier que le backend fonctionne
Write-Host "`n1. Test du backend..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:3026/health" -Method Get
    Write-Host "✅ Backend: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Backend non accessible: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Vérifier que le frontend fonctionne
Write-Host "`n2. Test du frontend..." -ForegroundColor Yellow
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:3000" -Method Get
    if ($frontendResponse.StatusCode -eq 200) {
        Write-Host "✅ Frontend accessible sur http://localhost:3000" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Frontend non accessible: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 Démarrez le frontend avec: cd multivendor-frontend && npm run dev" -ForegroundColor Cyan
}

Write-Host "`n3. Pages disponibles pour les clients:" -ForegroundColor Yellow
Write-Host "   📊 Dashboard: http://localhost:3000/customer" -ForegroundColor White
Write-Host "   🛒 Produits: http://localhost:3000/customer/products" -ForegroundColor White
Write-Host "   🏷️ Catégories: http://localhost:3000/customer/categories" -ForegroundColor White
Write-Host "   👨‍🌾 Fermiers: http://localhost:3000/customer/farmers" -ForegroundColor White
Write-Host "   🛍️ Panier: http://localhost:3000/customer/cart" -ForegroundColor White
Write-Host "   📦 Commandes: http://localhost:3000/customer/orders" -ForegroundColor White
Write-Host "   👤 Profil: http://localhost:3000/customer/profile" -ForegroundColor White

Write-Host "`n4. Pour tester l'inscription client:" -ForegroundColor Yellow
Write-Host "   📧 Email: test@example.com" -ForegroundColor White
Write-Host "   🔐 OTP: Vérifiez les logs du backend" -ForegroundColor White

Write-Host "`n5. Navigation dans l'interface:" -ForegroundColor Yellow
Write-Host "   - Utilisez le menu hamburger (☰) pour naviguer" -ForegroundColor White
Write-Host "   - Cliquez sur les liens dans la barre latérale" -ForegroundColor White
Write-Host "   - Utilisez la barre de recherche en haut" -ForegroundColor White

Write-Host "`n✨ Test terminé!" -ForegroundColor Green
