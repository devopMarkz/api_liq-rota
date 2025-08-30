package com.github.devopMarkz.api_liq_rota.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

    private static final String SECRET = "my-secret";
    private static final String ISSUER = "api-liq-rota";
    private static final String BEARER_PREFIX = "Bearer ";

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
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername())
                    .withClaim("Role", usuario.getPerfil().name())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                    .sign(algorithm);
        } catch (JWTCreationException e){
            throw new TokenInvalidoException("Erro na criação do token");
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException("Configuração de token inválida");
        }
    }

    @Transactional(readOnly = true)
    public Usuario validarToken(String token) {
        String sanitized = sanitizeToken(token);

        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            String username = verifier.verify(sanitized).getSubject();

            return usuarioRepository.findUsuarioByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        } catch (TokenExpiredException e) {
            throw new TokenInvalidoException("Token expirado");
        } catch (JWTVerificationException e) {
            throw new TokenInvalidoException("Token inválido");
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException("Token ausente ou malformado");
        }
    }

    private String obterTipoDoToken() {
        return "Bearer";
    }

    private Instant obterDataExpiracao(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(sanitizeToken(token))
                    .getExpiresAtAsInstant();
        } catch (TokenExpiredException e) {
            throw new TokenInvalidoException("Token expirado");
        } catch (JWTVerificationException | IllegalArgumentException e) {
            throw new TokenInvalidoException("Token inválido");
        }
    }

    /* ===== helpers ===== */

    private String sanitizeToken(String token) {
        if (token == null) throw new IllegalArgumentException("token nulo");
        String t = token.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("token vazio");
        if (t.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            t = t.substring(BEARER_PREFIX.length()).trim();
        }
        return t;
    }
}
