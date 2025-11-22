@echo off
echo ========================================
echo Starting Stable Diffusion WebUI
echo ========================================
echo.

echo Starting Stable Diffusion container...
docker run -d ^
  --name stable-diffusion ^
  -p 7860:7860 ^
  -e CLI_ARGS="--api --listen --port 7860" ^
  ghcr.io/neggles/sd-webui-docker:main

if %errorlevel% equ 0 (
    echo.
    echo ✓ Stable Diffusion is starting!
    echo.
    echo Please wait 1-2 minutes for the service to fully start.
    echo Then access it at: http://localhost:7860
    echo.
    echo To view logs: docker logs -f stable-diffusion
    echo To stop: docker stop stable-diffusion
    echo.
) else (
    echo.
    echo ✗ Failed to start Stable Diffusion
    echo.
)

pause
