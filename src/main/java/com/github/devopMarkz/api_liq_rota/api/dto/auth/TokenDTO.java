package com.github.devopMarkz.api_liq_rota.api.dto.auth;

import java.time.Instant;

public record TokenDTO(
        String access_token,
        String role,
        String token_type,
        Instant expires_in
) {
}
