# GitHub Setup Script for Pixel Art Agent

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Pixel Art Agent - GitHub Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if git is initialized
if (-not (Test-Path .git)) {
    Write-Host "ERROR: Git not initialized. Run 'git init' first." -ForegroundColor Red
    exit 1
}

# Get current status
Write-Host "Current Git Status:" -ForegroundColor Yellow
git status --short
Write-Host ""

# Instructions for GitHub
Write-Host "📋 Next Steps to Publish to GitHub:" -ForegroundColor Green
Write-Host ""
Write-Host "1. Create a new repository on GitHub:" -ForegroundColor White
Write-Host "   - Go to: https://github.com/new" -ForegroundColor Gray
Write-Host "   - Name: pixel-art-agent" -ForegroundColor Gray
Write-Host "   - Description: Spring AI agent for generating pixel art using Ollama" -ForegroundColor Gray
Write-Host "   - Make it Public or Private" -ForegroundColor Gray
Write-Host "   - Do NOT initialize with README (we already have one)" -ForegroundColor Gray
Write-Host ""

Write-Host "2. Once created, run these commands:" -ForegroundColor White
Write-Host ""
Write-Host "   # Replace YOUR_USERNAME with your GitHub username" -ForegroundColor Gray
Write-Host "   git remote add origin https://github.com/YOUR_USERNAME/pixel-art-agent.git" -ForegroundColor Cyan
Write-Host "   git branch -M main" -ForegroundColor Cyan
Write-Host "   git push -u origin main" -ForegroundColor Cyan
Write-Host ""

Write-Host "3. Alternative - using GitHub CLI (gh):" -ForegroundColor White
Write-Host ""
Write-Host "   gh repo create pixel-art-agent --public --source=. --remote=origin" -ForegroundColor Cyan
Write-Host "   git push -u origin main" -ForegroundColor Cyan
Write-Host ""

# Show what will be uploaded
Write-Host "📦 Files ready to upload:" -ForegroundColor Yellow
Write-Host ""
git ls-files | ForEach-Object {
    Write-Host "   ✓ $_" -ForegroundColor Green
}
Write-Host ""

Write-Host "Total lines of code:" -ForegroundColor Yellow
$totalLines = (git ls-files | Where-Object { $_ -match '\.(java|xml|properties|md)$' } | ForEach-Object { (Get-Content $_ | Measure-Object -Line).Lines } | Measure-Object -Sum).Sum
Write-Host "   $totalLines lines" -ForegroundColor Cyan
Write-Host ""

# Ask if user wants to set remote now
Write-Host "Would you like to add a GitHub remote now? (y/n): " -ForegroundColor Yellow -NoNewline
$response = Read-Host

if ($response -eq 'y' -or $response -eq 'Y') {
    Write-Host ""
    Write-Host "Enter your GitHub username: " -ForegroundColor Yellow -NoNewline
    $username = Read-Host
    
    $repoUrl = "https://github.com/$username/pixel-art-agent.git"
    
    Write-Host ""
    Write-Host "Adding remote: $repoUrl" -ForegroundColor Cyan
    git remote add origin $repoUrl
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Remote added successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Now run: git push -u origin main" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Failed to add remote. Please check the URL and try again." -ForegroundColor Red
    }
} else {
    Write-Host ""
    Write-Host "✓ Setup complete! Follow the manual steps above when ready." -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
