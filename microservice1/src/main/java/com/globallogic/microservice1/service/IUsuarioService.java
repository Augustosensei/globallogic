package com.globallogic.microservice1.service;

import java.util.List;

import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.model.Usuario;

public interface IUsuarioService {
    
    Usuario crearUsuario(UsuarioDTO dto);
    Usuario iniciarSesion(String email);
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    Usuario buscarPorEmail(String email);
}
