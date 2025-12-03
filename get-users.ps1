# Get Users
$baseUrl = "http://localhost:8081"

# Login
$loginBody = '{"email":"admin@metalix.com","password":"password123"}'
$login = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" -Method Post -Body $loginBody -ContentType "application/json"

$headers = @{
    "Authorization" = "Bearer $($login.token)"
}

# Get users
$users = Invoke-RestMethod -Uri "$baseUrl/api/v1/users/all" -Method Get -Headers $headers

Write-Host "`n=== USUARIOS EN EL SISTEMA ===" -ForegroundColor Cyan

Write-Host "`nAdmin:" -ForegroundColor Yellow
$users | Where-Object {$_.role -eq 'SYSTEM_ADMIN'} | ForEach-Object {
    Write-Host "  ID: $($_.id) - $($_.email)" -ForegroundColor White
}

Write-Host "`nBañistas:" -ForegroundColor Yellow
$users | Where-Object {$_.role -eq 'CITIZEN'} | ForEach-Object {
    Write-Host "  ID: $($_.id) - $($_.firstName) $($_.lastName) - RFID: $($_.rfidCard)" -ForegroundColor Cyan
}

