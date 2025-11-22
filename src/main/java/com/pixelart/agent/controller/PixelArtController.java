package com.pixelart.agent.controller;

import com.pixelart.agent.model.PixelArtRequest;
import com.pixelart.agent.model.PixelArtResponse;
import com.pixelart.agent.service.PixelArtAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for pixel art generation endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/pixelart")
@RequiredArgsConstructor
public class PixelArtController {

    private final PixelArtAgentService pixelArtAgentService;

    /**
     * Generate a single pixel art description
     */
    @PostMapping("/generate")
    public ResponseEntity<PixelArtResponse> generatePixelArt(@RequestBody PixelArtRequest request) {
        log.info("Received request to generate pixel art: {}", request.getAssetType());
        
        try {
            PixelArtResponse response = pixelArtAgentService.generatePixelArt(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating pixel art", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate multiple variations of pixel art
     */
    @PostMapping("/generate/variations")
    public ResponseEntity<List<PixelArtResponse>> generateVariations(
            @RequestBody PixelArtRequest request,
            @RequestParam(defaultValue = "3") int count) {
        
        log.info("Received request to generate {} variations", count);
        
        try {
            List<PixelArtResponse> variations = pixelArtAgentService.generateVariations(request, count);
            return ResponseEntity.ok(variations);
        } catch (Exception e) {
            log.error("Error generating variations", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Refine pixel art based on feedback
     */
    @PostMapping("/refine")
    public ResponseEntity<PixelArtResponse> refinePixelArt(
            @RequestBody PixelArtRequest request,
            @RequestParam String feedback) {
        
        log.info("Received request to refine pixel art with feedback");
        
        try {
            PixelArtResponse response = pixelArtAgentService.refinePixelArt(request, feedback);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error refining pixel art", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Pixel Art Agent",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Get example request for documentation
     */
    @GetMapping("/example")
    public ResponseEntity<PixelArtRequest> getExample() {
        PixelArtRequest example = new PixelArtRequest();
        example.setAssetType("character");
        example.setDescription("A brave knight with a sword and shield");
        example.setStyle("16-bit");
        example.setColorPalette("medieval");
        example.setSize("32x32");
        example.setAdditionalContext("Should have idle and attack animations");
        
        return ResponseEntity.ok(example);
    }

    /**
     * Download generated image as PNG file
     */
    @GetMapping("/image/{responseId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String responseId, @RequestParam String imageData) {
        try {
            // Decode base64 image data
            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Content-Disposition", "attachment; filename=\"pixel-art-" + responseId + ".png\"")
                    .body(imageBytes);
        } catch (Exception e) {
            log.error("Error downloading image", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
