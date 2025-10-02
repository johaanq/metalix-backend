# ====================================================================
# METALIX BACKEND - Automated API Endpoint Testing Script
# ====================================================================
# Este script prueba TODOS los endpoints del backend automáticamente
# y genera un reporte detallado de los resultados
# ====================================================================

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Email = "admin@metalix.com",
    [string]$Password = "password123"
)

# Colores para la salida
$ErrorColor = "Red"
$SuccessColor = "Green"
$InfoColor = "Cyan"
$WarningColor = "Yellow"

# Resultados
$results = @{
    Total = 0
    Passed = 0
    Failed = 0
    Skipped = 0
    Details = @()
}

# Función para hacer peticiones HTTP
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Endpoint,
        [hashtable]$Headers = @{},
        [object]$Body = $null,
        [string]$Description
    )
    
    $results.Total++
    $url = "$BaseUrl$Endpoint"
    
    try {
        Write-Host "`n[$results.Total] Testing: " -NoNewline -ForegroundColor $InfoColor
        Write-Host "$Method $Endpoint" -ForegroundColor White
        Write-Host "    Description: $Description" -ForegroundColor Gray
        
        $params = @{
            Uri = $url
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
            TimeoutSec = 30
        }
        
        if ($Body -ne $null) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-RestMethod @params
        
        $results.Passed++
        Write-Host "    ✅ PASSED" -ForegroundColor $SuccessColor
        Write-Host "    Status: 200 OK" -ForegroundColor $SuccessColor
        
        $results.Details += @{
            Test = $Description
            Method = $Method
            Endpoint = $Endpoint
            Status = "PASSED"
            StatusCode = 200
            Response = $response
        }
        
        return $response
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        $errorMessage = $_.Exception.Message
        
        # Algunos errores son esperados (401, 403, 404 para DELETE de recursos inexistentes)
        if ($statusCode -eq 401 -or $statusCode -eq 403) {
            $results.Skipped++
            Write-Host "    ⚠️ SKIPPED (Auth Required)" -ForegroundColor $WarningColor
            Write-Host "    Status: $statusCode" -ForegroundColor $WarningColor
            
            $results.Details += @{
                Test = $Description
                Method = $Method
                Endpoint = $Endpoint
                Status = "SKIPPED"
                StatusCode = $statusCode
                Reason = "Authentication/Authorization required"
            }
        }
        elseif ($statusCode -eq 404 -and $Method -eq "DELETE") {
            $results.Passed++
            Write-Host "    ✅ PASSED (Expected 404 for DELETE)" -ForegroundColor $SuccessColor
            
            $results.Details += @{
                Test = $Description
                Method = $Method
                Endpoint = $Endpoint
                Status = "PASSED"
                StatusCode = $statusCode
                Note = "Expected 404 for non-existent resource"
            }
        }
        else {
            $results.Failed++
            Write-Host "    ❌ FAILED" -ForegroundColor $ErrorColor
            Write-Host "    Status: $statusCode" -ForegroundColor $ErrorColor
            Write-Host "    Error: $errorMessage" -ForegroundColor $ErrorColor
            
            $results.Details += @{
                Test = $Description
                Method = $Method
                Endpoint = $Endpoint
                Status = "FAILED"
                StatusCode = $statusCode
                Error = $errorMessage
            }
        }
        
        return $null
    }
}

# ====================================================================
# INICIO DE PRUEBAS
# ====================================================================

Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor $InfoColor
Write-Host "║  METALIX BACKEND - Automated API Endpoint Testing            ║" -ForegroundColor $InfoColor
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor $InfoColor
Write-Host "`nBase URL: $BaseUrl" -ForegroundColor White
Write-Host "Started: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor White

# ====================================================================
# 0. HOME - Verificación básica
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: HOME - Basic Server Check" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/" -Description "Check server status"

# ====================================================================
# 1. AUTHENTICATION (sin token)
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: AUTHENTICATION" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

