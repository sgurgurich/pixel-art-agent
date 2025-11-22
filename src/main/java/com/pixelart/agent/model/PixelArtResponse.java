package com.pixelart.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response model containing the generated pixel art description and metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixelArtResponse {
    
    /**
     * Generated detailed description for the pixel art
     */
    private String detailedDescription;
    
    /**
     * Suggested color palette with hex codes
     */
    private List<String> suggestedColors;
    
    /**
     * Technical specifications for the sprite
     */
    private SpriteSpecification specifications;
    
    /**
     * Animation suggestions if applicable
     */
    private List<String> animationSuggestions;
    
    /**
     * Art style applied
     */
    private String style;
    
    /**
     * Timestamp of generation
     */
    private LocalDateTime generatedAt;
    
    /**
     * Prompt used for generation
     */
    private String prompt;
    
    /**
     * Base64 encoded image data (PNG format)
     */
    private String imageData;
    
    /**
     * Image generation status
     */
    private String imageStatus;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpriteSpecification {
        private String size;
        private String assetType;
        private Integer frameCount;
        private String orientation;
        private List<String> layers;
    }
}
