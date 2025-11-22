# Quick Start Guide - Pixel Art Agent

## Prerequisites Checklist

- [ ] Java 17 or higher installed
- [ ] Maven installed
- [ ] Ollama installed from https://ollama.ai/
- [ ] Ollama running on localhost:11434

## Quick Setup (5 minutes)

### Step 1: Install Ollama Model

```bash
ollama pull llama3.2
```

### Step 2: Verify Ollama is Running

```bash
ollama list
```

You should see `llama3.2` in the list.

### Step 3: Start the Application

**Option A - Using the start script:**
```bash
start.bat
```

**Option B - Using Maven directly:**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Step 4: Test the API

**Option A - Using PowerShell test script:**
```powershell
.\test-api.ps1
```

**Option B - Using cURL:**
```bash
curl http://localhost:8080/api/pixelart/health
```

## Your First Pixel Art Generation

### Using PowerShell:

```powershell
$body = @{
    assetType = "character"
    description = "A brave knight with a sword"
    style = "16-bit"
    colorPalette = "medieval"
    size = "32x32"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
```

### Using cURL:

```bash
curl -X POST http://localhost:8080/api/pixelart/generate \
  -H "Content-Type: application/json" \
  -d "{\"assetType\":\"character\",\"description\":\"A brave knight\",\"style\":\"16-bit\",\"colorPalette\":\"medieval\",\"size\":\"32x32\"}"
```

## What You'll Get Back

```json
{
  "detailedDescription": "A comprehensive description of the pixel art...",
  "suggestedColors": ["#2C3E50", "#E74C3C", "#ECF0F1", "#3498DB"],
  "specifications": {
    "size": "32x32",
    "assetType": "character",
    "frameCount": 3,
    "layers": ["background", "base", "details", "highlights"]
  },
  "animationSuggestions": ["idle", "walk", "attack"],
  "style": "16-bit",
  "generatedAt": "2025-11-22T10:30:00"
}
```

## Troubleshooting

### "Connection refused" error

**Problem:** Can't connect to Ollama  
**Solution:** Make sure Ollama is running:
```bash
ollama serve
```

### "Model not found" error

**Problem:** llama3.2 model not installed  
**Solution:** Pull the model:
```bash
ollama pull llama3.2
```

### Port 8080 already in use

**Problem:** Another application is using port 8080  
**Solution:** Change the port in `application.properties`:
```properties
server.port=8081
```

## Next Steps

1. **Explore Examples:** Check `EXAMPLES.md` for more request examples
2. **Customize Prompts:** Modify `PixelArtAgentService.java` to adjust prompts
3. **Try Different Models:** Change the model in `application.properties`:
   ```properties
   spring.ai.ollama.chat.options.model=mistral
   ```
4. **Adjust Temperature:** Control creativity in `application.properties`:
   ```properties
   spring.ai.ollama.chat.options.temperature=0.9
   ```

## API Endpoints Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/pixelart/health` | GET | Health check |
| `/api/pixelart/example` | GET | Get example request |
| `/api/pixelart/generate` | POST | Generate pixel art |
| `/api/pixelart/generate/variations` | POST | Generate variations |
| `/api/pixelart/refine` | POST | Refine with feedback |

## Support

For more details, see:
- `README.md` - Full documentation
- `EXAMPLES.md` - Request examples
- `application.properties` - Configuration options

Happy pixel art creating! 🎨
