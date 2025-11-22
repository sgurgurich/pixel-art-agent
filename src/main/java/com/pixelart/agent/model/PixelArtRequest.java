package com.pixelart.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for generating pixel art or sprite descriptions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixelArtRequest {
    
    /**
     * Type of asset to generate (e.g., "character", "weapon", "tile", "enemy", "item")
     */
    private String assetType;
    
    /**
     * Detailed description of what to generate
     */
    private String description;
    
    /**
     * Art style (e.g., "8-bit", "16-bit", "32-bit", "isometric")
     */
    private String style;
    
    /**
     * Color palette preference (e.g., "vibrant", "monochrome", "retro", "neon")
     */
    private String colorPalette;
    
    /**
     * Size constraints (e.g., "16x16", "32x32", "64x64")
     */
    private String size;
    
    /**
     * Additional context or constraints
     */
    private String additionalContext;
}
