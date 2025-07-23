package io.github.cursodsousa.libraryapi.security;

// Resource Server

import io.github.cursodsousa.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientService clientService;
    private final TokenSettings tokenSettings;
    private final ClientSettings clientSettings;

    @Override
    public void save(RegisteredClient registeredClient) {}

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = clientService.obterPorClientID(clientId);

        if(client == null) {
            return null;
        }

        return RegisteredClient
                .withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .redirectUri(client.getRedirectURI())
                .scope(client.getScope())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                //GrantType: Definne a forma usada para se autenticar
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Digita login e senha
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // De aplicação para aplicação

                // Só faz sentido usar REFRESH_TOKEN quando logado com usuário e senha
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                .tokenSettings(tokenSettings)
                .clientSettings(clientSettings)
                .build();
    }
}
