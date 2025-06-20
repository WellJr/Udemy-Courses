package io.github.dougllasfps.rest.controller;

import io.github.dougllasfps.domain.entity.Usuario;
import io.github.dougllasfps.service.impl.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")

// faz a injeção de dependência via construtor
@RequiredArgsConstructor
public class UsuarioController {

    private final UserDetailServiceImpl usuarioService;
    private final PasswordEncoder encoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario salvar(@RequestBody @Valid Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioService.salvar(usuario);
    }

}
