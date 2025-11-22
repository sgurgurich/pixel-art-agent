package com.pixelart.agent.service;

import com.pixelart.agent.model.PixelArtRequest;
import com.pixelart.agent.model.PixelArtResponse;
import com.pixelart.agent.model.PixelArtResponse.SpriteSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for generating pixel art and sprite descriptions using Spring AI and Ollama
 */
@Slf4j
@Service
public class PixelArtAgentService {

    private final ChatClient chatClient;
    private final ImageGenerationService imageGenerationService;
    
    @Value("${pixelart.agent.max-iterations:3}")
    private int maxIterations;

    public PixelArtAgentService(ChatClient.Builder chatClientBuilder, ImageGenerationService imageGenerationService) {
        this.chatClient = chatClientBuilder.build();
        this.imageGenerationService = imageGenerationService;
    }

    /**
     * Generate pixel art description and specifications based on the request
     */
    public PixelArtResponse generatePixelArt(PixelArtRequest request) {
        log.info("Generating pixel art for asset type: {}, style: {}", 
                 request.getAssetType(), request.getStyle());

        // Build the prompt for the AI agent
        String prompt = buildPrompt(request);
        
        // Call Ollama through Spring AI
        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.debug("AI Response: {}", aiResponse);

        // Parse and structure the response
        PixelArtResponse response = parseAiResponse(aiResponse, request);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPrompt(prompt);

        // Generate the actual image
        generateAndAttachImage(response, aiResponse, request);

        return response;
    }

    /**
     * Generate multiple variations of pixel art
     */
    public List<PixelArtResponse> generateVariations(PixelArtRequest request, int count) {
        log.info("Generating {} variations for asset type: {}", count, request.getAssetType());
        
        List<PixelArtResponse> variations = new ArrayList<>();
        for (int i = 0; i < Math.min(count, maxIterations); i++) {
            variations.add(generatePixelArt(request));
        }
        
        return variations;
    }

    /**
     * Refine an existing pixel art description
     */
    public PixelArtResponse refinePixelArt(PixelArtRequest request, String feedback) {
        log.info("Refining pixel art with feedback: {}", feedback);
        
        String refinementPrompt = buildRefinementPrompt(request, feedback);
        
        String aiResponse = chatClient.prompt()
                .user(refinementPrompt)
                .call()
                .content();

        PixelArtResponse response = parseAiResponse(aiResponse, request);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPrompt(refinementPrompt);

        return response;
    }

    /**
     * Build the initial prompt for pixel art generation
     */
    private String buildPrompt(PixelArtRequest request) {
        String template = """
                You are an expert pixel art and sprite design consultant for video games.
                
                Generate a detailed technical description for creating the following asset:
                
                Asset Type: {assetType}
                Description: {description}
                Style: {style}
                Color Palette: {colorPalette}
                Size: {size}
                Additional Context: {additionalContext}
                
                Provide:
                1. A detailed visual description of the pixel art (including shapes, proportions, key features)
                2. Suggested color palette (5-8 hex color codes that match the requested palette style)
                3. Technical specifications (exact dimensions, layer breakdown)
                4. Animation suggestions if applicable (idle, walk, attack, etc.)
                5. Any special effects or highlights
                
                Format your response clearly with these sections marked.
                Be specific and technical, as this will be used by pixel artists to create the actual asset.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of(
                "assetType", request.getAssetType() != null ? request.getAssetType() : "sprite",
                "description", request.getDescription() != null ? request.getDescription() : "a game character",
                "style", request.getStyle() != null ? request.getStyle() : "16-bit pixel art",
                "colorPalette", request.getColorPalette() != null ? request.getColorPalette() : "vibrant",
                "size", request.getSize() != null ? request.getSize() : "32x32",
                "additionalContext", request.getAdditionalContext() != null ? request.getAdditionalContext() : "none"
        ));

        return prompt.getContents();
    }

    /**
     * Build refinement prompt based on feedback
     */
    private String buildRefinementPrompt(PixelArtRequest request, String feedback) {
        String template = """
                You are an expert pixel art and sprite design consultant for video games.
                
                Previous request:
                Asset Type: {assetType}
                Description: {description}
                Style: {style}
                
                User Feedback: {feedback}
                
                Based on this feedback, provide an improved and refined detailed description for the pixel art.
                Include all the same sections: visual description, color palette, technical specs, and animation suggestions.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of(
                "assetType", request.getAssetType() != null ? request.getAssetType() : "sprite",
                "description", request.getDescription() != null ? request.getDescription() : "a game character",
                "style", request.getStyle() != null ? request.getStyle() : "16-bit pixel art",
                "feedback", feedback
        ));

