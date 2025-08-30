package com.github.devopMarkz.api_liq_rota.domain.service;

import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioRequestDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioResponseDTO;
import com.github.devopMarkz.api_liq_rota.api.exception.EntidadeInexistenteException;
import com.github.devopMarkz.api_liq_rota.api.exception.ViolacaoUnicidadeChaveException;
import com.github.devopMarkz.api_liq_rota.infraestructure.mapper.UsuarioMapper;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import com.github.devopMarkz.api_liq_rota.domain.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long criarUsuario(UsuarioRequestDTO requestDTO){
        validarViolacaoDeFK(requestDTO.getUsername());
        Usuario usuario = usuarioMapper.toUsuario(requestDTO);
        usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        usuarioRepository.save(usuario);
        return usuario.getId();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new EntidadeInexistenteException("Usuário não encontrado."));
        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Transactional(rollbackFor = Exception.class)
    public void atualizarUsuario(Long id, UsuarioRequestDTO requestDTO){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeInexistenteException("Usuário não encontrado."));

        validarViolacaoDeFK(requestDTO.getUsername());

        Usuario usuarioAtualizado = usuarioMapper.toUsuario(requestDTO);
        usuarioAtualizado.setSenha(passwordEncoder.encode(requestDTO.getSenha()));

        BeanUtils.copyProperties(usuarioAtualizado, usuario, "id", "perfil", "ativo", "refreshTokenJti", "plano");

        usuarioRepository.save(usuario);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletarUsuario(Long id){
        if(!usuarioRepository.existsById(id)) {
            throw new EntidadeInexistenteException("Usuário inexistente.");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void desativarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeInexistenteException("Usuário inexistente."));

        usuario.desativar();

        usuarioRepository.save(usuario);
    }

    private void validarViolacaoDeFK(String email) {
        if(usuarioRepository.existsUsuarioByUsername((email))) {
            throw new ViolacaoUnicidadeChaveException(email + " já está sendo utilizado.");
        }
    }

}
