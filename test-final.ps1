# Test Final IoT Endpoint
$baseUrl = "http://localhost:8081"

Write-Host "`n=== Test IoT Endpoint Final ===" -ForegroundColor Cyan

# Test con userId y points
Write-Host "`nRegistrando coleccion para Bañista ID: 7" -ForegroundColor Yellow
Write-Host "Peso: 5.5 kg | Tipo: PLASTIC | Puntos: 66" -ForegroundColor Gray

$request = @{
    userId = 7
    weight = 5.5
    collectorId = 1
    recyclableType = "PLASTIC"
    points = 66
} | ConvertTo-Json

Write-Host "`nRequest Body:" -ForegroundColor DarkGray
$request | Write-Host

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/iot/collections/register" -Method Post -Body $request -ContentType "application/json"
    
    Write-Host "`n*** EXITO ***" -ForegroundColor Green
    Write-Host "`nBañista: $($response.userName)" -ForegroundColor Cyan
    Write-Host "Email: $($response.userEmail)" -ForegroundColor Gray
    Write-Host "Puntos ganados: +$($response.pointsEarned)" -ForegroundColor Yellow
    Write-Host "Total acumulado: $($response.totalUserPoints)" -ForegroundColor Green
    Write-Host "`n$($response.message)" -ForegroundColor Cyan
    
} catch {
    Write-Host "`nERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Test Completado ===" -ForegroundColor Cyan

