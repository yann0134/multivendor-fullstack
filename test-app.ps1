# Test simple de l'application
Write-Host "🧪 Test de l'application Multivendor" -ForegroundColor Green

try {
    $response = Invoke-RestMethod -Uri "http://localhost:3026/health" -Method GET -TimeoutSec 5
    Write-Host "✅ Application démarrée avec succès!" -ForegroundColor Green
    Write-Host "📊 Statut: $($response.status)" -ForegroundColor Cyan
    Write-Host "🗄️ Base de données: $($response.database)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Application non accessible: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 Vérifiez que l'application est démarrée sur le port 3026" -ForegroundColor Yellow
}
