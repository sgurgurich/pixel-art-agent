# Pixel Art Agent - Test Script
# PowerShell script to test the API endpoints

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Pixel Art Agent - API Test Suite" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api/pixelart"

# Test 1: Health Check
Write-Host "Test 1: Health Check" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Get -Uri "$baseUrl/health"
    Write-Host "✓ Health check passed" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json) -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Health check failed" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

# Test 2: Get Example Request
Write-Host "Test 2: Get Example Request" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Get -Uri "$baseUrl/example"
    Write-Host "✓ Example request retrieved" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json) -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get example" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

# Test 3: Generate Pixel Art - Knight Character
Write-Host "Test 3: Generate Pixel Art - Knight Character" -ForegroundColor Yellow
$knightRequest = @{
    assetType = "character"
    description = "A brave knight with a sword and shield"
    style = "16-bit"
    colorPalette = "medieval"
    size = "32x32"
    additionalContext = "Should have idle and attack animations"
} | ConvertTo-Json

try {
    Write-Host "Sending request to AI agent..." -ForegroundColor Cyan
    $response = Invoke-RestMethod -Method Post -Uri "$baseUrl/generate" `
        -ContentType "application/json" -Body $knightRequest
    Write-Host "✓ Pixel art generated successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Detailed Description:" -ForegroundColor Cyan
    Write-Host $response.detailedDescription -ForegroundColor White
    Write-Host ""
    Write-Host "Suggested Colors:" -ForegroundColor Cyan
    Write-Host ($response.suggestedColors -join ", ") -ForegroundColor Magenta
    Write-Host ""
    Write-Host "Specifications:" -ForegroundColor Cyan
    Write-Host ($response.specifications | ConvertTo-Json) -ForegroundColor Gray
    Write-Host ""
    Write-Host "Animation Suggestions:" -ForegroundColor Cyan
    Write-Host ($response.animationSuggestions -join ", ") -ForegroundColor Yellow
    Write-Host ""
} catch {
    Write-Host "✗ Failed to generate pixel art" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

# Test 4: Generate Pixel Art - Fire Sword
Write-Host "Test 4: Generate Pixel Art - Fire Sword Weapon" -ForegroundColor Yellow
$swordRequest = @{
    assetType = "weapon"
    description = "A magical fire sword with flames"
    style = "16-bit"
    colorPalette = "vibrant"
    size = "32x16"
    additionalContext = "Should have a glowing effect"
} | ConvertTo-Json

try {
    Write-Host "Sending request to AI agent..." -ForegroundColor Cyan
    $response = Invoke-RestMethod -Method Post -Uri "$baseUrl/generate" `
        -ContentType "application/json" -Body $swordRequest
    Write-Host "✓ Pixel art generated successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Detailed Description:" -ForegroundColor Cyan
    Write-Host $response.detailedDescription -ForegroundColor White
    Write-Host ""
    Write-Host "Suggested Colors:" -ForegroundColor Cyan
    Write-Host ($response.suggestedColors -join ", ") -ForegroundColor Magenta
    Write-Host ""
} catch {
    Write-Host "✗ Failed to generate pixel art" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

# Test 5: Generate Variations - Slime Enemy
Write-Host "Test 5: Generate Variations - Slime Enemy (3 variations)" -ForegroundColor Yellow
$slimeRequest = @{
    assetType = "enemy"
    description = "A cute bouncing slime monster"
    style = "8-bit"
    colorPalette = "pastel"
    size = "16x16"
    additionalContext = "Should have bounce animation"
} | ConvertTo-Json

try {
    Write-Host "Generating 3 variations..." -ForegroundColor Cyan
    $response = Invoke-RestMethod -Method Post -Uri "$baseUrl/generate/variations?count=3" `
        -ContentType "application/json" -Body $slimeRequest
    Write-Host "✓ Generated $($response.Count) variations!" -ForegroundColor Green
    
    for ($i = 0; $i -lt $response.Count; $i++) {
        Write-Host ""
        Write-Host "--- Variation $($i + 1) ---" -ForegroundColor Cyan
        Write-Host "Colors: $($response[$i].suggestedColors -join ", ")" -ForegroundColor Magenta
        Write-Host "Style: $($response[$i].style)" -ForegroundColor Yellow
    }
    Write-Host ""
} catch {
    Write-Host "✗ Failed to generate variations" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
