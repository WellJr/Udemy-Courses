package io.github.cursodsousa.libraryapi.config;

// ResourceServer

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.cursodsousa.libraryapi.security.CustomAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1) // Define como filtro principal, visto que já exite outro
    //Configuração para habilitar o AuthorizationServer
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception{
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        //OIDC: permite obter informações do token. Ex: Quem é o usuário que está autenticado.
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        //Verifica se o token gerado é válido
        http.oauth2ResourceServer(oath2Rs -> oath2Rs.jwt(Customizer.withDefaults()));

        http.formLogin(configurer -> configurer.loginPage("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public TokenSettings tokenSettings(){
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                // access_toke: token utilizado nas requisições
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                // refresh_token: token para renovar o access_token
                .refreshTokenTimeToLive(Duration.ofMinutes(90))
                .build();
    }

    @Bean
    public ClientSettings clientSettings(){
        return ClientSettings.builder()
                // tela de consentimento do google
                .requireAuthorizationConsent(false)
                .build();
    }
    // JWK - JSON Web Key: Representação de uma chave crpitográfica que pode ser usado em processos
    // de autenticação e assinatura digital. Ele assina o token.
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey rsaKey = gerarChaveRSA();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    //gerar par de chaves RSA
    private RSAKey gerarChaveRSA() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        RSAPublicKey chavePublica = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateCrtKey chavePrivada = (RSAPrivateCrtKey) keyPair.getPrivate();

        return new RSAKey
                .Builder(chavePublica)
                .privateKey(chavePrivada)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    // decodifica JWT
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    // utilizado para modificar URLs padrão do OAuth (Opcional)
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings
                .builder()
                //obter token
                .tokenEndpoint("/oauth2/token")
                // para consultar status do token
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                // revogar token
                .tokenRevocationEndpoint("/oauth2/revoke")
                // authorization endpoint
                .authorizationEndpoint("/oauth2/authorize")
                // informacoes do usuario OPEN ID CONNECT
                .oidcLogoutEndpoint("/oauth2/userinfo")
                // obter a chave publica para verificar a assinatura do token
                .jwkSetEndpoint("/oauth2/jwks")
                //logout
                .oidcLogoutEndpoint("/oauth2/logout")
                .build();
    }

    @Bean
    // Customizar token
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        // context é passado pelo authorization server
        return context -> {
            var principal = context.getPrincipal();

            if (principal instanceof CustomAuthentication authentication) {
                OAuth2TokenType tipoToken = context.getTokenType();

                if (OAuth2TokenType.ACCESS_TOKEN.equals(tipoToken)) {
                    Collection<GrantedAuthority> authorities = authentication.getAuthorities();
                    List<String> authoritiesList = authorities.stream().map(GrantedAuthority::getAuthority).toList();

                    context
                            .getClaims()
                            .claim("authorities", authoritiesList)
                            .claim("email", authentication.getUsuario().getEmail());
                }
            }
        };

    }
}
