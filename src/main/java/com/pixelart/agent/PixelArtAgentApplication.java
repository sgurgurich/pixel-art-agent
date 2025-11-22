package com.pixelart.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Pixel Art Agent
 * A Spring AI powered agent for generating pixel art and 2D sprites using Ollama
 */
@SpringBootApplication
public class PixelArtAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixelArtAgentApplication.class, args);
    }
}
