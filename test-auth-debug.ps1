# Script para debuggear autenticación
Write-Host "=== AUTH DEBUG ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"

# Test 1: Verificar usuarios
Write-Host "1. Checking users..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/admin/check-users" -Method GET
    Write-Host "✅ Users check successful" -ForegroundColor Green
    $users = ($response.Content | ConvertFrom-Json).users
    foreach ($user in $users) {
        Write-Host "  - $($user.email) ($($user.role)) - Active: $($user.is_active)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Users check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 2: Verificar datos
Write-Host "2. Checking data status..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/admin/data-status" -Method GET
    Write-Host "✅ Data status check successful" -ForegroundColor Green
    $data = $response.Content | ConvertFrom-Json
    Write-Host "  - Municipalities: $($data.municipalities)" -ForegroundColor White
    Write-Host "  - Users: $($data.users)" -ForegroundColor White
    Write-Host "  - Has data: $($data.hasData)" -ForegroundColor White
} catch {
    Write-Host "❌ Data status check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 3: Probar login directo
Write-Host "3. Testing direct login..." -ForegroundColor Yellow
$body = @{
    email = "test@test.com"
    password = "password123"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/auth/login" -Method POST -Body $body -ContentType "application/json"
    Write-Host "✅ Direct login successful" -ForegroundColor Green
    $loginData = $response.Content | ConvertFrom-Json
    Write-Host "  - Token: $($loginData.token.Substring(0, 20))..." -ForegroundColor White
    Write-Host "  - User ID: $($loginData.userId)" -ForegroundColor White
    Write-Host "  - Role: $($loginData.role)" -ForegroundColor White
} catch {
    Write-Host "❌ Direct login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorBody = $reader.ReadToEnd()
        Write-Host "  Error body: $errorBody" -ForegroundColor Red
    }
}

Write-Host ""

# Test 4: Probar auth service
Write-Host "4. Testing auth service..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/admin/test-auth-service" -Method POST -Body $body -ContentType "application/json"
    Write-Host "✅ Auth service test successful" -ForegroundColor Green
    $authData = $response.Content | ConvertFrom-Json
    Write-Host "  - Success: $($authData.success)" -ForegroundColor White
    if ($authData.token) {
        Write-Host "  - Token: $($authData.token.Substring(0, 20))..." -ForegroundColor White
    }
} catch {
    Write-Host "❌ Auth service test failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorBody = $reader.ReadToEnd()
        Write-Host "  Error body: $errorBody" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== DEBUG COMPLETED ===" -ForegroundColor Green
