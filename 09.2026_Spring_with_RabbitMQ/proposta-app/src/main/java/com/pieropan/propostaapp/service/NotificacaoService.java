package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.dto.PropostaResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificacaoService {

    // Dependecy injection
    private RabbitTemplate rabbitTemplate;

    public void notificar(PropostaResponseDto proposta, String exchange) {
        rabbitTemplate.convertAndSend(exchange, "", proposta);
    }

}
