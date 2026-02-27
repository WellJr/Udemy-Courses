package com.pieropan.analisecredito.service;

import com.pieropan.analisecredito.domain.Proposta;
import com.pieropan.analisecredito.service.strategy.CalculoPonto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseCreditoService {

    // carrega todas as interfaces que implementam a interface CalculoPonto usando injeção de dependência
    private List<CalculoPonto> calculoPontosList;

    // injeta a dependência
    public AnaliseCreditoService(List<CalculoPonto> calculoPontosList) {
        this.calculoPontosList = calculoPontosList;
    }

    public void analisar(Proposta proposta) {
        // Faz a soma de todas as Strategies criadas.
        int sum = calculoPontosList.stream().mapToInt(impl -> impl.calcular(proposta)).sum();
    }

}
