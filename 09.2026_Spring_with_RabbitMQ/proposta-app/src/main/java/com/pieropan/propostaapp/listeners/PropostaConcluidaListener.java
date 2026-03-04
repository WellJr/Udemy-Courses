package com.pieropan.propostaapp.listeners;

import com.pieropan.propostaapp.Repository.PropostaRepository;
import com.pieropan.propostaapp.dto.PropostaResponseDto;
import com.pieropan.propostaapp.entity.Proposta;
import com.pieropan.propostaapp.mapper.PropostaMapper;
import com.pieropan.propostaapp.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropostaConcluidaListener {

    @Autowired
    PropostaRepository propostaRepository;

    @Autowired
    private WebSocketService webSocketService;

    @RabbitListener(queues = "${rabbitmq.queue.proposta.concluida}")
    public void propostaEmAnalise(Proposta proposta) {
        propostaRepository.save(proposta);
        PropostaResponseDto propostaResponseDto = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        webSocketService.notificar(propostaResponseDto);
    }

}
