package com.globallogic.microservice1.controller;



import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.security.JwtUtils;
import com.globallogic.microservice1.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final JwtUtils jwtUtils;

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody UsuarioDTO dto) {
        Usuario nuevo = usuarioService.crearUsuario(dto);
        String token = jwtUtils.generateToken(
            User.builder()
                .username(nuevo.getEmail())
                .password(nuevo.getPassword())
                .authorities("ROLE_USER")
                .build()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevo.getId());
        response.put("created", nuevo.getCreated());
        response.put("lastLogin", nuevo.getLastLogin());
        response.put("isActive", nuevo.getIsActive());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

  
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email) {
        Usuario u = usuarioService.iniciarSesion(email);
        String token = jwtUtils.generateToken(
            User.builder()
                .username(u.getEmail())
                .password(u.getPassword())
                .authorities("ROLE_USER")
                .build()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("id", u.getId());
        response.put("created", u.getCreated());
        response.put("lastLogin", u.getLastLogin());
        response.put("isActive", u.getIsActive());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

  
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }


    @GetMapping("/perfil")
    public ResponseEntity<Usuario> perfil(Principal principal) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        return ResponseEntity.ok(usuario);
    }
}
