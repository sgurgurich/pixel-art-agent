# Setup Pixel Art Model for Stable Diffusion

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Pixel Art Model Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Stable Diffusion container is running
Write-Host "Checking Stable Diffusion status..." -ForegroundColor Yellow
$containerStatus = docker ps --filter "name=stable-diffusion" --format "{{.Status}}"

if (-not $containerStatus) {
    Write-Host "X Stable Diffusion container is not running" -ForegroundColor Red
    Write-Host "Please start it first with: .\start-stable-diffusion.bat" -ForegroundColor Yellow
    exit 1
}

Write-Host "Stable Diffusion is running" -ForegroundColor Green
Write-Host ""

# Create models directory in container
Write-Host "Setting up model directories..." -ForegroundColor Yellow
docker exec stable-diffusion mkdir -p /data/models/Lora 2>$null
docker exec stable-diffusion mkdir -p /data/models/Stable-diffusion 2>$null

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Pixel Art Model Options" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Download from Civitai (Manual)" -ForegroundColor Green
Write-Host "2. Download from Hugging Face (Automatic)" -ForegroundColor Green
Write-Host "3. Show Manual Setup Instructions" -ForegroundColor Yellow
Write-Host ""

$choice = Read-Host "Select option (1/2/3)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "CivitAI Pixel Art Models" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Recommended: Pixel Art Diffusion XL" -ForegroundColor Cyan
        Write-Host "URL: https://civitai.com/models/120096/pixel-art-xl" -ForegroundColor White
        Write-Host ""
        Write-Host "Steps:" -ForegroundColor Yellow
        Write-Host "1. Visit the URL above" -ForegroundColor White
        Write-Host "2. Click Download" -ForegroundColor White
        Write-Host "3. Save the .safetensors file" -ForegroundColor White
        Write-Host "4. Run: docker cp downloaded-file.safetensors stable-diffusion:/data/models/Stable-diffusion/" -ForegroundColor Cyan
        Write-Host "5. Run: docker restart stable-diffusion" -ForegroundColor Cyan
        Write-Host ""
    }
    "2" {
        Write-Host ""
        Write-Host "Downloading Pixel Art XL from Hugging Face..." -ForegroundColor Yellow
        Write-Host "This will take several minutes (6.5GB download)" -ForegroundColor Gray
        Write-Host ""
        
        $confirm = Read-Host "Continue? (y/n)"
        if ($confirm -eq 'y') {
            Write-Host "Starting download..." -ForegroundColor Cyan
            docker exec stable-diffusion wget -P /data/models/Stable-diffusion/ https://huggingface.co/nerijs/pixel-art-xl/resolve/main/pixel-art-xl.safetensors
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host ""
                Write-Host "Download complete!" -ForegroundColor Green
                Write-Host "Restarting Stable Diffusion to load the model..." -ForegroundColor Yellow
                docker restart stable-diffusion
                Write-Host "Wait 2-3 minutes for the model to load" -ForegroundColor Yellow
            } else {
                Write-Host "Download failed" -ForegroundColor Red
            }
        }
    }
    "3" {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "Manual Setup Instructions" -ForegroundColor Cyan
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Best Pixel Art Models:" -ForegroundColor Yellow
        Write-Host "  1. Pixel Art Diffusion XL" -ForegroundColor White
        Write-Host "     https://civitai.com/models/120096" -ForegroundColor Gray
        Write-Host ""
        Write-Host "  2. Pixel Art Sprite Sheet" -ForegroundColor White
        Write-Host "     https://civitai.com/models/155164" -ForegroundColor Gray
        Write-Host ""
        Write-Host "Setup Steps:" -ForegroundColor Yellow
        Write-Host "  1. Download .safetensors file from above URLs" -ForegroundColor White
        Write-Host "  2. Copy to container:" -ForegroundColor White
        Write-Host "     docker cp yourfile.safetensors stable-diffusion:/data/models/Stable-diffusion/" -ForegroundColor Cyan
        Write-Host "  3. Restart: docker restart stable-diffusion" -ForegroundColor Cyan
        Write-Host "  4. Wait 2-3 minutes for model to load" -ForegroundColor White
        Write-Host ""
    }
    default {
        Write-Host "Invalid selection" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "After Installation" -ForegroundColor Green  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "1. Restart Stable Diffusion: docker restart stable-diffusion" -ForegroundColor White
Write-Host "2. Wait 2-3 minutes for model to load" -ForegroundColor White
Write-Host "3. Test with the Pixel Art Agent app" -ForegroundColor White
Write-Host ""