        return prompt.getContents();
    }

    /**
     * Parse the AI response into a structured PixelArtResponse object
     */
    private PixelArtResponse parseAiResponse(String aiResponse, PixelArtRequest request) {
        // Extract color codes (hex codes)
        List<String> colors = extractColors(aiResponse);
        
        // Extract animation suggestions
        List<String> animations = extractAnimations(aiResponse);
        
        // Build sprite specifications
        SpriteSpecification specs = SpriteSpecification.builder()
                .size(request.getSize() != null ? request.getSize() : "32x32")
                .assetType(request.getAssetType())
                .frameCount(animations.isEmpty() ? 1 : animations.size())
                .orientation("front-facing")
                .layers(Arrays.asList("background", "base", "details", "highlights"))
                .build();

        return PixelArtResponse.builder()
                .detailedDescription(aiResponse)
                .suggestedColors(colors)
                .specifications(specs)
                .animationSuggestions(animations)
                .style(request.getStyle() != null ? request.getStyle() : "pixel-art")
                .build();
    }

    /**
     * Extract hex color codes from the AI response
     */
    private List<String> extractColors(String text) {
        List<String> colors = new ArrayList<>();
        // Simple regex to find hex color codes
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.matches("#[0-9A-Fa-f]{6}")) {
                colors.add(word);
            }
        }
        
        // If no colors found, provide defaults
        if (colors.isEmpty()) {
            colors = Arrays.asList("#2C3E50", "#E74C3C", "#ECF0F1", "#3498DB", "#F39C12", "#27AE60");
        }
        
        return colors;
    }

    /**
     * Extract animation suggestions from the AI response
     */
    private List<String> extractAnimations(String text) {
        List<String> animations = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        // Look for common animation keywords
        if (lowerText.contains("idle")) animations.add("idle");
        if (lowerText.contains("walk") || lowerText.contains("walking")) animations.add("walk");
        if (lowerText.contains("run") || lowerText.contains("running")) animations.add("run");
        if (lowerText.contains("jump") || lowerText.contains("jumping")) animations.add("jump");
        if (lowerText.contains("attack") || lowerText.contains("attacking")) animations.add("attack");
        if (lowerText.contains("death") || lowerText.contains("dying")) animations.add("death");
        
        return animations;
    }

    /**
     * Generate actual image and attach to response
     */
    private void generateAndAttachImage(PixelArtResponse response, String description, PixelArtRequest request) {
        try {
            // Parse dimensions from the size
            String size = request.getSize() != null ? request.getSize() : "32x32";
            String[] dimensions = size.split("x");
            int width = Integer.parseInt(dimensions[0].trim());
            int height = dimensions.length > 1 ? Integer.parseInt(dimensions[1].trim()) : width;
            
            // Create a concise prompt for image generation
            String imagePrompt = buildImagePrompt(description, request);
            
            log.debug("Attempting to generate image with dimensions: {}x{}", width, height);
            String imageData = imageGenerationService.generateImage(imagePrompt, width, height);
            
            if (imageData != null && !imageData.isEmpty()) {
                response.setImageData(imageData);
                response.setImageStatus("generated");
                log.info("Image generated successfully");
            } else {
                response.setImageStatus("text-only");
                log.debug("Image generation not available - text description only");
            }
            
        } catch (Exception e) {
            log.debug("Image generation skipped: {}", e.getMessage());
            response.setImageStatus("text-only");
            // Don't throw - continue with text-only response
        }
    }

    /**
     * Build a concise prompt for image generation from the AI description
     */
    private String buildImagePrompt(String description, PixelArtRequest request) {
        // Extract key visual elements from the description
        String assetType = request.getAssetType() != null ? request.getAssetType() : "";
        String style = request.getStyle() != null ? request.getStyle() : "pixel art";
        String userDescription = request.getDescription() != null ? request.getDescription() : "";
        
        // Build a focused prompt
        return String.format("%s %s, %s style", assetType, userDescription, style);
    }
}
