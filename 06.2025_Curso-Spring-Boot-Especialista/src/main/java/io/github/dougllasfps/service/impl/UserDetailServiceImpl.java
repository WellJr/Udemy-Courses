package io.github.dougllasfps.service.impl;

import io.github.dougllasfps.domain.entity.Usuario;
import io.github.dougllasfps.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    /*
    * O objetivo de UserDetailServiceImpl é carregar um usuário da base de dados.
    * */

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Usuario usuario = repository.findByLogin(username).orElseThrow(
                    () -> new UsernameNotFoundException("Usuário ("+username+") não econtrado na base de daods."));

            String[] roles = usuario.isAdmin() ? new String[] {"ADMIN", "USER"} : new String[] {"USER"};

            // retorna o user do Springframwwork
            return User
                    .builder()
                    .username(usuario.getLogin())
                    .password(usuario.getSenha())
                    .roles(roles)
                    .build();
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }
}
