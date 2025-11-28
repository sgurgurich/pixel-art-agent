# Pixel Art Agent 🎨

A Spring AI powered agent for generating detailed pixel art and 2D sprite descriptions with full abstraction for AI models and mock support for local development.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M3-blue.svg)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📑 Table of Contents

- [🎯 Quick Start](#-quick-start)
- [🚀 Key Features](#-key-features)
- [📋 Prerequisites](#-prerequisites)
- [⚙️ Configuration](#️-configuration)
- [🏗️ Model Abstraction](#️-model-abstraction-architecture)
- [📊 Configuration Reference](#-full-configuration-reference)
- [🎮 API Endpoints](#-api-endpoints)
- [📝 Usage Examples](#-usage-examples)
- [🏗️ Architecture](#️-architecture)
- [🎨 Supported Assets & Styles](#-supported-asset-types)
- [🖼️ Image Generation](#️-image-generation)
- [🛠️ Development](#️-development)
- [🐛 Troubleshooting](#-troubleshooting)
- [🚀 Deployment](#-deployment)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

## 🎯 Quick Start

**Run locally with mock models (no external dependencies):**

```bash
./mvnw spring-boot:run
```

**Test the API:**

```bash
curl -X POST http://localhost:8080/api/pixelart/generate \
  -H "Content-Type: application/json" \
  -d '{
    "assetType": "character",
    "description": "a warrior with sword",
    "style": "16-bit",
    "colorPalette": "vibrant",
    "size": "32x32"
  }'
```

## 🚀 Key Features

- ✅ **Run Offline** - Mock models enabled by default, no external dependencies needed
- 🤖 **AI-Powered** - Uses Spring AI with Ollama (Qwen 2.5:3b) for intelligent descriptions
- 🖼️ **Image Generation** - Stable Diffusion integration for pixel art sprite creation
- ⚙️ **Configuration-Driven** - Switch between mock and real models with a single property
- 🎞️ **Spritesheet Support** - Generate multi-frame animations automatically
- 🎮 **Game Assets** - Characters, weapons, tiles, enemies, items, UI elements
- 🎨 **Art Styles** - 8-bit, 16-bit, 32-bit, isometric, retro game styles
- 🧪 **Fully Testable** - Mock providers for instant testing
- 📊 **Technical Specs** - Color palettes, dimensions, animation suggestions
- 🔄 **Refinement** - Iteratively improve designs with feedback

## 📋 Prerequisites

- **Java 17 or higher**
- **Maven 3.6+**
- Optional: **Docker** (only needed for real Ollama and Stable Diffusion)
- Optional: **16 GB RAM** (only for running actual AI services)

## ⚙️ Configuration

### Run with Mock Models (Default - Recommended for Development)

By default, the app runs with mock providers that require **no external dependencies**:

```bash
./mvnw spring-boot:run
```

**Configuration:**
```properties
# In application.properties (default)
pixelart.models.use-mock=true
```

Mock mode features:
- ✅ Instant responses (milliseconds)
- ✅ No Ollama or Stable Diffusion needed
- ✅ Realistic cycling responses (warrior → chest → potion)
- ✅ Perfect for development and testing

### Switch to Real Models

To use actual AI models, update your configuration:

```properties
# In application.properties
pixelart.models.use-mock=false
pixelart.chat-model.mock-responses-enabled=false
pixelart.image-model.mock-responses-enabled=false
```

Then start the AI services:

```bash
# Terminal 1: Start Ollama
docker run -d --name ollama -p 11434:11434 -v ollama-data:/root/.ollama ollama/ollama
docker exec ollama ollama pull qwen2.5:3b

# Terminal 2: Start Stable Diffusion  
docker run -d --name stable-diffusion -p 7860:7860 \
  -v sd-models:/data/StableDiffusion \
  -e CLI_ARGS="--api --listen --port 7860 --no-half --precision full" \
  ghcr.io/neggles/sd-webui-docker:main

# Terminal 3: Start the app
./mvnw spring-boot:run
```

### Mixed Mode (Mock Chat + Real Images)

```properties
pixelart.models.use-mock=false
pixelart.chat-model.mock-responses-enabled=true   # Uses mock chat
pixelart.image-model.mock-responses-enabled=false # Uses real images
```

### Customize Mock Responses

In `application.properties`:
```properties
pixelart.mock.chat-responses[0]=Your custom warrior description
pixelart.mock.chat-responses[1]=Your custom chest description
pixelart.mock.chat-responses[2]=Your custom potion description
```

Or in `application.yml`:
```yaml
pixelart:
  mock:
    chat-responses:
      0: "Custom warrior response"
      1: "Custom chest response"
      2: "Custom potion response"
```

## 🏗️ Model Abstraction Architecture

The project now uses a clean abstraction layer for AI models:

### Two Models Abstracted

| Model | Purpose | Real Provider | Mock Provider |
|-------|---------|--------------|---------------|
| **Chat** | Generates pixel art descriptions | `OllamaModelProvider` | `MockModelProvider` |
| **Image** | Generates pixel art images | `StableDiffusionImageProvider` | `MockImageProvider` |

### Provider Interfaces

```java
// Chat model interface
public interface ModelProvider {
    String generateResponse(String prompt);
    boolean isAvailable();
}

// Image generation interface  
public interface ImageProvider {
    String generateImage(String prompt, int width, int height);
    String generateImage(String prompt, int width, int height, boolean isSpritesheet, int frameCount);
    boolean isAvailable();
}
```

### Configuration-Driven Selection

The `ModelConfig` bean automatically selects the correct providers:

```java
@Configuration
public class ModelConfig {
    
    @Bean
    public ModelProvider modelProvider(/* ... */) {
        if (properties.models.useMock()) {
            return new MockModelProvider(mockConfig);
        }
        return new OllamaModelProvider(chatClient);
    }
    
    @Bean
    public ImageProvider imageProvider(/* ... */) {
        if (properties.models.useMock()) {
            return new MockImageProvider();
        }
        return new StableDiffusionImageProvider();
    }
}
```

### Service Integration

Services now depend on abstractions, not concrete implementations:

```java
@Service
public class PixelArtAgentService {
    private final ModelProvider modelProvider;
    
    public PixelArtAgentService(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }
    
    public PixelArtResponse generatePixelArt(String prompt) {
        // Uses either real or mock provider based on config
        String description = modelProvider.generateResponse(prompt);
        return new PixelArtResponse(description);
    }
}
```

## 📊 Full Configuration Reference

### application.properties

```properties
# Server
server.port=8080

# Model Configuration (REFACTORED)
pixelart.models.use-mock=true
pixelart.chat-model.mock-responses-enabled=true
pixelart.image-model.mock-responses-enabled=true

# Ollama Settings
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen2.5:3b
spring.ai.ollama.chat.options.temperature=0.8
spring.ai.ollama.chat.options.top-p=0.9

# Image Generation
pixelart.image.generation.enabled=true
pixelart.image.generation.api-url=http://localhost:7860
pixelart.image.generation.model=stable-diffusion
pixelart.image.generation.lora=
pixelart.image.generation.lora-strength=0.8

# Mock Response Configuration
pixelart.mock.chat-responses[0]=Realistic warrior description...
pixelart.mock.chat-responses[1]=Realistic chest description...
pixelart.mock.chat-responses[2]=Realistic potion description...
```

### application.yml

```yaml
server:
  port: 8080

pixelart:
  models:
    use-mock: true                    # Master toggle for mock mode
  chat-model:
    mock-responses-enabled: true      # Override for chat model only
  image-model:
    mock-responses-enabled: true      # Override for image model only
  mock:
    chat-responses:
      0: "Warrior description..."
      1: "Chest description..."
      2: "Potion description..."

spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:3b
          temperature: 0.8
```



## 🎮 API Endpoints

### Generate Pixel Art with Image

**POST** `/api/pixelart/generate`

Generate a complete pixel art specification with actual sprite image.

```json
{
  "assetType": "CHARACTER",
  "description": "brave knight with sword and shield",
  "style": "16-bit",
  "colorPalette": "medieval",
  "size": "32x32",
  "additionalContext": "animation frames for walk cycle"
}
```

**Response:**

```json
{
  "detailedDescription": "### Character Asset Specification\n\n...",
  "suggestedColors": ["#2C3E50", "#E74C3C", "#ECF0F1", "#3498DB"],
  "specifications": {
    "size": "32x32",
    "assetType": "CHARACTER",
    "frameCount": 4,
    "orientation": "front-facing",
    "layers": ["background", "base", "details", "highlights"]
  },
  "animationSuggestions": ["idle", "walk"],
  "style": "16-bit",
  "imageData": "iVBORw0KGgoAAAANSUhEUgAA...",
  "imageStatus": "spritesheet-generated",
  "generatedAt": "2025-11-22T14:00:00"
}
```

### Generate Image Only

**POST** `/api/pixelart/generate/image`

Generate only the sprite image without description.

Returns: Binary PNG image file

### Generate Variations

**POST** `/api/pixelart/generate/variations?count=3`

Generate multiple variations of the same concept.

### Refine Design

**POST** `/api/pixelart/refine?feedback=make+it+more+colorful`

Refine an existing design based on feedback.

### Health Check

**GET** `/api/pixelart/health`

Check if all services are running and available.

## 📝 Usage Examples

### Example 1: Knight Character

```bash
curl -X POST http://localhost:8080/api/pixelart/generate \
  -H "Content-Type: application/json" \
  -d '{
    "assetType": "CHARACTER",
    "description": "brave knight with sword and shield",
    "style": "16-bit",
    "colorPalette": "medieval",
    "size": "32x32",
    "additionalContext": "idle and attack animations"
  }'
```

### Example 2: Fire Sword Weapon

```powershell
$body = @{
    assetType = "ITEM"
    description = "magical fire sword with flames"
    style = "16-bit"
    colorPalette = "vibrant"
    size = "32x16"
    additionalContext = "glowing effect"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body

# Save the generated image
$imageBytes = [Convert]::FromBase64String($response.imageData)
[IO.File]::WriteAllBytes("fire-sword.png", $imageBytes)
```

### Example 3: Slime Enemy with Spritesheet

```json
{
  "assetType": "ENEMY",
  "description": "cute bouncing slime monster",
  "style": "8-bit",
  "colorPalette": "pastel",
  "size": "16x16",
  "additionalContext": "bounce and damage animation frames"
}
```

### Example 4: Health Potion

```json
{
  "assetType": "ITEM",
  "description": "red health potion in glass bottle",
  "style": "16-bit",
  "colorPalette": "vibrant",
  "size": "16x16",
  "additionalContext": "sparkle effect"
}
```

### Example 5: Grass Tile

```json
{
  "assetType": "TILE",
  "description": "grass ground tile with flowers",
  "style": "16-bit",
  "colorPalette": "natural",
  "size": "32x32",
  "additionalContext": "seamless tiling"
}
```

### Example 6: Dragon Boss

```json
{
  "assetType": "ENEMY",
  "description": "fierce red dragon boss with wings",
  "style": "32-bit",
  "colorPalette": "vibrant",
  "size": "64x64",
  "additionalContext": "idle, fly, and breathe fire animations"
}
```

### Example 7: Wizard Character

```json
{
  "assetType": "CHARACTER",
  "description": "elderly wizard with staff and pointed hat",
  "style": "16-bit",
  "colorPalette": "mystical",
  "size": "32x32",
  "additionalContext": "casting spell animation"
}
```

### Example 8: Laser Gun

```json
{
  "assetType": "WEAPON",
  "description": "futuristic laser gun with neon accents",
  "style": "16-bit",
  "colorPalette": "neon",
  "size": "24x12",
  "additionalContext": "sci-fi theme with glowing elements"
}
```

## 🏗️ Architecture

```
┌─────────────┐    ┌──────────────────┐    ┌──────────────────┐
│   Client    │───▶│ REST Controller  │───▶│  Agent Service   │
└─────────────┘    └──────────────────┘    └──────────────────┘
                                                      │
                                    ┌─────────────────┴─────────────────┐
                                    ▼                                   ▼
                          ┌──────────────────┐              ┌──────────────────┐
                          │  Spring AI       │              │ Image Generation │
                          │  ChatClient      │              │    Service       │
                          └──────────────────┘              └──────────────────┘
                                    │                                   │
                                    ▼                                   ▼
                          ┌──────────────────┐              ┌──────────────────┐
                          │  Ollama Server   │              │ Stable Diffusion │
                          │  (Qwen 2.5:3b)   │              │   WebUI API      │
                          └──────────────────┘              └──────────────────┘
```

**See [Architecture Documentation](docs/Pixel-Art-Agent-Architecture.pdf) for detailed diagrams and explanation.**

## 🎨 Supported Asset Types

| Asset Type | Description | Typical Size | Examples |
|------------|-------------|--------------|----------|
| CHARACTER | Player characters, NPCs | 32x32, 64x64 | Knight, wizard, hero |
| ENEMY | Enemy sprites, bosses | 16x16, 32x32, 64x64 | Slime, dragon, skeleton |
| WEAPON | Swords, guns, items | 16x16, 32x16 | Sword, bow, gun |
| TILE | Ground, walls | 16x16, 32x32 | Grass, stone, water |
| ITEM | Collectibles, consumables | 8x8, 16x16 | Coins, potions, keys |
| UI | UI elements, icons | 16x16, 24x24 | Buttons, hearts, stars |
| EFFECT | Visual effects | 16x16, 32x32 | Explosion, sparkle, smoke |

## 🎨 Supported Art Styles

- **8-bit** - Classic NES-style (limited colors, chunky pixels)
- **16-bit** - SNES/Genesis era (more colors, smoother)
- **32-bit** - Advanced pixel art (detailed sprites)
- **isometric** - 3D-looking isometric perspective
- **retro** - Generic retro game style
- **pixel-art** - Modern pixel art style

## 🖼️ Image Generation

### How It Works

1. **Text Generation**: Ollama (Qwen 2.5:3b) generates detailed pixel art specifications
2. **Prompt Enhancement**: System adds strong pixel art style markers and constraints
3. **Image Generation**: Stable Diffusion creates the actual sprite image
4. **Spritesheet Detection**: Automatically creates multi-frame layouts for animations
5. **Response Assembly**: Combines description with base64-encoded PNG image

### Image Parameters

- **Steps**: 50 (higher for better style adherence)
- **CFG Scale**: 15 (maximum prompt guidance)
- **Sampler**: Euler a (good for pixel art)
- **Resolution**: 8x upscaled, then downscaled for pixel effect
- **Negative Prompts**: Excludes blur, gradients, anti-aliasing

### Saving Images

```powershell
# PowerShell example
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/pixelart/generate" `
    -Method POST -ContentType "application/json" -Body $json

if ($response.imageStatus -eq "generated" -or $response.imageStatus -eq "spritesheet-generated") {
    $imageBytes = [Convert]::FromBase64String($response.imageData)
    [IO.File]::WriteAllBytes("sprite.png", $imageBytes)
    Write-Host "Image saved to sprite.png"
}
```

```html
<!-- HTML example -->
<img src="data:image/png;base64,{{imageData}}" alt="Generated Sprite" />
```

### Configuration Options

```properties
# Enable/disable image generation
pixelart.image.generation.enabled=true

# Stable Diffusion API URL
pixelart.image.generation.api-url=http://localhost:7860

# Model identifier
pixelart.image.generation.model=stable-diffusion
```

## 🛠️ Development

### Project Structure

```
pixel-art-agent/
├── docs/                        # Documentation
│   ├── ARCHITECTURE.html        # Interactive architecture doc
│   └── Pixel-Art-Agent-Architecture.pdf
├── scripts/                     # Utility scripts
│   ├── setup-github.ps1         # GitHub setup
│   ├── setup-pixel-art-model.ps1 # Download pixel art LoRA
│   ├── start-stable-diffusion.bat # Start SD container
│   ├── start.bat                # Start application
│   └── test-api.ps1             # Test API endpoints
├── src/
│   ├── main/
│   │   ├── java/com/pixelart/agent/
│   │   │   ├── PixelArtAgentApplication.java
│   │   │   ├── config/
│   │   │   │   ├── ChatClientConfig.java
│   │   │   │   └── PixelArtProperties.java
│   │   │   ├── controller/
│   │   │   │   └── PixelArtController.java
│   │   │   ├── model/
│   │   │   │   ├── PixelArtRequest.java
│   │   │   │   └── PixelArtResponse.java
│   │   │   └── service/
│   │   │       ├── ImageGenerationService.java
│   │   │       └── PixelArtAgentService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

### Technologies

- **Spring Boot 3.3.5** - Application framework
- **Spring AI 1.0.0-M3** - AI integration framework
- **Ollama** - Local LLM runtime (Qwen 2.5:3b, 1.9 GB)
- **Stable Diffusion** - Image generation (v1.5 model, 4 GB)
- **Apache HttpClient 5** - HTTP communication
- **Lombok** - Reduce boilerplate code
- **Maven** - Build tool

### Building

```bash
# Clean build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run in development mode
mvn spring-boot:run
```

## 🐛 Troubleshooting

### Issue: Ollama Connection Refused

**Symptom:** `Connection refused: http://localhost:11434`

**Solution:**
```bash
# Check if Ollama is running
docker ps | grep ollama

# Start Ollama if not running
docker start ollama

# Check port mapping
docker port ollama
```

### Issue: Image Status "text-only" or Placeholder

**Symptom:** `imageStatus: "text-only"`, 70-byte images

**Solution:**
```bash
# Check Stable Diffusion logs
docker logs stable-diffusion --tail 50

# Verify API is accessible
curl http://localhost:7860/sdapi/v1/sd-models

# Restart with correct flags
.\scripts\start-stable-diffusion.bat
```

### Issue: Model Not Found

**Symptom:** `Error: model 'qwen2.5:3b' not found`

**Solution:**
```bash
# Pull the model
docker exec ollama ollama pull qwen2.5:3b

# Verify it's available
docker exec ollama ollama list
```

### Issue: Out of Memory

**Symptom:** Container crashes or system freezes

**Solution:**
- Increase Docker memory limit (Settings → Resources → Memory)
- Close unnecessary applications
- Consider using smaller Qwen model (1.8b instead of 3b)
- Use GPU acceleration if available

### Issue: Slow Image Generation

**Symptom:** Takes 30-60 seconds to generate images

**Solution:**
- This is normal for CPU mode
- For faster generation (2-5 seconds), use GPU:
  ```bash
  docker run -d --gpus all --name stable-diffusion ...
  ```
- Or disable image generation:
  ```properties
  pixelart.image.generation.enabled=false
  ```

### Issue: Port Already in Use

**Symptom:** Port 8080 already in use

**Solution:**
```properties
# Change port in application.properties
server.port=8081
```

## 🚀 Deployment

### Docker Compose Setup

```yaml
version: '3.8'
services:
  ollama:
    image: ollama/ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama-data:/root/.ollama

  stable-diffusion:
    image: ghcr.io/neggles/sd-webui-docker:main
    ports:
      - "7860:7860"
    volumes:
      - sd-models:/data/StableDiffusion
    environment:
      - CLI_ARGS=--api --listen --port 7860 --no-half --precision full --disable-nan-check

  pixel-art-agent:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - ollama
      - stable-diffusion
    environment:
      - SPRING_AI_OLLAMA_BASE_URL=http://ollama:11434
      - PIXELART_IMAGE_GENERATION_API_URL=http://stable-diffusion:7860

volumes:
  ollama-data:
  sd-models:
```

### System Requirements

| Component | RAM | Storage | CPU |
|-----------|-----|---------|-----|
| Spring Boot App | 1-2 GB | 100 MB | 2 cores |
| Ollama + Qwen 2.5:3b | 4-6 GB | 2 GB | 4 cores |
| Stable Diffusion | 8-16 GB | 6-8 GB | 8+ cores |
| **Total Minimum** | **16 GB** | **10 GB** | **8 cores** |

**With GPU**: 12 GB VRAM recommended for optimal Stable Diffusion performance

## 🤝 Contributing

Contributions are welcome! Areas for improvement:

- 🎨 Additional art styles and templates
- 🖼️ Alternative image generation models
- 📚 More comprehensive documentation
- 🐛 Bug fixes and performance improvements
- 🧪 Test coverage
- 🌐 Additional LLM model support
- 🎮 Game engine integrations (Unity, Godot)

### Development Guidelines

- Follow Java conventions
- Use Lombok annotations where appropriate
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Write meaningful commit messages
- Test thoroughly before submitting

### Pull Request Process

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Test thoroughly
5. Commit with clear messages: `git commit -m 'Add amazing feature'`
6. Push to your fork: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details

## 🙏 Acknowledgments

- **Ollama** for local LLM inference
- **Qwen Team** for the excellent Qwen 2.5 model
- **AUTOMATIC1111** for Stable Diffusion WebUI
- **Spring AI Team** for the AI integration framework
- **Pixel art community** for inspiration and feedback

## 📧 Support

- 📖 [Architecture Documentation](docs/Pixel-Art-Agent-Architecture.pdf)
- 🐛 [Report Issues](https://github.com/sgurgurich/pixel-art-agent/issues)
- 💬 [Discussions](https://github.com/sgurgurich/pixel-art-agent/discussions)

---

Made with ❤️ for game developers and pixel art enthusiasts
