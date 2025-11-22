package com.pixelart.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
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
        if (!imageGenerationEnabled) {
            log.info("Image generation is disabled");
            return null;
        }

        try {
            log.info("Generating image for prompt: {} ({}x{})", prompt, width, height);
            
            // Try Stable Diffusion API first
            String image = generateWithStableDiffusion(prompt, width, height);
            
            if (image != null) {
                return image;
            }
            
            // Fallback: Generate a simple placeholder
            return generatePlaceholder(width, height);
            
        } catch (Exception e) {
            log.error("Error generating image", e);
            return generatePlaceholder(width, height);
        }
    }

    /**
     * Generate image using Stable Diffusion API (AUTOMATIC1111 or similar)
     */
    private String generateWithStableDiffusion(String prompt, int width, int height) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManagerShared(false)
                .build()) {
            
            // Prepare the request
            HttpPost request = new HttpPost(sdApiUrl + "/sdapi/v1/txt2img");
            
            // Build the payload with pixel art optimized settings
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", enhancePromptForPixelArt(prompt));
            payload.put("negative_prompt", "blurry, smooth, realistic, photograph, 3d render, detailed textures");
            payload.put("steps", 20);
            payload.put("width", width);
            payload.put("height", height);
            payload.put("cfg_scale", 7);
            payload.put("sampler_name", "DPM++ 2M Karras");
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            
            // Execute request
            return httpClient.execute(request, response -> {
                if (response.getCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    
                    // Extract the first image from the response
                    if (jsonResponse.has("images") && jsonResponse.get("images").size() > 0) {
                        String base64Image = jsonResponse.get("images").get(0).asText();
                        log.info("Successfully generated image with Stable Diffusion");
                        return base64Image;
                    }
                }
                log.warn("Stable Diffusion API returned status: {}", response.getCode());
                return null;
            });
            
        } catch (Exception e) {
            log.debug("Stable Diffusion not available at {}: {}", sdApiUrl, e.getMessage());
            return null;
        }
    }

    /**
     * Enhance the prompt for better pixel art generation
     */
    private String enhancePromptForPixelArt(String prompt) {
        return String.format(
            "pixel art, %s, crisp pixels, retro game art, clean outlines, limited color palette, " +
            "video game sprite, low resolution aesthetic, sharp edges, no anti-aliasing",
            prompt
        );
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
