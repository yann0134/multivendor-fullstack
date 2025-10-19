# Script de test pour le backend
Write-Host "🔍 Test du backend AgriMarket..." -ForegroundColor Green

# Test de l'API de santé
Write-Host "`n1. Test de l'API de santé..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:3026/health" -Method GET -TimeoutSec 5
    Write-Host "✅ API de santé: $($health.status)" -ForegroundColor Green
    $health | ConvertTo-Json
} catch {
    Write-Host "❌ API de santé: $($_.Exception.Message)" -ForegroundColor Red
}

# Test de l'API des catégories
Write-Host "`n2. Test de l'API des catégories..." -ForegroundColor Yellow
try {
    $categories = Invoke-RestMethod -Uri "http://localhost:3026/api/categories" -Method GET -TimeoutSec 5
    Write-Host "✅ API catégories: $($categories.Count) catégories trouvées" -ForegroundColor Green
    $categories | ConvertTo-Json -Depth 2
} catch {
    Write-Host "❌ API catégories: $($_.Exception.Message)" -ForegroundColor Red
}

# Test de l'API des produits
Write-Host "`n3. Test de l'API des produits..." -ForegroundColor Yellow
try {
    $products = Invoke-RestMethod -Uri "http://localhost:3026/api/products" -Method GET -TimeoutSec 5
    Write-Host "✅ API produits: $($products.content.Count) produits trouvés" -ForegroundColor Green
    $products | ConvertTo-Json -Depth 2
} catch {
    Write-Host "❌ API produits: $($_.Exception.Message)" -ForegroundColor Red
}

# Test de l'endpoint d'authentification
Write-Host "`n4. Test de l'endpoint d'authentification..." -ForegroundColor Yellow
try {
    $body = @{
        email = "test@example.com"
        role = "ROLE_CUSTOMER"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:3026/auth/sent/login-signup-otp" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 5
    Write-Host "✅ API auth: $($response.message)" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ API auth: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Tests terminés!" -ForegroundColor Green
