package com.pieropan.analisecredito.service.strategy.impl;

import com.pieropan.analisecredito.constantes.MensagemConstante;
import com.pieropan.analisecredito.domain.Proposta;
import com.pieropan.analisecredito.exceptions.StrategyException;
import com.pieropan.analisecredito.service.strategy.CalculoPonto;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Order(1)
@Component
public class NomeNegativadoImpl implements CalculoPonto {
    @Override
    public int calcular(Proposta proposta) {
        if (nomeNegativado()) {
            throw new StrategyException(String.format(MensagemConstante.CLIENTE_NEGATIVADO, proposta.getUsuario().getNome()));
        }
        return 100;
    }
    private boolean nomeNegativado() {
        return new Random().nextBoolean();
    }
}
