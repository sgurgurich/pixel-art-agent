package com.pixelart.agent.service.model;

import com.pixelart.agent.config.MockResponsesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mock model provider for local development and testing
 * Returns spoofed pixel art descriptions
 * Responses can be customized via MockResponsesConfig
 */
@Slf4j
@Component
public class MockModelProvider implements ModelProvider {
    
    private final MockResponsesConfig mockResponsesConfig;
    private int responseIndex = 0;
    
    @Autowired
    public MockModelProvider(MockResponsesConfig mockResponsesConfig) {
        this.mockResponsesConfig = mockResponsesConfig;
    }
    
    @Override
    public String generateResponse(String prompt) {
        log.info("MockModelProvider: Returning spoofed response for prompt (first 50 chars): {}...", 
            prompt.substring(0, Math.min(50, prompt.length())));
        
        // Get response from config, cycling through available responses
        String response = mockResponsesConfig.getChatResponses()
            .getOrDefault(responseIndex % mockResponsesConfig.getChatResponses().size(),
                getDefaultResponse());
        
        responseIndex++;
        
        log.debug("MockModelProvider: Returning response #{}", responseIndex);
        return response;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    private String getDefaultResponse() {
        return "Unable to load mock response. Please check MockResponsesConfig.";
    }
}
