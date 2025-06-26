package io.github.cursodsousa.libraryapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {
    @GetMapping("/login")
    public String paginaLogin() {
        // retorna nome da view criada no WebConfiguration
        return "login";
    }
}
