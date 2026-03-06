package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.entity.Proposta;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificacaoRabbitService {

    // Dependecy injection
    private RabbitTemplate rabbitTemplate;

    public void notificar(Proposta proposta, String exchange, MessagePostProcessor messagePostProcessors) {
        rabbitTemplate.convertAndSend(exchange, "", proposta, messagePostProcessors);
    }

}
