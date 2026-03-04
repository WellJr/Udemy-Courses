package com.pieropan.propostaapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // criar um end-point dentro de localhost 8080 e da permissão para locahost (front-end) ficar acessando
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cria um tópico de escuta onde o front-end fica escutando
        registry.enableSimpleBroker("/propostas");
    }


}
