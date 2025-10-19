# Script PowerShell pour tester l'API d'authentification
Write-Host "🧪 Test de l'API d'authentification Multivendor" -ForegroundColor Green

$baseUrl = "http://localhost:3026"

# Test 1: Vérifier la santé de l'application
Write-Host "`n1️⃣ Test de santé de l'application..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/health" -Method GET
    Write-Host "✅ Application: $($healthResponse.status)" -ForegroundColor Green
    Write-Host "✅ Base de données: $($healthResponse.database)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur de santé: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Envoyer un OTP pour inscription
Write-Host "`n2️⃣ Test d'envoi d'OTP pour inscription..." -ForegroundColor Yellow
$otpRequest = @{
    email = "test@example.com"
    role = "ROLE_CUSTOMER"
} | ConvertTo-Json

try {
    $otpResponse = Invoke-RestMethod -Uri "$baseUrl/auth/sent/login-signup-otp" -Method POST -Body $otpRequest -ContentType "application/json"
    Write-Host "✅ OTP envoyé: $($otpResponse.message)" -ForegroundColor Green
    Write-Host "📝 Vérifiez les logs pour voir le code OTP généré" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erreur OTP: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test d'inscription avec OTP (utilisez le code OTP des logs)
Write-Host "`n3️⃣ Test d'inscription avec OTP..." -ForegroundColor Yellow
Write-Host "📝 Utilisez le code OTP affiché dans les logs de l'application" -ForegroundColor Cyan

$signupRequest = @{
    email = "test@example.com"
    fullName = "Test User"
    otp = "123456"  # Remplacez par le code OTP des logs
} | ConvertTo-Json

try {
    $signupResponse = Invoke-RestMethod -Uri "$baseUrl/auth/signup" -Method POST -Body $signupRequest -ContentType "application/json"
    Write-Host "✅ Inscription réussie: $($signupResponse.message)" -ForegroundColor Green
    Write-Host "🔑 Token JWT reçu" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur inscription: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n✅ Tests terminés" -ForegroundColor Green
