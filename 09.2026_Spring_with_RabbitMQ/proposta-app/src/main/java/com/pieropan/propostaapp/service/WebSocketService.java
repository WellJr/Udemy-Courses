package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.dto.PropostaResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void notificar(PropostaResponseDto propostaResponseDto) {
        simpMessagingTemplate.convertAndSend("/propostas", propostaResponseDto);
    }

}
