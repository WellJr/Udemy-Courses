package com.pieropan.propostaapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Value("${rabbitmq.propostapendente.exchange}")
    private String exchangePropostaPendente;

    @Value("${rabbitmq.propostaconcluida.exchange}")
    private String exchangePropostaConcluida;

    @Bean
    public Queue criarFilaPropostaMsAnaliseCredito() {
        return QueueBuilder.durable("proposta-pendente.ms-analise-credito")
                // arguments for queue
                // .maxLength(2L)
//                .ttl(10000) // tempo maximo para mensagem ser consumida
                .deadLetterExchange("proposta-pendente-dlx.ex") // vincula a dead letter queue com a fila.
                .build();
    }

    @Bean
    public Queue criarFilaPropostaPendenteMsNotificacao() {
        return QueueBuilder.durable("proposta-pendente.ms-notificacao").build();
    }

    @Bean
    public Queue criarFilaPropostaConcluidaMsProposta() {
        return QueueBuilder.durable("proposta-concluida.ms-proposta").build();
    }

    @Bean
    public Queue criarFilaPropostaConcluidaMsNotificacao() {
        return QueueBuilder.durable("proposta-concluida.ms-notificacao").build();
    }

    @Bean
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    // Criando dead letter queues
    @Bean
    public Queue criarFilaPropostaPendenteDlq() {
        return QueueBuilder.durable("proposta-pendente.dlq").build();
    }

    @Bean
    public FanoutExchange deadLetterQueueExchange() {
        return ExchangeBuilder.fanoutExchange("proposta-pendente-dlx.ex").build();
    }

    @Bean
    public Binding criarDeadLetterQueueBinding() {
        return BindingBuilder.bind(criarFilaPropostaPendenteDlq())
                .to(deadLetterQueueExchange());
    }

    // Criando Exchanges
    @Bean
    public FanoutExchange criarFanoutExchangePropostaPendente() {
        return ExchangeBuilder.fanoutExchange(exchangePropostaPendente).build();
    }

    @Bean
    public FanoutExchange criarFanoutExchangePropostaConcluida() {
        return ExchangeBuilder.fanoutExchange(exchangePropostaConcluida).build();
    }

    // Criando Bindings e linkando as filas na exchanges
    @Bean
    public Binding criarBindingPropostaPendenteMSAnaliseCredito() {
        return BindingBuilder.bind(criarFilaPropostaMsAnaliseCredito())
                .to(criarFanoutExchangePropostaPendente());
    }

    @Bean
    public Binding criarBindingPropostaPendenteMSNotificacao() {
        return BindingBuilder.bind(criarFilaPropostaPendenteMsNotificacao())
                .to(criarFanoutExchangePropostaPendente());
    }

    // Bindings proposta concluida
    @Bean
    public Binding criarBindingPropostaConcluidaMSPropostaApp() {
        return BindingBuilder.bind(criarFilaPropostaConcluidaMsProposta())
                .to(criarFanoutExchangePropostaConcluida());
    }

    @Bean
    public Binding criarBindingPropostaConcluidaMSNotificacao() {
        return BindingBuilder.bind(criarFilaPropostaConcluidaMsNotificacao())
                .to(criarFanoutExchangePropostaConcluida());
    }

    // Resolver problemas com conversão do objeto para JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }


}
