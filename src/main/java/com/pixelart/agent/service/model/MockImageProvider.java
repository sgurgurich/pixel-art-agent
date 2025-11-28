package com.pixelart.agent.service.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock image provider for local development and testing
 * Returns spoofed base64 encoded pixel art images
 */
@Slf4j
@Component
public class MockImageProvider implements ImageProvider {
    
    // 1x1 transparent PNG as base64
    private static final String PLACEHOLDER_IMAGE = 
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
    
    // 8x8 colored pixel pattern PNG (red and blue checkerboard)
    private static final String COLORED_PIXEL_IMAGE = 
        "iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAAJ0lEQVQY02P4/x8DAxgYGBhgAIwMjAyMjAzwAKMDrAyMjIxgEgAA//8DAK0DDhU2XoYAAAAASUVORK5CYII=";
    
    private int callCount = 0;
    
    @Override
    public String generateImage(String prompt, int width, int height) {
        return generateImage(prompt, width, height, false, 1);
    }
    
    @Override
    public String generateImage(String prompt, int width, int height, boolean isSpritesheet, int frameCount) {
        log.info("MockImageProvider: Generating spoofed image for prompt: '{}', dimensions: {}x{}, spritesheet: {}", 
            prompt.substring(0, Math.min(50, prompt.length())), width, height, isSpritesheet);
        
        callCount++;
        
        // Alternate between different responses
        String response = callCount % 2 == 0 ? PLACEHOLDER_IMAGE : COLORED_PIXEL_IMAGE;
        
        log.debug("MockImageProvider: Returning spoofed image #{} (base64 length: {})", callCount, response.length());
        return response;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
