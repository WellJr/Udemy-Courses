package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.Repository.PropostaRepository;
import com.pieropan.propostaapp.dto.PropostaRequestDto;
import com.pieropan.propostaapp.dto.PropostaResponseDto;
import com.pieropan.propostaapp.entity.Proposta;
import com.pieropan.propostaapp.mapper.PropostaMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

//@AllArgsConstructor
@Service
public class PropostaService {

    private PropostaRepository propostaRepository;

    private NotificacaoService notificacaoService;

    private String exchange;

    public PropostaService(PropostaRepository propostaRepository,
                           NotificacaoService notificacaoService,
                           @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notificacaoService = notificacaoService;
        this.exchange = exchange;
    }

    public PropostaResponseDto criar(PropostaRequestDto requestDto) {
        Proposta proposta = PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        PropostaResponseDto response = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        notificacaoService.notificar(response, exchange);

        return response;
    }

    public List<PropostaResponseDto> obterProposta() {
        return PropostaMapper.INSTANCE.convertLisEntityToListDto(propostaRepository.findAll());
    }
}
