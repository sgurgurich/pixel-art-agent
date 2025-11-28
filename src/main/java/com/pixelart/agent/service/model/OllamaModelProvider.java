package com.pixelart.agent.service.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Real Ollama model provider for generating pixel art descriptions
 */
@Slf4j
@Component
public class OllamaModelProvider implements ModelProvider {
    
    private final ChatClient chatClient;
    
    public OllamaModelProvider(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    @Override
    public String generateResponse(String prompt) {
        try {
            log.debug("Calling Ollama with prompt (first 100 chars): {}", 
                prompt.substring(0, Math.min(100, prompt.length())));
            
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            log.debug("Ollama response received (length: {})", response.length());
            return response;
        } catch (Exception e) {
            log.error("Error calling Ollama model", e);
            throw new RuntimeException("Failed to get response from Ollama", e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        try {
            // Try to get a simple response to verify availability
            String testResponse = chatClient.prompt()
                    .user("test")
                    .call()
                    .content();
            return testResponse != null && !testResponse.isEmpty();
        } catch (Exception e) {
            log.debug("Ollama model not available: {}", e.getMessage());
            return false;
        }
    }
}
