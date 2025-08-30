package com.github.devopMarkz.api_liq_rota.domain.repository;

import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findUsuarioByUsername(String username);

    boolean existsUsuarioByUsername(String username);
}
