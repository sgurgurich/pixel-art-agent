# Image Generation Setup Guide

The Pixel Art Agent now includes **actual image generation** capabilities using Stable Diffusion!

## Response Format

When you call the `/api/pixelart/generate` endpoint, you'll now receive:

```json
{
  "detailedDescription": "AI-generated description...",
  "suggestedColors": ["#2C3E50", "#E74C3C", ...],
  "specifications": { ... },
  "imageData": "iVBORw0KGgoAAAANSUhEUgAA...",  // Base64 encoded PNG
  "imageStatus": "generated",  // or "unavailable", "error: ..."
  ...
}
```

## Image Generation Options

### Option 1: Stable Diffusion (Recommended for Quality)

1. **Install AUTOMATIC1111 Stable Diffusion WebUI:**
   ```bash
   # Clone the repository
   git clone https://github.com/AUTOMATIC1111/stable-diffusion-webui.git
   cd stable-diffusion-webui
   
   # Run the webui (Windows)
   ./webui-user.bat --api
   ```

2. **Configure the agent** (already done in `application.properties`):
   ```properties
   pixelart.image.generation.enabled=true
   pixelart.image.generation.api-url=http://localhost:7860
   ```

3. The agent will automatically connect to Stable Diffusion

### Option 2: Docker Stable Diffusion

Run Stable Diffusion in Docker:

```bash
docker run -d \
  -p 7860:7860 \
  --name stable-diffusion \
  -v sd-data:/data \
  --gpus all \
  sd-webui:latest
```

### Option 3: Placeholder Mode

If no image generation service is available, the agent will:
- Return `imageStatus: "unavailable"`
- Include a placeholder base64 image
- Still provide the full text description

## Using the Image Data

### Display in Browser (HTML)

```html
<img src="data:image/png;base64,{{imageData}}" alt="Generated Pixel Art" />
```

### Save to File (PowerShell)

```powershell
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body

# Decode and save the image
$imageBytes = [Convert]::FromBase64String($response.imageData)
[IO.File]::WriteAllBytes("pixel-art.png", $imageBytes)
```

### Download via Endpoint

```bash
# Use the download endpoint
curl "http://localhost:8080/api/pixelart/image/123?imageData={{base64data}}" -o pixel-art.png
```

## Configuration Options

In `application.properties`:

```properties
# Enable/disable image generation
pixelart.image.generation.enabled=true

# Stable Diffusion API URL
pixelart.image.generation.api-url=http://localhost:7860

# Model identifier (for future use)
pixelart.image.generation.model=stable-diffusion
```

## Optimizations for Pixel Art

The agent automatically enhances prompts for pixel art generation by adding:
- "pixel art" style keywords
- "crisp pixels" and "retro game art"
- "limited color palette"
- Negative prompts to avoid smooth/blurry results

## Troubleshooting

### Image Status: "unavailable"

**Cause:** Stable Diffusion is not running  
**Solution:** Start Stable Diffusion WebUI with `--api` flag

### Image Status: "error: Connection refused"

**Cause:** Wrong API URL or SD not accessible  
**Solution:** Check `pixelart.image.generation.api-url` in properties

### Slow Generation

**Cause:** Image generation takes time (10-30 seconds)  
**Solution:** This is normal for AI image generation

### No GPU

**Cause:** Stable Diffusion needs GPU for fast generation  
**Solution:** 
- Use CPU mode (very slow)
- Use cloud-based Stable Diffusion API
- Disable image generation: `pixelart.image.generation.enabled=false`

## Example: Full Workflow

```powershell
# 1. Generate pixel art with image
$body = @{
    assetType = "character"
    description = "A brave knight with sword"
    style = "16-bit"
    size = "64x64"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body

# 2. Check status
Write-Host "Image Status: $($response.imageStatus)"

# 3. Save the image
if ($response.imageStatus -eq "generated") {
    $imageBytes = [Convert]::FromBase64String($response.imageData)
    [IO.File]::WriteAllBytes("my-knight.png", $imageBytes)
    Write-Host "Image saved to my-knight.png"
}

# 4. View the description
Write-Host "Description: $($response.detailedDescription)"
Write-Host "Colors: $($response.suggestedColors -join ', ')"
```

## Performance Notes

- **With Stable Diffusion:** 10-30 seconds per image
- **Without (placeholder mode):** < 1 second
- **Variations endpoint:** Generates N images sequentially

## Future Enhancements

- Batch image generation
- Alternative image models (DALL-E, Midjourney APIs)
- Image upscaling for higher resolution
- Animation frame generation
- Custom model fine-tuning for pixel art
