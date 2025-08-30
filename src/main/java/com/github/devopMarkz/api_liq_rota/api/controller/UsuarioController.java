package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioRequestDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioResponseDTO;
import com.github.devopMarkz.api_liq_rota.domain.service.UsuarioService;
import com.github.devopMarkz.api_liq_rota.utils.GerenciadorDePermissoes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.github.devopMarkz.api_liq_rota.utils.GeradorDeUri.generateUri;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Void> criarUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO){
        Long id = usuarioService.criarUsuario(requestDTO);
        URI uri = generateUri(id);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_ADMINISTRADOR)
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id){
        UsuarioResponseDTO responseDTO = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO requestDTO){
        usuarioService.atualizarUsuario(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_ADMINISTRADOR)
    @Deprecated(forRemoval = true)
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id){
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_ADMINISTRADOR)
    public ResponseEntity<Void> desativarUsuario(@PathVariable Long id){
        usuarioService.desativarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}