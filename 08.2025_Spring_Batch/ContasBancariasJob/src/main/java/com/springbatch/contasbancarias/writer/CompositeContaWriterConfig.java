package com.springbatch.contasbancarias.writer;

import com.springbatch.contasbancarias.dominio.Conta;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompositeContaWriterConfig {
    @Bean
    // delega escrita para outros escritores
    public CompositeItemWriter<Conta> compositeContaWriter(
            @Qualifier("fileContaWriter") FlatFileItemWriter<Conta> flatFileItemWriter,
            JdbcBatchItemWriter<Conta> jdbcBatchItemWriter
    ) {
        return new CompositeItemWriterBuilder<Conta>()
                .delegates(flatFileItemWriter, jdbcBatchItemWriter)
                .build();
    }
}
