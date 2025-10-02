# Script para probar todos los endpoints del backend Metalix con datos cargados
# Ejecutar desde PowerShell en el directorio del proyecto

Write-Host "=== TESTING ALL ENDPOINTS WITH DATA ===" -ForegroundColor Green
Write-Host "Backend URL: http://localhost:8080" -ForegroundColor Yellow
Write-Host ""

# Función para hacer requests HTTP
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = $null,
        [hashtable]$Headers = @{},
        [string]$Description = ""
    )
    
    try {
        Write-Host "Testing: $Method $Url" -ForegroundColor Cyan
        if ($Description) {
            Write-Host "Description: $Description" -ForegroundColor Gray
        }
        
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
        }
        
        if ($Body) {
            $params.Body = $Body
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-WebRequest @params
        Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
        
        if ($response.Content) {
            try {
                $jsonContent = $response.Content | ConvertFrom-Json -ErrorAction SilentlyContinue
                if ($jsonContent) {
                    Write-Host "Response: $($jsonContent | ConvertTo-Json -Compress)" -ForegroundColor White
                } else {
                    Write-Host "Response: $($response.Content)" -ForegroundColor White
                }
            } catch {
                Write-Host "Response: $($response.Content)" -ForegroundColor White
            }
        }
        
    } catch {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }
    
    Write-Host ""
}

# Variables globales
$baseUrl = "http://localhost:8080"
$token = $null

Write-Host "=== 1. CHECKING DATA STATUS ===" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/admin/data-status" -Description "Check data status"

Write-Host "=== 2. TESTING PUBLIC ENDPOINTS ===" -ForegroundColor Magenta

# Home endpoint
Test-Endpoint -Method "GET" -Url "$baseUrl/" -Description "Home endpoint"

# Municipalities (should be public and have data now)
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities" -Description "Get all municipalities"

Write-Host "=== 3. TESTING AUTHENTICATION ===" -ForegroundColor Magenta

# Login with admin user
$loginBody = @{
    email = "admin@metalix.com"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/auth/login" -Body $loginBody -Description "Login admin user"

# Try to extract token from login response
try {
    $loginResult = Invoke-WebRequest -Uri "$baseUrl/api/v1/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginData = $loginResult.Content | ConvertFrom-Json
    if ($loginData.token) {
        $token = $loginData.token
        Write-Host "✅ Token obtained: $($token.Substring(0, 20))..." -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Could not obtain token from login" -ForegroundColor Red
}

# Login with citizen user
$citizenLoginBody = @{
    email = "maria.lopez@email.com"
    password = "password123"
} | ConvertTo-Json

$citizenLoginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/auth/login" -Body $citizenLoginBody -Description "Login citizen user"

Write-Host "=== 4. TESTING PROTECTED ENDPOINTS ===" -ForegroundColor Magenta

if ($token) {
    $authHeaders = @{
        "Authorization" = "Bearer $token"
    }
    
    # Test protected endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users" -Headers $authHeaders -Description "Get all users"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users/1" -Headers $authHeaders -Description "Get user by ID"
    
    # Waste collection endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/waste-collections" -Headers $authHeaders -Description "Get waste collections"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/waste-collectors" -Headers $authHeaders -Description "Get waste collectors"
    
    # Reward endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/rewards" -Headers $authHeaders -Description "Get rewards"
    
    # RFID endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/rfid-cards" -Headers $authHeaders -Description "Get RFID cards"
    
    # Monitoring endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/monitoring/alerts" -Headers $authHeaders -Description "Get alerts"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/monitoring/metrics" -Headers $authHeaders -Description "Get metrics"
    
    # Municipality specific endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities/1" -Headers $authHeaders -Description "Get municipality by ID"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities/1/stats" -Headers $authHeaders -Description "Get municipality stats"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities/1/dashboard" -Headers $authHeaders -Description "Get municipality dashboard"
    
} else {
    Write-Host "❌ No token available, skipping protected endpoints" -ForegroundColor Red
}

Write-Host "=== 5. TESTING ADMIN ENDPOINTS (if token available) ===" -ForegroundColor Magenta

if ($token) {
    # Try to create a new municipality (requires SYSTEM_ADMIN role)
    $municipalityBody = @{
        name = "Municipalidad de Prueba"
        code = "TEST001"
        region = "Región Test"
        population = 50000
        area = 100.0
        contactEmail = "test@municipio.test"
        contactPhone = "+506 2222-2222"
    } | ConvertTo-Json
    
    Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/municipalities" -Body $municipalityBody -Headers $authHeaders -Description "Create municipality (requires admin)"
    
    # Try to create a new user (requires admin)
    $userBody = @{
        email = "test.user@example.com"
        password = "password123"
        firstName = "Test"
        lastName = "User"
        role = "CITIZEN"
        municipalityId = 1
        phone = "+51-999-000-000"
        address = "Test Address"
        city = "Lima"
        zipCode = "15001"
    } | ConvertTo-Json
    
    Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/users" -Body $userBody -Headers $authHeaders -Description "Create user (requires admin)"
}

Write-Host "=== 6. TESTING ERROR CASES ===" -ForegroundColor Magenta

# Test 404
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/nonexistent" -Description "Test 404 error"

# Test unauthorized access
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users" -Description "Test unauthorized access"

Write-Host "=== 7. TESTING SPECIFIC FUNCTIONALITY ===" -ForegroundColor Magenta

if ($token) {
    # Test municipality endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities/code/LIM-001" -Headers $authHeaders -Description "Get municipality by code"
    
    # Test zones
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/zones" -Headers $authHeaders -Description "Get all zones"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/zones/municipality/1" -Headers $authHeaders -Description "Get zones by municipality"
    
    # Test waste collectors by municipality
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/waste-collectors/municipality/1" -Headers $authHeaders -Description "Get waste collectors by municipality"
    
    # Test user stats
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users/2/stats" -Headers $authHeaders -Description "Get user stats"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users/2/profile" -Headers $authHeaders -Description "Get user profile"
}

Write-Host "=== TESTING COMPLETED ===" -ForegroundColor Green
Write-Host "Check the results above for any errors or issues." -ForegroundColor Yellow
