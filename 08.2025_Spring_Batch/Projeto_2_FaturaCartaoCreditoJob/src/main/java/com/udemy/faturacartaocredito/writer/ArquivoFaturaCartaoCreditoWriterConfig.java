package com.udemy.faturacartaocredito.writer;

import com.udemy.faturacartaocredito.dominio.FaturaCartaoCredito;
import com.udemy.faturacartaocredito.dominio.Transacao;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@Configuration
public class ArquivoFaturaCartaoCreditoWriterConfig {
    @Bean
    public MultiResourceItemWriter<FaturaCartaoCredito> arquivosFaturaCartaoCredito() {
        return new MultiResourceItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivosFaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura"))
                .itemCountLimitPerResource(1)
                .resourceSuffixCreator(suffixCreator())
                .delegate(arquivoFaturaCartaoCredito())
                .build();
    }

    private FlatFileItemWriter<FaturaCartaoCredito> arquivoFaturaCartaoCredito() {
        return new FlatFileItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivoFaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura.txt"))
                .lineAggregator(lineAggregator())
                .headerCallback(headerCallback())
                .footerCallback(footerCallback())
                .build();
    }

    @Bean
    public FlatFileFooterCallback footerCallback() {
        return new TotalTransacoesFooterCallBack();
    }

    private FlatFileHeaderCallback headerCallback() {
        return new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.append(String.format("%123s\n", "Cartao XPTO"));
                writer.append(String.format("121s\n\n", "Rua Vergueiro, 131"));

            }
        };
    }

    private LineAggregator<FaturaCartaoCredito> lineAggregator() {
        return new LineAggregator<FaturaCartaoCredito>() {
            @Override
            public String aggregate(FaturaCartaoCredito faturaCartaoCredito) {
                StringBuilder writer = new StringBuilder();
                writer.append(String.format("Nome: %s\n", faturaCartaoCredito.getCliente().getNome()));
                writer.append(String.format("Endereço: %s\n\n\n", faturaCartaoCredito.getCliente().getEndereco() ));
                writer.append(String.format("Fatura completa do cartao %d\n", faturaCartaoCredito.getCartaoCredito().getNumeroCartaoCredito()));
                writer.append("-------------------------------------------------------------------------------------------------------------------------\n");
                writer.append("DATA DESCRICAO VALOR\n");
                writer.append("-------------------------------------------------------------------------------------------------------------------------\n");

                for (Transacao transacao : faturaCartaoCredito.getTransacoes()) {
                    writer.append(String.format("\n[10s] %-80s - %s",
                            new SimpleDateFormat("dd/MM/yyyy").format(transacao.getData()),
                            transacao.getDescricao(),
                            NumberFormat.getCurrencyInstance().format(transacao.getValor())));
                }
                return writer.toString();
            }
        };
    }

    private ResourceSuffixCreator suffixCreator() {
        return new ResourceSuffixCreator() {
            @Override
            public String getSuffix(int index) {
                return index + ".txt";
            }
        };
    }
}
