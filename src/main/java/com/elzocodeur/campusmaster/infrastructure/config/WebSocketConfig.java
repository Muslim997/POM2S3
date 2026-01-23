package com.elzocodeur.campusmaster.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active un simple broker en mémoire pour envoyer des messages aux clients
        // Destinations préfixées par /topic (broadcast) et /queue (privé)
        config.enableSimpleBroker("/topic", "/queue");

        // Préfixe pour les messages envoyés depuis le client
        config.setApplicationDestinationPrefixes("/app");

        // Préfixe pour les destinations privées utilisateur
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket pour la connexion
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // À configurer selon vos besoins de sécurité
                .withSockJS(); // Fallback pour les navigateurs qui ne supportent pas WebSocket
    }
}
