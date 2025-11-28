package com.pixelart.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for mock model responses
 * Allows enabling/disabling mock models and customizing their responses
 */
@Data
@Component
@ConfigurationProperties(prefix = "pixelart.mock")
public class MockResponsesConfig {
    
    /**
     * Chat model mock responses - a map of response indices to response texts
     * Can be customized in application.yml or application.properties
     */
    private Map<Integer, String> chatResponses = new HashMap<>();
    
    /**
     * Image provider response descriptions for logging
     */
    private String imageResponseDescription = "pixel-art sprite";
    
    public MockResponsesConfig() {
        // Initialize with default responses
        initializeDefaultResponses();
    }
    
    private void initializeDefaultResponses() {
        chatResponses.put(0, """
            Visual Description:
            A classic warrior character with a helmet, armor plating on the chest and shoulders, 
            holding a sword. The design follows 16-bit SNES aesthetics with distinct color blocks 
            and clear silhouette. The warrior stands in a neutral ready position.
            
            Color Palette:
            #2C3E50 (dark blue) for shadows
            #34495E (medium blue-gray) for armor base
            #E74C3C (red) for cloth/cape
            #F39C12 (gold) for helmet trim
            #ECF0F1 (light gray) for highlights
            
            Technical Specifications:
            Dimensions: 32x32 pixels
            Layers: background, base armor, details, highlights
            
            Animation Suggestions:
            idle: breathing animation with subtle shoulder movement
            walk: 4-frame walking cycle with swinging arms
            attack: 3-frame sword slash animation
            """);
            
        chatResponses.put(1, """
            Visual Description:
            A treasure chest in isometric view, wooden construction with metal bands and a golden lock.
            Classic RPG-style treasure chest with closed state showing ornate detailing. The design 
            emphasizes the metallic accents and wooden texture with retro pixel art styling.
            
            Color Palette:
            #8B4513 (brown) for wood
            #DAA520 (goldenrod) for metal bands and lock
            #654321 (dark brown) for shadows
            #FFD700 (gold) for highlights
            #A0522D (sienna) for depth
            
            Technical Specifications:
            Dimensions: 32x32 pixels
            Layers: background, wood base, metal bands, lock, highlights
            
            Animation Suggestions:
            idle: static with subtle shadow
            open: 4-frame opening animation
            """);
            
        chatResponses.put(2, """
            Visual Description:
            A fantasy potion bottle with luminescent contents. Glass bottle design with cork stopper,
            containing swirling magical energy inside. The design captures an alchemist's potion 
            with mystical glow effects using limited color palette.
            
            Color Palette:
            #4B0082 (indigo) for potion liquid
            #9932CC (dark orchid) for magical glow
            #E6B800 (dark gold) for bottle highlights
            #228B22 (forest green) for glass tint
            #FFFACD (light yellow) for bright highlights
            
            Technical Specifications:
            Dimensions: 16x24 pixels
            Layers: bottle glass, liquid, cork, glow effect
            
            Animation Suggestions:
            idle: gentle swirling animation of contents
            """);
    }
}
