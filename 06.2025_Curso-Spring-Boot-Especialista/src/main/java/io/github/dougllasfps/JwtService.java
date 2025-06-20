package io.github.dougllasfps;

import io.github.dougllasfps.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.expiracao}")
    private String expiracao;

    @Value("${security.jwt.chave-assinatura}")
    private String chaveAssinatura;

    // # 1
    public String gerarToken (Usuario usuario) {
        long expString = Long.valueOf(expiracao);

        //pega a hora atual e adiciona +30min para expiração do token
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expString);

        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        return Jwts
                .builder()
                .setSubject(usuario.getLogin())
                .setExpiration(data)
                .signWith(SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();
    }

    //# 2: Decodifica Token
    private Claims obterClaims (String token) throws ExpiredJwtException {
       return Jwts
               .parser()
               .setSigningKey(chaveAssinatura)
               .parseClaimsJws(token)
               .getBody();
    }

    //# 3
    public boolean isTokenValido(String token) {
        try {
            Claims claims = obterClaims(token);
            Date dataExpiracao = claims.getExpiration();
            LocalDateTime localDateTime =
                    dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            return LocalDateTime.now().isAfter(localDateTime);

        }catch (Exception e) {
            return false;
        }
    }

    // # 4
    public String obterLoginUsuario(String token) throws ExpiredJwtException {
        return (String) obterClaims(token).getSubject();
    }

    public static void main (String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VendasApplication.class);
        JwtService service = context.getBean(JwtService.class);

        Usuario usuario = Usuario.builder().login("fulano").build();
        String token = service.gerarToken(usuario);
        System.out.println(token);

        System.out.println("O token está válido? " + service.isTokenValido(token));

        System.out.println(service.obterLoginUsuario(token));
    }

}
