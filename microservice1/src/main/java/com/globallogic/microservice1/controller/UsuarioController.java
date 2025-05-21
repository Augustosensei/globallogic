package com.globallogic.microservice1.controller;


import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @PostMapping("/sign-up")
    public ResponseEntity<UsuarioResponseDTO> signUp(@Valid @RequestBody UsuarioDTO dto) {
        Usuario nuevo = usuarioService.crearUsuario(dto);
        UsuarioResponseDTO response = usuarioService.mapToResponseDTO(nuevo);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> getByEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.getEntidadPorEmail(email);
        UsuarioResponseDTO dto = usuarioService.mapToResponseDTO(usuario);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/perfil/{email}")
    public ResponseEntity<UsuarioResponseDTO> perfil(@PathVariable String email) {
        Usuario usuario = usuarioService.getEntidadPorEmail(email);
        UsuarioResponseDTO dto = usuarioService.mapToResponseDTO(usuario);
        return ResponseEntity.ok(dto);
    }

}
