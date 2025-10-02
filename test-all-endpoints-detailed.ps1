# Script para probar todos los endpoints del backend Metalix
# Ejecutar desde PowerShell en el directorio del proyecto

Write-Host "=== TESTING ALL ENDPOINTS ===" -ForegroundColor Green
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
            $jsonContent = $response.Content | ConvertFrom-Json -ErrorAction SilentlyContinue
            if ($jsonContent) {
                Write-Host "Response: $($jsonContent | ConvertTo-Json -Compress)" -ForegroundColor White
            } else {
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

Write-Host "=== 1. TESTING PUBLIC ENDPOINTS ===" -ForegroundColor Magenta

# Home endpoint
Test-Endpoint -Method "GET" -Url "$baseUrl/" -Description "Home endpoint"

# Swagger/OpenAPI endpoints
Test-Endpoint -Method "GET" -Url "$baseUrl/v3/api-docs" -Description "OpenAPI JSON"
Test-Endpoint -Method "GET" -Url "$baseUrl/swagger-ui/index.html" -Description "Swagger UI"

# Municipalities (should be public)
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/municipalities" -Description "Get all municipalities"

Write-Host "=== 2. TESTING AUTHENTICATION ===" -ForegroundColor Magenta

# Register a test user
$registerBody = @{
    email = "test@metalix.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    role = "CITIZEN"
} | ConvertTo-Json

Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/auth/register" -Body $registerBody -Description "Register test user"

# Login with test user
$loginBody = @{
    email = "test@metalix.com"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/api/v1/auth/login" -Body $loginBody -Description "Login test user"

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

Write-Host "=== 3. TESTING PROTECTED ENDPOINTS ===" -ForegroundColor Magenta

if ($token) {
    $authHeaders = @{
        "Authorization" = "Bearer $token"
    }
    
    # Test protected endpoints
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users/profile" -Headers $authHeaders -Description "Get user profile"
    Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users" -Headers $authHeaders -Description "Get all users"
    
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
    
} else {
    Write-Host "❌ No token available, skipping protected endpoints" -ForegroundColor Red
}

Write-Host "=== 4. TESTING ADMIN ENDPOINTS (if token available) ===" -ForegroundColor Magenta

if ($token) {
    # Try to create a municipality (requires SYSTEM_ADMIN role)
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
}

Write-Host "=== 5. TESTING ERROR CASES ===" -ForegroundColor Magenta

# Test 404
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/nonexistent" -Description "Test 404 error"

# Test unauthorized access
Test-Endpoint -Method "GET" -Url "$baseUrl/api/v1/users" -Description "Test unauthorized access"

Write-Host "=== TESTING COMPLETED ===" -ForegroundColor Green
Write-Host "Check the results above for any errors or issues." -ForegroundColor Yellow
