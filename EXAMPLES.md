# Example API Requests for Pixel Art Agent

## Example 1: Knight Character

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

## Example 2: Fire Sword Weapon

```json
{
  "assetType": "weapon",
  "description": "A magical fire sword with flames",
  "style": "16-bit",
  "colorPalette": "vibrant",
  "size": "32x16",
  "additionalContext": "Should have a glowing effect"
}
```

## Example 3: Slime Enemy

```json
{
  "assetType": "enemy",
  "description": "A cute bouncing slime monster",
  "style": "8-bit",
  "colorPalette": "pastel",
  "size": "16x16",
  "additionalContext": "Should have bounce and damage animations"
}
```

## Example 4: Health Potion Item

```json
{
  "assetType": "item",
  "description": "A red health potion in a glass bottle",
  "style": "16-bit",
  "colorPalette": "vibrant",
  "size": "16x16",
  "additionalContext": "Should sparkle or glow slightly"
}
```

## Example 5: Grass Tile

```json
{
  "assetType": "tile",
  "description": "A grass ground tile with small flowers",
  "style": "16-bit",
  "colorPalette": "natural",
  "size": "32x32",
  "additionalContext": "Should tile seamlessly"
}
```

## Example 6: Dragon Boss

```json
{
  "assetType": "enemy",
  "description": "A fierce red dragon boss with wings",
  "style": "32-bit",
  "colorPalette": "vibrant",
  "size": "64x64",
  "additionalContext": "Should have idle, fly, and breathe fire animations"
}
```

## Example 7: Coin Collectible

```json
{
  "assetType": "item",
  "description": "A spinning gold coin",
  "style": "8-bit",
  "colorPalette": "retro",
  "size": "8x8",
  "additionalContext": "Should have rotation animation frames"
}
```

## Example 8: Wizard Character

```json
{
  "assetType": "character",
  "description": "An elderly wizard with a staff and pointed hat",
  "style": "16-bit",
  "colorPalette": "mystical",
  "size": "32x32",
  "additionalContext": "Should have casting spell animation"
}
```

## Example 9: Stone Wall Tile

```json
{
  "assetType": "tile",
  "description": "A stone brick wall tile",
  "style": "isometric",
  "colorPalette": "monochrome",
  "size": "32x32",
  "additionalContext": "Isometric perspective for a dungeon"
}
```

## Example 10: Laser Gun Weapon

```json
{
  "assetType": "weapon",
  "description": "A futuristic laser gun with neon accents",
  "style": "16-bit",
  "colorPalette": "neon",
  "size": "24x12",
  "additionalContext": "Sci-fi theme with glowing elements"
}
```

## Testing with cURL

```bash
# Test the generate endpoint
curl -X POST http://localhost:8080/api/pixelart/generate \
  -H "Content-Type: application/json" \
  -d @example1.json

# Test variations
curl -X POST "http://localhost:8080/api/pixelart/generate/variations?count=3" \
  -H "Content-Type: application/json" \
  -d @example2.json

# Test refinement
curl -X POST "http://localhost:8080/api/pixelart/refine?feedback=make+it+more+colorful" \
  -H "Content-Type: application/json" \
  -d @example3.json
```

## Testing with PowerShell

```powershell
# Example 1: Knight Character
$body = @{
    assetType = "character"
    description = "A brave knight with a sword and shield"
    style = "16-bit"
    colorPalette = "medieval"
    size = "32x32"
    additionalContext = "Should have idle and attack animations"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10

# Example 2: Fire Sword
$body = @{
    assetType = "weapon"
    description = "A magical fire sword with flames"
    style = "16-bit"
    colorPalette = "vibrant"
    size = "32x16"
    additionalContext = "Should have a glowing effect"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pixelart/generate" `
    -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
```
