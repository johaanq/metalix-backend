# Script simple para probar login
Write-Host "=== TESTING LOGIN ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"

# Test 1: Login admin
Write-Host "Testing admin login..." -ForegroundColor Yellow
$body = @{
    email = "admin@metalix.com"
    password = "password123"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/auth/login" -Method POST -Body $body -ContentType "application/json"
    Write-Host "✅ Admin login successful: $($response.StatusCode)" -ForegroundColor Green
    $loginData = $response.Content | ConvertFrom-Json
    Write-Host "Token: $($loginData.token.Substring(0, 20))..." -ForegroundColor Green
    Write-Host "User ID: $($loginData.userId)" -ForegroundColor Green
    Write-Host "Role: $($loginData.role)" -ForegroundColor Green
} catch {
    Write-Host "❌ Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error body: $errorBody" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Login citizen
Write-Host "Testing citizen login..." -ForegroundColor Yellow
$body2 = @{
    email = "maria.lopez@email.com"
    password = "password123"
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest -Uri "$baseUrl/api/v1/auth/login" -Method POST -Body $body2 -ContentType "application/json"
    Write-Host "✅ Citizen login successful: $($response2.StatusCode)" -ForegroundColor Green
    $loginData2 = $response2.Content | ConvertFrom-Json
    Write-Host "Token: $($loginData2.token.Substring(0, 20))..." -ForegroundColor Green
    Write-Host "User ID: $($loginData2.userId)" -ForegroundColor Green
    Write-Host "Role: $($loginData2.role)" -ForegroundColor Green
} catch {
    Write-Host "❌ Citizen login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error body: $errorBody" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== LOGIN TEST COMPLETED ===" -ForegroundColor Green
