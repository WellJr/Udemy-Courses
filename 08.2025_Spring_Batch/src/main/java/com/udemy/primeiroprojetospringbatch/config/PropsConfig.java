package com.udemy.primeiroprojetospringbatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

/*
    Quando não existe nada no application.properties, o spring irá procurar as configurações neste arquivo.
    Essa confiuração é usada para evitar expor informações sensíveis direto pela aplicação.
 */

@Configuration
public class PropsConfig {

// Está comentado pois será utilizado o proprio application.properties da aplicação.

//    @Bean
//    public PropertySourcesPlaceholderConfigurer config() {
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        configurer.setLocation(new FileSystemResource("/etc/config/primeirojobspringbatch/application/application.properties"));
//        return configurer;
//    }

}
