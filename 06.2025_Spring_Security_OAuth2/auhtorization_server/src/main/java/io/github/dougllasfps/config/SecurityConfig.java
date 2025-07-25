package io.github.dougllasfps.config;

import io.github.dougllasfps.security.jwt.JwtAuthFilter;
import io.github.dougllasfps.security.jwt.JwtService;
import io.github.dougllasfps.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder --> Gera um hash diferente pra mesma senha.
        return new BCryptPasswordEncoder();
    }

    // Registrar Filter dentro do Spring Security
    @Bean
    public OncePerRequestFilter jwtFilter() {
        // OncePerRequestFilter foi implemetado em JwtAuthService
        return new JwtAuthFilter(jwtService, userDetailService);
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
                    .antMatchers(HttpMethod.POST, "/api/usuarios/**").permitAll()

                /* garante de as demais URIs que não forma mapeadas acima, tenham acesso estando pelo menos
                autenticado */
                .anyRequest().authenticated()

                // volta para raiz do HttpSecurity
                .and()

                    //Não cria mais sessões. Pois o controle será feito através do token
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // Pririza jwtFilter antes de UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
