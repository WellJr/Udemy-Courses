package com.pieropan.propostaapp.listeners;

import com.pieropan.propostaapp.Repository.PropostaRepository;
import com.pieropan.propostaapp.entity.Proposta;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropostaConcluidaListener {

    @Autowired
    PropostaRepository propostaRepository;

    @RabbitListener(queues = "${rabbitmq.queue.proposta.concluida}")
    public void propostaEmAnalise(Proposta proposta) {
        propostaRepository.save(proposta);
    }

}
