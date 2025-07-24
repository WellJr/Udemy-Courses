package io.github.cursodsousa.resource_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurationRS {

    @Bean
    public SecurityFilterChain securityConfiguration(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/public/**").permitAll();
                    auth.anyRequest().authenticated();
                })

                // informa que irá se autenticar com JWT
                // Customizer.withDefaults(): Indica que usará a configuração padrão do JWT
                .oauth2ResourceServer(oauthRS -> oauthRS.jwt(Customizer.withDefaults()));
        return http.build();
    }


}
