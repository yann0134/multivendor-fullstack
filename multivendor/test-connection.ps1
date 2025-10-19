# Script PowerShell de test de connectivité MySQL
Write-Host "🔍 Test de connectivité MySQL..." -ForegroundColor Green

# Test de ping du serveur
Write-Host "📡 Test de ping vers 51.195.11.202..." -ForegroundColor Yellow
Test-NetConnection -ComputerName 51.195.11.202 -Port 3308

Write-Host ""
Write-Host "🔌 Test de connectivité TCP sur le port 3308..." -ForegroundColor Yellow
$tcpClient = New-Object System.Net.Sockets.TcpClient
try {
    $tcpClient.Connect("51.195.11.202", 3308)
    Write-Host "✅ Connexion TCP réussie" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec de la connexion TCP: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    $tcpClient.Close()
}

Write-Host ""
Write-Host "✅ Tests terminés" -ForegroundColor Green
