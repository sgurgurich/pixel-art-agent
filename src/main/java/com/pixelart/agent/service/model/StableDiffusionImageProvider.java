package com.pixelart.agent.service.model;

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
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Real Stable Diffusion image provider for generating pixel art images
 */
@Slf4j
@Component
public class StableDiffusionImageProvider implements ImageProvider {
    
    @Value("${pixelart.image.generation.api-url:http://localhost:7860}")
    private String sdApiUrl;
    
    @Value("${pixelart.image.generation.lora:}")
    private String loraModel;
    
    @Value("${pixelart.image.generation.lora-strength:0.8}")
    private double loraStrength;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String generateImage(String prompt, int width, int height) {
        return generateImage(prompt, width, height, false, 1);
    }
    
    @Override
    public String generateImage(String prompt, int width, int height, boolean isSpritesheet, int frameCount) {
        try {
            int finalWidth = width;
            int finalHeight = height;
            
            if (isSpritesheet && frameCount > 1) {
                finalWidth = width * frameCount;
                log.info("Generating spritesheet with {} frames ({}x{})", frameCount, finalWidth, finalHeight);
            } else {
                log.info("Generating single sprite image ({}x{})", finalWidth, finalHeight);
            }
            
            return generateWithStableDiffusion(prompt, finalWidth, finalHeight, isSpritesheet, frameCount);
            
        } catch (Exception e) {
            log.error("Error generating image", e);
            return null;
        }
    }
    
    private String generateWithStableDiffusion(String prompt, int width, int height, boolean isSpritesheet, int frameCount) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManagerShared(false)
                .build()) {
            
            HttpPost request = new HttpPost(sdApiUrl + "/sdapi/v1/txt2img");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", enhancePromptForPixelArt(prompt, isSpritesheet, frameCount));
            payload.put("negative_prompt", 
                "blurry, smooth, realistic, photograph, photorealistic, 3d render, detailed textures, " +
                "soft edges, gradients, anti-aliasing, high resolution, smooth shading, dithering, " +
                "detailed, complex, modern graphics, HD, 4K, ray tracing, ambient occlusion");
            payload.put("steps", 50);
            payload.put("width", width * 8);
            payload.put("height", height * 8);
            payload.put("cfg_scale", 15);
            payload.put("sampler_name", "Euler a");
            payload.put("seed", -1);
            payload.put("denoising_strength", 0.4);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            
            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                log.debug("Stable Diffusion API response status: {}", statusCode);
                
                if (statusCode == 200) {
                    try {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        JsonNode jsonResponse = objectMapper.readTree(responseBody);
                        
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
    
    private String enhancePromptForPixelArt(String prompt, boolean isSpritesheet, int frameCount) {
        StringBuilder enhancedPrompt = new StringBuilder();
        
        if (loraModel != null && !loraModel.isEmpty()) {
            enhancedPrompt.append(String.format("<lora:%s:%.1f> ", loraModel, loraStrength));
        }
        
        enhancedPrompt.append("((pixel art)), ((16-bit)), ((retro game sprite)), ");
        enhancedPrompt.append("((NES style)), ((SNES style)), ((Game Boy Advance)), ");
        
        if (isSpritesheet && frameCount > 1) {
            enhancedPrompt.append(String.format(
                "((sprite sheet)), %d animation frames side by side, ", frameCount));
            enhancedPrompt.append("horizontal sprite strip, game animation frames, ");
        }
        
        enhancedPrompt.append(prompt);
        
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
    
    @Override
    public boolean isAvailable() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(sdApiUrl + "/sdapi/v1/options");
            return httpClient.execute(request, response -> response.getCode() == 200);
        } catch (Exception e) {
            log.debug("Image generation service not available: {}", e.getMessage());
            return false;
        }
    }
}
