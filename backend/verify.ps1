# PlaceNextAI backend verification — run inside the backend folder:  powershell -ExecutionPolicy Bypass -File verify.ps1
$ErrorActionPreference = "Stop"
$base = "http://localhost:8080"

Write-Host "=== CHECK 1/5: mvn clean install ===" -ForegroundColor Cyan
mvn clean install
if ($LASTEXITCODE -ne 0) { Write-Host "FAILED: build did not succeed." -ForegroundColor Red; exit 1 }
Write-Host "PASS: build succeeded." -ForegroundColor Green

Write-Host "=== CHECK 2/5: mvn spring-boot:run starts ===" -ForegroundColor Cyan
$server = Start-Process mvn -ArgumentList "spring-boot:run" -PassThru -NoNewWindow
$started = $false
for ($attempt = 1; $attempt -le 60; $attempt++) {
    try {
        Invoke-RestMethod "$base/v3/api-docs" -TimeoutSec 2 | Out-Null
        $started = $true
        break
    } catch { Start-Sleep -Seconds 2 }
}
if (-not $started) {
    Write-Host "FAILED: backend did not start within 2 minutes. Check MySQL is running (service MySQL80) and credentials in application.properties." -ForegroundColor Red
    exit 1
}
Write-Host "PASS: backend is up on port 8080." -ForegroundColor Green

Write-Host "=== CHECK 3/5: Swagger contains the auth routes ===" -ForegroundColor Cyan
$docs = Invoke-RestMethod "$base/v3/api-docs"
$paths = $docs.paths.PSObject.Properties.Name
foreach ($route in "/api/auth/login", "/api/auth/register", "/api/auth/me") {
    if ($paths -contains $route) {
        Write-Host "PASS: $route documented in Swagger." -ForegroundColor Green
    } else {
        Write-Host "FAILED: $route missing from Swagger." -ForegroundColor Red
        exit 1
    }
}

Write-Host "=== CHECK 4/5: login returns a JWT ===" -ForegroundColor Cyan
$body = '{"email":"admin@placenextai.com","password":"Admin@123"}'
$login = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType "application/json" -Body $body
if (-not $login.token) { Write-Host "FAILED: no token in login response." -ForegroundColor Red; exit 1 }
Write-Host "PASS: token received. role=$($login.role) name=$($login.name)" -ForegroundColor Green

Write-Host "=== CHECK 5/5: /api/auth/me returns the profile ===" -ForegroundColor Cyan
$me = Invoke-RestMethod "$base/api/auth/me" -Headers @{ Authorization = "Bearer $($login.token)" }
Write-Host "PASS: me endpoint OK. name=$($me.name) role=$($me.role) profileCompletion=$($me.profileCompletion)" -ForegroundColor Green

Write-Host ""
Write-Host "ALL 5 CHECKS PASSED — the backend matches the frontend contract." -ForegroundColor Green
Write-Host "The server is still running in this window. Open a second terminal:  cd frontend && npm run dev" -ForegroundColor Yellow
Write-Host "Then log in at http://localhost:5173 with admin@placenextai.com / Admin@123"
