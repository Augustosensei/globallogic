package com.globallogic.microservice1.service;

import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.model.Usuario;

import java.util.List;

public interface IUsuarioService {

    Usuario crearUsuario(UsuarioDTO dto);

    Usuario iniciarSesion(String email);

    List<UsuarioResponseDTO> listarTodos();

    Usuario buscarPorId(Long id);

    UsuarioResponseDTO buscarPerfilPorEmail(String email);

    UsuarioDTO buscarPorEmail(String email);

    UsuarioResponseDTO mapToResponseDTO(Usuario usuario);

    Usuario getEntidadPorEmail(String email);

    UsuarioDTO mapToDTO(Usuario usuario);
}
