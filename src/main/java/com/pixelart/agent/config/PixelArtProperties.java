package com.pixelart.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Pixel Art Agent
 */
@Configuration
@ConfigurationProperties(prefix = "pixelart.agent")
public class PixelArtProperties {
    
    private int maxIterations = 3;
    private String defaultStyle = "pixel-art";

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }
}
