package com.pieropan.analisecredito.service.strategy.impl;

import com.pieropan.analisecredito.domain.Proposta;
import com.pieropan.analisecredito.service.strategy.CalculoPonto;

public class PrazoPagamentoInferiorDezAnos implements CalculoPonto {
    @Override
    public int calcular(Proposta proposta) {
        return proposta.getPrazoPagamento() < 120 ? 80 : 0;
    }
}
