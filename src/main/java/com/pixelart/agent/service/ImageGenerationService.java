package com.pixelart.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating actual pixel art images using Stable Diffusion
 */
@Slf4j
@Service
public class ImageGenerationService {

    @Value("${pixelart.image.generation.enabled:true}")
    private boolean imageGenerationEnabled;

    @Value("${pixelart.image.generation.api-url:http://localhost:7860}")
    private String sdApiUrl;

    @Value("${pixelart.image.generation.model:stable-diffusion}")
    private String model;

    @Value("${pixelart.image.generation.lora:}")
    private String loraModel;

    @Value("${pixelart.image.generation.lora-strength:0.8}")
    private double loraStrength;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate a pixel art image from a text prompt
     * 
     * @param prompt The detailed description to generate image from
     * @param width Image width in pixels
     * @param height Image height in pixels
     * @return Base64 encoded PNG image, or null if generation fails
     */
    public String generateImage(String prompt, int width, int height) {
        return generateImage(prompt, width, height, false, 1);
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
        if (!imageGenerationEnabled) {
            log.info("Image generation is disabled");
            return null;
        }

        try {
            int finalWidth = width;
            int finalHeight = height;
            
            // For spritesheets, multiply dimensions
            if (isSpritesheet && frameCount > 1) {
                finalWidth = width * frameCount;  // Horizontal spritesheet
                log.info("Generating spritesheet with {} frames ({}x{})", frameCount, finalWidth, finalHeight);
            } else {
                log.info("Generating single sprite image ({}x{})", finalWidth, finalHeight);
            }
            
            // Try Stable Diffusion API first
            String image = generateWithStableDiffusion(prompt, finalWidth, finalHeight, isSpritesheet, frameCount);
            
            if (image != null) {
                return image;
            }
            
            // Fallback: Generate a simple placeholder
            return generatePlaceholder(finalWidth, finalHeight);
            
        } catch (Exception e) {
            log.error("Error generating image", e);
            return generatePlaceholder(width, height);
        }
    }

    /**
     * Generate image using Stable Diffusion API (AUTOMATIC1111 or similar)
     */
    private String generateWithStableDiffusion(String prompt, int width, int height, boolean isSpritesheet, int frameCount) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManagerShared(false)
                .build()) {
            
            // Prepare the request
            HttpPost request = new HttpPost(sdApiUrl + "/sdapi/v1/txt2img");
            
            // Build the payload with EXTREME pixel art constraints
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", enhancePromptForPixelArt(prompt, isSpritesheet, frameCount));
            payload.put("negative_prompt", 
                "blurry, smooth, realistic, photograph, photorealistic, 3d render, detailed textures, " +
                "soft edges, gradients, anti-aliasing, high resolution, smooth shading, dithering, " +
                "detailed, complex, modern graphics, HD, 4K, ray tracing, ambient occlusion");
            payload.put("steps", 50);  // More steps for better adherence
            payload.put("width", width * 8);   // Generate at 8x size
            payload.put("height", height * 8); // Then downscale for pixel effect
            payload.put("cfg_scale", 15);  // Maximum guidance for style adherence
            payload.put("sampler_name", "Euler a");
            payload.put("seed", -1);
            payload.put("denoising_strength", 0.4);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            
            // Execute request
            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                log.debug("Stable Diffusion API response status: {}", statusCode);
                
                if (statusCode == 200) {
                    try {
                        // Read response body using EntityUtils
                        String responseBody = org.apache.hc.core5.http.io.entity.EntityUtils.toString(response.getEntity());
                        log.debug("Received response body (first 100 chars): {}", 
                            responseBody.substring(0, Math.min(100, responseBody.length())));
                        
                        JsonNode jsonResponse = objectMapper.readTree(responseBody);
                        
                        // Extract the first image from the response
                        if (jsonResponse.has("images") && jsonResponse.get("images").size() > 0) {
                            String base64Image = jsonResponse.get("images").get(0).asText();
                            log.info("Successfully generated {} with Stable Diffusion (image size: {} bytes)", 
                                isSpritesheet ? frameCount + "-frame spritesheet" : "sprite image",
                                base64Image.length());
                            return base64Image;
                        } else {
                            log.warn("Response missing 'images' field or empty array");
                        }
                    } catch (Exception e) {
                        log.error("Error parsing Stable Diffusion response", e);
                    }
                }
                log.warn("Stable Diffusion API returned status: {}", statusCode);
                return null;
            });
            
        } catch (Exception e) {
            log.error("Stable Diffusion API error at {}", sdApiUrl, e);
            return null;
        }
    }

    /**
     * Enhance the prompt for better pixel art generation
     */
    private String enhancePromptForPixelArt(String prompt, boolean isSpritesheet, int frameCount) {
        StringBuilder enhancedPrompt = new StringBuilder();
        
        // Add LoRA trigger if configured
        if (loraModel != null && !loraModel.isEmpty()) {
            enhancedPrompt.append(String.format("<lora:%s:%.1f> ", loraModel, loraStrength));
        }
        
        // VERY STRONG pixel art style markers
        enhancedPrompt.append("((pixel art)), ((16-bit)), ((retro game sprite)), ");
        enhancedPrompt.append("((NES style)), ((SNES style)), ((Game Boy Advance)), ");
        
        if (isSpritesheet && frameCount > 1) {
            // Spritesheet specific instructions
            enhancedPrompt.append(String.format(
                "((sprite sheet)), %d animation frames side by side, ", frameCount));
            enhancedPrompt.append("horizontal sprite strip, game animation frames, ");
        }
        
        // Add the actual description
        enhancedPrompt.append(prompt);
        
        // EXTREME quality constraints for pixel art
        enhancedPrompt.append(", ((8-bit graphics)), ((low resolution)), ((chunky pixels)), ");
        enhancedPrompt.append("((grid aligned)), ((limited colors)), ((4-color palette)), ");
        enhancedPrompt.append("((hard edges)), ((no anti-aliasing)), ((no gradients)), ");
        enhancedPrompt.append("((pixel perfect)), ((retro gaming)), ((classic arcade)), ");
        enhancedPrompt.append("((simple shapes)), ((clear silhouette)), ((iconic sprite)), ");
        enhancedPrompt.append("Super Nintendo, Sega Genesis, Pokemon Red Blue style, ");
        enhancedPrompt.append("Final Fantasy 6 sprite, Chrono Trigger sprite, ");
        enhancedPrompt.append("sharp pixel borders, no blur, retro 90s game asset");
        
        return enhancedPrompt.toString();
    }

    /**
     * Generate a simple placeholder image as Base64
     * This creates a minimal PNG as a fallback
     */
    private String generatePlaceholder(int width, int height) {
        log.info("Generating placeholder image ({}x{})", width, height);
        
        // Simple 1x1 transparent PNG encoded as base64
        // In a real implementation, you might want to generate a proper placeholder
        String transparentPixel = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
        
        return transparentPixel;
    }

    /**
     * Check if image generation service is available
     */
    public boolean isAvailable() {
        if (!imageGenerationEnabled) {
            return false;
        }
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(sdApiUrl + "/sdapi/v1/options");
            return httpClient.execute(request, response -> response.getCode() == 200);
        } catch (Exception e) {
            log.debug("Image generation service not available: {}", e.getMessage());
            return false;
        }
    }
}
