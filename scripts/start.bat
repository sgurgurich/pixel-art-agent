@echo off
echo ========================================
echo Pixel Art Agent - Quick Start
echo ========================================
echo.

echo Checking if Ollama is running...
curl -s http://localhost:11434/api/tags >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Ollama is not running!
    echo Please start Ollama first: ollama serve
    echo.
    pause
    exit /b 1
)

echo Ollama is running! ✓
echo.

echo Checking if llama3.2 model is available...
ollama list | findstr "llama3.2" >nul 2>&1
if %errorlevel% neq 0 (
    echo Model llama3.2 not found. Would you like to pull it? (Y/N)
    set /p choice=
    if /i "%choice%"=="Y" (
        echo Pulling llama3.2...
        ollama pull llama3.2
    ) else (
        echo Continuing without pulling model...
    )
)

echo.
echo Starting Pixel Art Agent...
echo.

mvn spring-boot:run

pause
