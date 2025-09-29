package com.springbatch.contasbancarias.processor;

import com.springbatch.contasbancarias.dominio.Conta;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClassifierContaWriterConfig {
    @Bean
    public ClassifierCompositeItemWriter<Conta> classifierCompositeItemWriter(
            @Qualifier("clienteInvalidoWriter")FlatFileItemWriter<Conta> clienteInvalidoWriter,
            CompositeItemWriter<Conta> clienteValidoWriter
            ) {
        return new ClassifierCompositeItemWriterBuilder<Conta>()
                // classifier: Escolher qual writer vai ser aplicado dependo da classificação
                .classifier(classifier(clienteInvalidoWriter, clienteValidoWriter))
                .build();
    }

    private Classifier<Conta, ItemWriter<? super Conta>> classifier(FlatFileItemWriter<Conta> clienteInvalidoWriter, CompositeItemWriter<Conta> clienteValidoWriter) {
        return new Classifier<Conta, ItemWriter<? super Conta>>() {
            @Override
            public ItemWriter<? super Conta> classify(Conta conta) {
                if (conta.getTipo() != null) {
                    return clienteValidoWriter;
                } else {
                    return clienteInvalidoWriter;
                }
            }
        };
    }
}
