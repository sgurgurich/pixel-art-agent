package com.pixelart.agent.service.model;

/**
 * Interface for AI model providers
 * Implementations can provide real or mocked responses
 */
public interface ModelProvider {
    /**
     * Generate response from the model
     * @param prompt The input prompt
     * @return The model's response
     */
    String generateResponse(String prompt);
    
    /**
     * Check if the model provider is available
     * @return true if the model is available and working
     */
    boolean isAvailable();
}
