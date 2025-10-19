# Test simple de l'endpoint d'authentification
Write-Host "🔍 Test de l'endpoint d'authentification..." -ForegroundColor Green

# Test 1: Vérifier si le serveur répond
Write-Host "`n1. Test de connectivité..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3026/health" -Method GET -TimeoutSec 5
    Write-Host "✅ Serveur accessible (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "❌ Serveur non accessible: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Test de l'endpoint OTP
Write-Host "`n2. Test de l'endpoint OTP..." -ForegroundColor Yellow
$body = @{
    email = "test@example.com"
    role = "ROLE_CUSTOMER"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:3026/auth/sent/login-signup-otp" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "✅ OTP envoyé: $($response.message)" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur OTP: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
        Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Yellow
    }
}

# Test 3: Test de l'endpoint de connexion (qui donne l'erreur 403)
Write-Host "`n3. Test de l'endpoint de connexion..." -ForegroundColor Yellow
$loginBody = @{
    email = "test@example.com"
    otp = "123456"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:3026/auth/signing" -Method POST -Body $loginBody -ContentType "application/json" -TimeoutSec 10
    Write-Host "✅ Connexion réussie: $($response.message)" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur de connexion: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
        Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Yellow
    }
}

Write-Host "`n🎉 Tests terminés!" -ForegroundColor Green