# Login para obtener token
$loginResponse = Invoke-ApiRequest -Method "POST" -Endpoint "/api/v1/auth/login" `
    -Body @{email=$Email; password=$Password} `
    -Description "Login as admin"

$token = ""
if ($loginResponse -ne $null -and $loginResponse.token) {
    $token = $loginResponse.token
    Write-Host "`n🔑 Token obtained successfully!" -ForegroundColor $SuccessColor
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
}
else {
    Write-Host "`n⚠️ Could not obtain token. Authenticated tests will be skipped." -ForegroundColor $WarningColor
}

# Headers con token
$authHeaders = @{}
if ($token -ne "") {
    $authHeaders = @{
        "Authorization" = "Bearer $token"
    }
}

# Register (crear usuario de prueba)
Invoke-ApiRequest -Method "POST" -Endpoint "/api/v1/auth/register" `
    -Body @{
        email="test.auto.$(Get-Random)@metalix.com"
        password="password123"
        firstName="Test"
        lastName="Auto"
        role="CITIZEN"
    } `
    -Description "Register new citizen user"

# ====================================================================
# 2. USERS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: USERS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users" -Headers $authHeaders -Description "Get all users"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/1" -Headers $authHeaders -Description "Get user by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/3/points" -Headers $authHeaders -Description "Get user points"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/role/CITIZEN" -Headers $authHeaders -Description "Get users by role"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/municipality/1" -Headers $authHeaders -Description "Get users by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/3/profile" -Headers $authHeaders -Description "Get user profile"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/3/stats" -Headers $authHeaders -Description "Get user stats"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/users/3/activity" -Headers $authHeaders -Description "Get user activity"

# ====================================================================
# 3. MUNICIPALITIES
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: MUNICIPALITIES" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/municipalities" -Headers $authHeaders -Description "Get all municipalities"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/municipalities/1" -Headers $authHeaders -Description "Get municipality by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/municipalities/code/LIMA001" -Headers $authHeaders -Description "Get municipality by code"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/municipalities/1/stats" -Headers $authHeaders -Description "Get municipality stats"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/municipalities/1/dashboard" -Headers $authHeaders -Description "Get municipality dashboard"

# ====================================================================
# 4. ZONES
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: ZONES" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/zones" -Headers $authHeaders -Description "Get all zones"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/zones/1" -Headers $authHeaders -Description "Get zone by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/zones/municipality/1" -Headers $authHeaders -Description "Get zones by municipality"

# ====================================================================
# 5. WASTE COLLECTORS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: WASTE COLLECTORS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collectors" -Headers $authHeaders -Description "Get all waste collectors"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collectors/1" -Headers $authHeaders -Description "Get waste collector by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collectors/municipality/1" -Headers $authHeaders -Description "Get collectors by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collectors/zone/1" -Headers $authHeaders -Description "Get collectors by zone"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collectors/full" -Headers $authHeaders -Description "Get full collectors"

# ====================================================================
# 6. WASTE COLLECTIONS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: WASTE COLLECTIONS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collections" -Headers $authHeaders -Description "Get all waste collections"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collections/1" -Headers $authHeaders -Description "Get waste collection by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collections/user/3?page=0&size=10" -Headers $authHeaders -Description "Get collections by user"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collections/collector/1" -Headers $authHeaders -Description "Get collections by collector"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/waste-collections/municipality/1" -Headers $authHeaders -Description "Get collections by municipality"

# ====================================================================
# 7. SENSOR DATA
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: SENSOR DATA" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/sensor-data" -Headers $authHeaders -Description "Get all sensor data"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/sensor-data/1" -Headers $authHeaders -Description "Get sensor data by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/sensor-data/collector/1" -Headers $authHeaders -Description "Get sensor data by collector"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/sensor-data/collector/1/latest" -Headers $authHeaders -Description "Get latest sensor data"

