package com.github.devopMarkz.api_liq_rota.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.github.devopMarkz.api_liq_rota.api.dto.auth.TokenDTO;
import com.github.devopMarkz.api_liq_rota.api.exception.TokenInvalidoException;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import com.github.devopMarkz.api_liq_rota.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final UsuarioRepository usuarioRepository;

    private final String SECRET = "my-secret";
    private final String ISSUER = "api-liq-rota";

    public TokenService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public TokenDTO montarToken(Usuario usuario){
        String token = gerarToken(usuario);
        return new TokenDTO(
                token,
                usuario.getPerfil().name(),
                obterTipoDoToken(),
                obterDataExpiracao(token)
        );
    }

    public String gerarToken(Usuario usuario){
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername())
                    .withClaim("Role", usuario.getPerfil().name())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(8L, ChronoUnit.HOURS))
                    .sign(algorithm);
        } catch (JWTCreationException e){
            throw new TokenInvalidoException("Erro na criação do token");
        }
    }

    @Transactional(readOnly = true)
    public Usuario validarToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

        try {
            String email = verifier.verify(token).getSubject();

            return usuarioRepository.findUsuarioByUsername(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        } catch (JWTCreationException | TokenExpiredException exception) {
            throw new TokenInvalidoException("Token expirado ou inválido");
        }
    }

    private String obterTipoDoToken() {
        return "Bearer";
    }

    private Instant obterDataExpiracao(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getExpiresAtAsInstant();
    }

}
