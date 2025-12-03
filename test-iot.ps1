# Test IoT Endpoint Simple
$baseUrl = "http://localhost:8081"

Write-Host "`n=== Testing IoT Collection Endpoint ===" -ForegroundColor Cyan

# Test Health
Write-Host "`n1. Health Check..." -ForegroundColor Green
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/api/v1/iot/collections/health" -Method Get
    Write-Host "SUCCESS: $health" -ForegroundColor Green
} catch {
    Write-Host "FAILED - Backend not ready" -ForegroundColor Red
    exit
}

# Test Register Collection
Write-Host "`n2. Register Collection (RFID + Weight + Type)..." -ForegroundColor Green
Write-Host "Bañista escanea RFID y deposita 5.5kg de PLASTICO" -ForegroundColor Gray

$request = @{
    rfidCardNumber = "RFID10000000"
    weight = 5.5
    collectorId = 1
    recyclableType = "PLASTIC"
} | ConvertTo-Json

Write-Host "`nRequest:" -ForegroundColor DarkGray
$request | Write-Host -ForegroundColor DarkGray

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/iot/collections/register" -Method Post -Body $request -ContentType "application/json"
    
    Write-Host "`n*** SUCCESS ***" -ForegroundColor Green
    Write-Host "`nBañista identificado:" -ForegroundColor Cyan
    Write-Host "  Nombre: $($response.userName)" -ForegroundColor White
    Write-Host "  Email: $($response.userEmail)" -ForegroundColor Gray
    
    Write-Host "`nRecoleccion registrada:" -ForegroundColor Cyan
    Write-Host "  Peso depositado: $($response.weight) kg" -ForegroundColor White
    Write-Host "  Tipo material: $($response.recyclableType)" -ForegroundColor White
    
    Write-Host "`nPuntos:" -ForegroundColor Cyan
    Write-Host "  Puntos ganados: +$($response.pointsEarned)" -ForegroundColor Yellow
    Write-Host "  Total acumulado: $($response.totalUserPoints)" -ForegroundColor Green
    
    Write-Host "`nMensaje: $($response.message)" -ForegroundColor Cyan
    
    Write-Host "`nPantalla IoT mostraria:" -ForegroundColor Magenta
    Write-Host "  Bienvenido $($response.userName)!" -ForegroundColor White
    Write-Host "  +$($response.pointsEarned) puntos" -ForegroundColor Yellow
    Write-Host "  Total: $($response.totalUserPoints) puntos" -ForegroundColor Green
    
} catch {
    Write-Host "`nERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Test Completed ===" -ForegroundColor Cyan

