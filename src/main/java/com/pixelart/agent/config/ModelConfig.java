package com.pixelart.agent.config;

import com.pixelart.agent.service.model.ImageProvider;
import com.pixelart.agent.service.model.MockImageProvider;
import com.pixelart.agent.service.model.MockModelProvider;
import com.pixelart.agent.service.model.ModelProvider;
import com.pixelart.agent.service.model.OllamaModelProvider;
import com.pixelart.agent.service.model.StableDiffusionImageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for model providers (real or mock)
 * Use mock providers for local development, real providers for production
 */
@Slf4j
@Configuration
public class ModelConfig {
    
    @Value("${pixelart.models.use-mock:true}")
    private boolean useMockModels;
    
    @Value("${pixelart.chat-model.mock-responses-enabled:true}")
    private boolean mockChatResponsesEnabled;
    
    @Value("${pixelart.image-model.mock-responses-enabled:true}")
    private boolean mockImageResponsesEnabled;
    
    @Autowired
    private OllamaModelProvider ollamaModelProvider;
    
    @Autowired
    private MockModelProvider mockModelProvider;
    
    @Autowired
    private StableDiffusionImageProvider stableDiffusionImageProvider;
    
    @Autowired
    private MockImageProvider mockImageProvider;
    
    @Bean
    public ModelProvider modelProvider() {
        if (useMockModels || mockChatResponsesEnabled) {
            log.info("Using MOCK model provider for AI responses");
            return mockModelProvider;
        } else {
            log.info("Using REAL Ollama model provider for AI responses");
            return ollamaModelProvider;
        }
    }
    
    @Bean
    public ImageProvider imageProvider() {
        if (useMockModels || mockImageResponsesEnabled) {
            log.info("Using MOCK image provider for image generation");
            return mockImageProvider;
        } else {
            log.info("Using REAL Stable Diffusion image provider for image generation");
            return stableDiffusionImageProvider;
        }
    }
}
