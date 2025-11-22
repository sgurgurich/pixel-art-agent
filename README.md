# Pixel Art Agent 🎨

A Spring AI powered agent for generating detailed pixel art and 2D sprite descriptions for video games using Ollama, with optional Stable Diffusion image generation.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M3-blue.svg)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🚀 Features

- 🤖 **AI-Powered Generation** - Uses Spring AI with Ollama (qwen3-vl:4b) for intelligent sprite descriptions
- 🖼️ **Image Generation** - Optional Stable Diffusion integration for actual pixel art images
- 🎮 **Game Asset Types** - Characters, weapons, tiles, enemies, items, UI elements
- 🎨 **Multiple Art Styles** - 8-bit, 16-bit, 32-bit, isometric
- 🔄 **Variations** - Generate multiple design variations
- ✨ **Refinement** - Iteratively improve designs with feedback
- 📊 **Technical Specs** - Color palettes, dimensions, animation suggestions
- 🐳 **Docker Support** - Easy deployment with Docker containers
- 📋 **REST API** - Simple JSON-based endpoints

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for Ollama and optional Stable Diffusion)
- [Ollama](https://ollama.ai/) with qwen3-vl:4b model

## Setup

### 1. Install and Start Ollama

```bash
# Install Ollama from https://ollama.ai/

# Pull the default model
ollama pull llama3.2

# Start Ollama (it usually runs automatically)
ollama serve
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Ollama connection
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.8

# Application settings
pixelart.agent.max-iterations=3
```

## API Endpoints

### Generate Pixel Art

**POST** `/api/pixelart/generate`

Generate a detailed pixel art description.

```json
{
  "assetType": "character",
  "description": "A brave knight with a sword and shield",
  "style": "16-bit",
  "colorPalette": "medieval",
  "size": "32x32",
  "additionalContext": "Should have idle and attack animations"
}
```

### Generate Variations

**POST** `/api/pixelart/generate/variations?count=3`

Generate multiple variations of the same concept.

### Refine Pixel Art

**POST** `/api/pixelart/refine?feedback=make+it+more+colorful`

Refine an existing design based on feedback.

### Health Check

**GET** `/api/pixelart/health`

Check if the service is running.

### Get Example Request

**GET** `/api/pixelart/example`

Get an example request payload for reference.

## Example Usage

### Using cURL

```bash
curl -X POST http://localhost:8080/api/pixelart/generate \
  -H "Content-Type: application/json" \
  -d '{
    "assetType": "weapon",
    "description": "A magical fire sword",
    "style": "16-bit",
    "colorPalette": "vibrant",
    "size": "32x16"
  }'
```

### Using PowerShell

```powershell
$body = @{
    assetType = "character"
    description = "A cute slime enemy"
    style = "8-bit"
    colorPalette = "pastel"
    size = "16x16"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body
```

## Response Structure

```json
{
  "detailedDescription": "Detailed visual description...",
  "suggestedColors": ["#2C3E50", "#E74C3C", "#ECF0F1", "#3498DB", "#F39C12"],
  "specifications": {
    "size": "32x32",
    "assetType": "character",
    "frameCount": 4,
    "orientation": "front-facing",
    "layers": ["background", "base", "details", "highlights"]
  },
  "animationSuggestions": ["idle", "walk", "attack"],
  "style": "16-bit",
  "generatedAt": "2025-11-22T10:30:00",
  "prompt": "Full prompt used for generation..."
}
```

## Supported Asset Types

- **character** - Player characters, NPCs
- **enemy** - Enemy sprites and bosses
- **weapon** - Swords, guns, magical items
- **tile** - Ground tiles, wall tiles
- **item** - Collectibles, power-ups
- **ui** - UI elements, icons
- **effect** - Visual effects, particles

## Supported Styles

- **8-bit** - Classic NES-style pixel art
- **16-bit** - SNES/Genesis era
- **32-bit** - Advanced pixel art with more detail
- **isometric** - 3D-looking isometric perspective

## Architecture

```
┌─────────────┐      ┌──────────────────┐      ┌─────────┐
│   Client    │─────▶│  REST Controller │─────▶│ Service │
└─────────────┘      └──────────────────┘      └─────────┘
                                                     │
                                                     ▼
                                              ┌─────────────┐
                                              │ Spring AI   │
                                              │ ChatClient  │
                                              └─────────────┘
                                                     │
                                                     ▼
                                              ┌─────────────┐
                                              │   Ollama    │
                                              │  (llama3.2) │
                                              └─────────────┘
```

## Development

### Project Structure

```
pixel-art-agent/
├── src/
│   ├── main/
│   │   ├── java/com/pixelart/agent/
│   │   │   ├── PixelArtAgentApplication.java
│   │   │   ├── controller/
│   │   │   │   └── PixelArtController.java
│   │   │   ├── service/
│   │   │   │   └── PixelArtAgentService.java
│   │   │   └── model/
│   │   │       ├── PixelArtRequest.java
│   │   │       └── PixelArtResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Technologies

- **Spring Boot 3.2.0** - Application framework
- **Spring AI 1.0.0-M3** - AI integration framework
- **Ollama** - Local LLM runtime
- **Lombok** - Reduce boilerplate code
- **Maven** - Build tool

## Troubleshooting

### Ollama not connecting

Ensure Ollama is running:
```bash
ollama serve
```

Check if the model is available:
```bash
ollama list
```

### Model not found

Pull the required model:
```bash
ollama pull llama3.2
```

### Change the AI model

Edit `application.properties`:
```properties
spring.ai.ollama.chat.options.model=mistral
```

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
