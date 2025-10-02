# Script simple de prueba del backend
$baseUrl = "https://metalix-backend.onrender.com/api/v1"
$homeUrl = "https://metalix-backend.onrender.com"
$passed = 0
$failed = 0

Write-Host "`n=== METALIX BACKEND TEST (RENDER) ===" -ForegroundColor Cyan
Write-Host "Base URL: $baseUrl" -ForegroundColor White
Write-Host "Testing production server on Render...`n" -ForegroundColor Yellow

# Test 1: Home
Write-Host "[1] Testing Home..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri $homeUrl -Method GET -TimeoutSec 30
    Write-Host " PASSED" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAILED: $_" -ForegroundColor Red
    $failed++
}

# Test 2: Login
Write-Host "[2] Testing Login..." -NoNewline
try {
    $loginBody = @{
        email = "admin@metalix.com"
        password = "password123"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -TimeoutSec 30
    $token = $response.token
    Write-Host " PASSED (Token obtained)" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAILED: $_" -ForegroundColor Red
    $failed++
    $token = $null
}

if ($token) {
    $headers = @{ "Authorization" = "Bearer $token" }
    
    # Test 3: Get Users
    Write-Host "[3] Testing GET /users..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/users" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) users)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
    
    # Test 4: Get Municipalities
    Write-Host "[4] Testing GET /municipalities..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/municipalities" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) municipalities)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
    
    # Test 5: Get Zones
    Write-Host "[5] Testing GET /zones..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/zones" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) zones)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
    
    # Test 6: Get Waste Collectors
    Write-Host "[6] Testing GET /waste-collectors..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/waste-collectors" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) collectors)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
    
    # Test 7: Get Rewards
    Write-Host "[7] Testing GET /rewards..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/rewards" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) rewards)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
    
    # Test 8: Get RFID Cards
    Write-Host "[8] Testing GET /rfid-cards..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/rfid-cards" -Method GET -Headers $headers -TimeoutSec 30
        Write-Host " PASSED (Found $($response.Count) cards)" -ForegroundColor Green
        $passed++
    } catch {
        Write-Host " FAILED: $_" -ForegroundColor Red
        $failed++
    }
}

Write-Host "`n=== RESULTS ===" -ForegroundColor Cyan
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
Write-Host "Total: $($passed + $failed)`n" -ForegroundColor White

