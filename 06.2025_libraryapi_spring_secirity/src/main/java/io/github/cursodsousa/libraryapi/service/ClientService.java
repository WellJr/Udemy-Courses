package io.github.cursodsousa.libraryapi.service;

// RESOURCE SERVER

import io.github.cursodsousa.libraryapi.model.Client;
import io.github.cursodsousa.libraryapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private final PasswordEncoder encoder;

    public Client salvar(Client client) {
        client.setClientSecret(encoder.encode(client.getClientSecret()));

        return repository.save(client);
    }

    public Client obterPorClientID(String clientId) {
        return repository.findByClientId(clientId);
    }

}
