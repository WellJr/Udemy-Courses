package io.github.dougllasfps.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Representa o retorno para o usuario
public class TokenDTO {
    private String login;
    private String token;
}
