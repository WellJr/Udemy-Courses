package com.pieropan.notifacao.liesteners;

import com.pieropan.notifacao.constante.MensagemConstante;
import com.pieropan.notifacao.domain.Proposta;
import com.pieropan.notifacao.service.NotificacaoSnsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropostaConcluidaListener {

    @Autowired
    private NotificacaoSnsService notificacaoSnsService;

    @RabbitListener(queues = "${rabbitmq.queue.proposta.concluida}")
    public void propostaConcluida(Proposta proposta) {
        String mensagem = String.format(MensagemConstante.PROPOSTA_CONCLUIDA, proposta.getUsuario().getNome(), proposta.getAprovada() ? "aprovada" : "negada");
        // notificacaoSnsService.notificar(proposta.getUsuario().getTelefone(), mensagem);
        System.out.println(mensagem);
    }

}
