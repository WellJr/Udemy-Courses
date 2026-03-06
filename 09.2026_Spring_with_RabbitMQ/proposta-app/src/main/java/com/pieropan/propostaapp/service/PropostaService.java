package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.Repository.PropostaRepository;
import com.pieropan.propostaapp.dto.PropostaRequestDto;
import com.pieropan.propostaapp.dto.PropostaResponseDto;
import com.pieropan.propostaapp.entity.Proposta;
import com.pieropan.propostaapp.mapper.PropostaMapper;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

//@AllArgsConstructor
@Service
public class PropostaService {

    private PropostaRepository propostaRepository;

    private NotificacaoRabbitService notificacaoService;

    private String exchange;

    public PropostaService(PropostaRepository propostaRepository,
                           NotificacaoRabbitService notificacaoService,
                           @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notificacaoService = notificacaoService;
        this.exchange = exchange;
    }

    public PropostaResponseDto criar(PropostaRequestDto requestDto) {
        Proposta proposta = PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        // Definindo prioridade da mensagem
        int prioridade = proposta.getUsuario().getRenda() > 10000 ? 10 : 5;
        MessagePostProcessor messagePostProcessor = message -> {
          message.getMessageProperties().setPriority(prioridade);
          return message;
        };


        notificarRabbitMq(proposta, messagePostProcessor);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);
    }

    public void notificarRabbitMq(Proposta proposta, MessagePostProcessor  messagePostProcessor) {
        try {
            notificacaoService.notificar(proposta, exchange, messagePostProcessor);
        } catch (RuntimeException ex) {
            // Caso o RabbitMQ está fors
            proposta.setIntegrada(false);
            propostaRepository.save(proposta);
        }

    }


    public List<PropostaResponseDto> obterProposta() {
        return PropostaMapper.INSTANCE.convertLisEntityToListDto(propostaRepository.findAll());
    }
}