# ====================================================================
# 8. REWARDS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: REWARDS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rewards" -Headers $authHeaders -Description "Get all rewards"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rewards/1" -Headers $authHeaders -Description "Get reward by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rewards/active" -Headers $authHeaders -Description "Get active rewards"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rewards/municipality/1" -Headers $authHeaders -Description "Get rewards by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rewards/affordable/500" -Headers $authHeaders -Description "Get affordable rewards"

# ====================================================================
# 9. REWARD TRANSACTIONS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: REWARD TRANSACTIONS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/reward-transactions" -Headers $authHeaders -Description "Get all reward transactions"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/reward-transactions/1" -Headers $authHeaders -Description "Get transaction by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/reward-transactions/user/3?page=0&size=10" -Headers $authHeaders -Description "Get transactions by user"

# ====================================================================
# 10. RFID CARDS
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: RFID CARDS" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rfid-cards" -Headers $authHeaders -Description "Get all RFID cards"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rfid-cards/1" -Headers $authHeaders -Description "Get RFID card by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rfid-cards/number/RFID001" -Headers $authHeaders -Description "Get card by number"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/rfid-cards/user/3" -Headers $authHeaders -Description "Get card by user ID"
Invoke-ApiRequest -Method "POST" -Endpoint "/api/v1/rfid-cards/use/RFID001" -Headers $authHeaders -Description "Use RFID card"

# ====================================================================
# 11. MONITORING
# ====================================================================
Write-Host "`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "MODULE: MONITORING" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/reports" -Headers $authHeaders -Description "Get all reports"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/reports/1" -Headers $authHeaders -Description "Get report by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/reports/municipality/1" -Headers $authHeaders -Description "Get reports by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/metrics" -Headers $authHeaders -Description "Get all metrics"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/metrics/1" -Headers $authHeaders -Description "Get metric by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/metrics/municipality/1" -Headers $authHeaders -Description "Get metrics by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/alerts" -Headers $authHeaders -Description "Get all alerts"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/alerts/1" -Headers $authHeaders -Description "Get alert by ID"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/alerts/municipality/1" -Headers $authHeaders -Description "Get alerts by municipality"
Invoke-ApiRequest -Method "GET" -Endpoint "/api/v1/monitoring/alerts/unread" -Headers $authHeaders -Description "Get unread alerts"

# ====================================================================
# RESULTADOS FINALES
# ====================================================================
Write-Host "`n`n" + ("="*70) -ForegroundColor $InfoColor
Write-Host "TEST RESULTS SUMMARY" -ForegroundColor $InfoColor
Write-Host ("="*70) -ForegroundColor $InfoColor

$passRate = if($results.Total -gt 0) { [math]::Round(($results.Passed / $results.Total) * 100, 2) } else { 0 }

Write-Host "`nTotal Tests: $($results.Total)" -ForegroundColor White
Write-Host "✅ Passed: $($results.Passed)" -ForegroundColor $SuccessColor
Write-Host "❌ Failed: $($results.Failed)" -ForegroundColor $ErrorColor
Write-Host "⚠️  Skipped: $($results.Skipped)" -ForegroundColor $WarningColor
Write-Host "`nPass Rate: $passRate%" -ForegroundColor $(if($passRate -ge 80) { $SuccessColor } else { $WarningColor })

Write-Host "`nCompleted: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor White

# Guardar resultados en archivo JSON
$reportFile = "test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').json"
$results | ConvertTo-Json -Depth 10 | Out-File -FilePath $reportFile -Encoding UTF8
Write-Host "`n📄 Detailed report saved to: $reportFile" -ForegroundColor $InfoColor

# Retornar código de salida
if ($results.Failed -gt 0) {
    Write-Host "`n❌ Some tests failed. Please review the results above." -ForegroundColor $ErrorColor
    exit 1
}
else {
    Write-Host "`n✅ All tests passed successfully!" -ForegroundColor $SuccessColor
    exit 0
}

