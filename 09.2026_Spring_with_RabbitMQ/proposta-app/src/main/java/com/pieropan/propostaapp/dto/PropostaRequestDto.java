package com.pieropan.propostaapp.dto;

import lombok.Data;

@Data
public class PropostaRequestDto {

    private String nome;
    private String sobrenome;
    private String telefone;
    private String cpf;
    private Double renda;
    private Double valorSolicitado;
    private int prazoPagamento;
}
