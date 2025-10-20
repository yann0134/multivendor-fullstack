# Script pour créer un supplier manuellement
Write-Host "🔧 Création manuelle du supplier yanndom@gmail.com" -ForegroundColor Yellow

$supplierData = @{
    email = "yanndom@gmail.com"
    supplierName = "Yann Dom"
    mobile = "778046375"
} | ConvertTo-Json

Write-Host "📤 Envoi de la requête..." -ForegroundColor Blue

try {
    $response = Invoke-RestMethod -Uri "http://localhost:3026/api/products/create-supplier" -Method POST -Body $supplierData -ContentType "application/json"
    
    Write-Host "✅ Supplier créé avec succès !" -ForegroundColor Green
    Write-Host "📋 Détails du supplier:" -ForegroundColor Cyan
    Write-Host "   Email: $($response.email)" -ForegroundColor White
    Write-Host "   Nom: $($response.supplierName)" -ForegroundColor White
    Write-Host "   Rôle: $($response.role)" -ForegroundColor White
    Write-Host "   Statut: $($response.accountStatus)" -ForegroundColor White
    
    Write-Host "`n🎉 Vous pouvez maintenant ajouter des produits !" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur lors de la création du supplier:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
