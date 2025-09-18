package com.springbatch.contasbancarias.writer;

import com.springbatch.contasbancarias.dominio.Conta;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class FileContaWriterConfig {

    @Bean
    public FlatFileItemWriter<Conta> fileContaWriter() {
        return new FlatFileItemWriterBuilder<Conta>()
                .name("fileContaWriter")
                .resource(new FileSystemResource("./files/contas.txt"))
                .delimited()
                .names("tipo", "limite", "clienteId")
                .build();
    }

}
