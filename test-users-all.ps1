# Test nuevo endpoint /api/v1/users/all
$baseUrl = "http://localhost:8081"

Write-Host "Testing /api/v1/users/all endpoint..." -ForegroundColor Cyan

# Login
$loginBody = '{"email":"admin@metalix.com","password":"password123"}'
$login = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $login.token

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Test endpoint sin paginacion
Write-Host "`nTesting GET /api/v1/users/all (sin paginacion)..." -ForegroundColor Green
try {
    $users = Invoke-RestMethod -Uri "$baseUrl/api/v1/users/all" -Method Get -Headers $headers
    Write-Host "SUCCESS - Received $($users.Count) users" -ForegroundColor Green
    Write-Host "`nPrimeros 3 usuarios:" -ForegroundColor Yellow
    $users | Select-Object -First 3 | ConvertTo-Json -Depth 2
} catch {
    Write-Host "FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

# Comparar con endpoint paginado
Write-Host "`nTesting GET /api/v1/users (con paginacion)..." -ForegroundColor Green
try {
    $usersPaged = Invoke-RestMethod -Uri "$baseUrl/api/v1/users" -Method Get -Headers $headers
    Write-Host "SUCCESS - Page info:" -ForegroundColor Green
    Write-Host "  Total Elements: $($usersPaged.totalElements)" -ForegroundColor Gray
    Write-Host "  Total Pages: $($usersPaged.totalPages)" -ForegroundColor Gray
    Write-Host "  Current Page: $($usersPaged.number)" -ForegroundColor Gray
    Write-Host "  Page Size: $($usersPaged.size)" -ForegroundColor Gray
    Write-Host "  Content Count: $($usersPaged.content.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTest completed!" -ForegroundColor Cyan

