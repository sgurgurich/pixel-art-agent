package com.pixelart.agent.service.model;

/**
 * Interface for image generation providers
 * Implementations can provide real or mocked responses
 */
public interface ImageProvider {
    /**
     * Generate an image from a text prompt
     * @param prompt The text prompt describing the image
     * @param width Image width
     * @param height Image height
     * @return Base64 encoded image data
     */
    String generateImage(String prompt, int width, int height);
    
    /**
     * Generate an image with optional spritesheet support
     * @param prompt The text prompt describing the image
     * @param width Image width for a single sprite
     * @param height Image height for a single sprite
     * @param isSpritesheet Whether to generate a spritesheet
     * @param frameCount Number of frames for spritesheet
     * @return Base64 encoded image data
     */
    String generateImage(String prompt, int width, int height, boolean isSpritesheet, int frameCount);
    
    /**
     * Check if the image provider is available
     * @return true if the provider is available and working
     */
    boolean isAvailable();
}
