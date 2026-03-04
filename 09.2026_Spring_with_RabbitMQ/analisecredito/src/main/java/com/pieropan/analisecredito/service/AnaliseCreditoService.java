package com.pieropan.analisecredito.service;

import com.pieropan.analisecredito.domain.Proposta;
import com.pieropan.analisecredito.exceptions.StrategyException;
import com.pieropan.analisecredito.service.strategy.CalculoPonto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseCreditoService {

    // carrega todas as interfaces que implementam a interface CalculoPonto usando injeção de dependência
    private List<CalculoPonto> calculoPontosList;

    private NotificacaoRabbitService notificacaoRabbitService;

    @Value("${rabbitmq.propostaconcluida.exchange}")
    private String exchangePropostaConcluida;

    // injeta a dependência
    public AnaliseCreditoService(List<CalculoPonto> calculoPontosList, NotificacaoRabbitService notificacaoRabbitService) {
        this.calculoPontosList = calculoPontosList;
        this.notificacaoRabbitService = notificacaoRabbitService;
    }

    public void analisar(Proposta proposta) {
        try {
            // Faz a soma de todas as Strategies criadas.
            int pontos = calculoPontosList.stream().mapToInt(impl -> impl.calcular(proposta)).sum();
            proposta.setAprovada(pontos > 350);
        } catch(StrategyException ex) {
           proposta.setAprovada(false);
           proposta.setObservacao(ex.getMessage());
        }
        notificacaoRabbitService.notificar(exchangePropostaConcluida, proposta);
    }

}
