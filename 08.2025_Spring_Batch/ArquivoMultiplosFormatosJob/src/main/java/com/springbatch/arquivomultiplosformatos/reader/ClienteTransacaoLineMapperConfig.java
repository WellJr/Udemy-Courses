package com.springbatch.arquivomultiplosformatos.reader;

import com.springbatch.arquivomultiplosformatos.dominio.Cliente;
import com.springbatch.arquivomultiplosformatos.dominio.Transacao;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClienteTransacaoLineMapperConfig {


    @Bean
    // descrore o padrão de um arquivo para decidir qual lineMapper usa.
    public PatternMatchingCompositeLineMapper lineMapper () {
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setTokenizers(tokenizers()); // <- Divide a linha em palavras
        lineMapper.setFieldSetMappers(fieldSetMappers()); // <- Pega as palavras e mapeia para objeto de domnínio

        return  lineMapper;

    }

    private Map<String, FieldSetMapper> fieldSetMappers() {
        Map<String, FieldSetMapper> fieldSetMapperMappers = new HashMap<>();
        fieldSetMapperMappers.put("0*", fieldSetMapper(Cliente.class));
        fieldSetMapperMappers.put("1*", fieldSetMapper(Transacao.class));
        return fieldSetMapperMappers;
    }

    private FieldSetMapper fieldSetMapper(Class<?> classe) {
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(classe);
        return fieldSetMapper;
    }

    // Mapa para relacionar padrão com valor
    private Map<String, LineTokenizer> tokenizers() {
       Map<String, LineTokenizer> tokenizers = new HashMap<>();
       tokenizers.put("0*", clienteLineTokenizer());
       tokenizers.put("1*", transacaoLineTokenizer());
        return tokenizers;
    }

    private LineTokenizer clienteLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer  = new DelimitedLineTokenizer();
        // Define as colunas do objeto de domínio.
        lineTokenizer.setNames("nome", "sobrenome", "idade", "email");

        lineTokenizer.setIncludedFields(1,2,3,4); // a coluna 0 não foi mapeada propositalmente devido a estrutura do arquivo.
        return lineTokenizer;
    }

    private LineTokenizer transacaoLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer  = new DelimitedLineTokenizer();
        // Define as colunas do objeto de domínio.
        lineTokenizer.setNames("id", "descricao", "valor");

        lineTokenizer.setIncludedFields(1,2,3); // a coluna 0 não foi mapeada propositalmente devido a estrutura do arquivo.
        return lineTokenizer;
    }

}
