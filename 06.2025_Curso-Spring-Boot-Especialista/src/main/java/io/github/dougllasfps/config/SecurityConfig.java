package io.github.dougllasfps.config;

import io.github.dougllasfps.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder --> Gera um hash diferente pra mesma senha.
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // AuthenticationManagerBuilder --> Define de onde virá os usuários e senhas &
        // Tras os objetos que irá fazer a autenticação dos usuários

        // .userDetailsService --> Carrega os usuarios da base
        auth.userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    // Controla a autorização (Roles --> Perfis e Authority)
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/api/clientes/**").hasAnyRole("USER", "ADMIN")
                    .antMatchers("/api/produtos/**").hasRole("ADMIN")
                    .antMatchers("/api/pedidos/**").hasAnyRole("USER", "ADMIN")

                // volta para raiz do HttpSecurity
                .and()

                //permite requisição através do header
                .httpBasic();
    }

}
