package com.springbatch.arquivolargurafixa.writer;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.arquivolargurafixa.dominio.Cliente;
import org.springframework.core.io.Resource;

@Configuration
public class ArquivoLarguraFixaWriterConfig {

	@StepScope
	@Bean
	public FlatFileItemWriter<Cliente> escritaaArquivoLarguraFixaWriter(
			@Value("#{jobParameters['arquivoClientesSaida']}")Resource arquivoClientesSaida) {
	return new FlatFileItemWriterBuilder<Cliente>()
			.name("escritaaArquivoLarguraFixaWriter")
			.resource(arquivoClientesSaida)

			.formatted() // <-- informa ao builder que será usado um formato customizado
			.format("%-9s %-9s %-2s %-19s")
			.names("nome", "sobrenome", "idade", "email")
			.build();
	}
}
