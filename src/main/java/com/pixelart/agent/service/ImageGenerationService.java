package com.pixelart.agent.service;

import com.pixelart.agent.service.model.ImageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for generating pixel art images using abstracted image provider
 */
@Slf4j
@Service
public class ImageGenerationService {

    private final ImageProvider imageProvider;
    
    public ImageGenerationService(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }

    /**
     * Generate a pixel art image from a text prompt
     * 
     * @param prompt The detailed description to generate image from
     * @param width Image width in pixels
     * @param height Image height in pixels
     * @return Base64 encoded PNG image, or null if generation fails
     */
    public String generateImage(String prompt, int width, int height) {
        return imageProvider.generateImage(prompt, width, height);
    }

    /**
     * Generate a pixel art image with optional spritesheet support
     * 
     * @param prompt The detailed description to generate image from
     * @param width Image width in pixels for single sprite
     * @param height Image height in pixels for single sprite
     * @param isSpritesheet Whether to generate a spritesheet with multiple frames
     * @param frameCount Number of frames for spritesheet (only used if isSpritesheet is true)
     * @return Base64 encoded PNG image, or null if generation fails
     */
    public String generateImage(String prompt, int width, int height, boolean isSpritesheet, int frameCount) {
        return imageProvider.generateImage(prompt, width, height, isSpritesheet, frameCount);
    }

    /**
     * Check if image generation service is available
     */
    public boolean isAvailable() {
        return imageProvider.isAvailable();
    }
}
