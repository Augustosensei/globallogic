package com.globallogic.microservice1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.security.JwtUtils;
import com.globallogic.microservice1.service.IUsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private IUsuarioService usuarioService;
    @MockBean private JwtUtils jwtUtils;

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String EMAIL = "denise@example.com";

    @Test
    @DisplayName("signUp: crea usuario y responde 200 con cuerpo")
    void signUp_ok_returns200() throws Exception {
        // Entrada
        UsuarioDTO in = new UsuarioDTO();
        in.setName("Denise");
        in.setEmail(EMAIL);
        in.setPassword("A1bcdefg2"); // válida según regex

        // Entidad y respuesta esperada
        Usuario entidad = new Usuario();
        entidad.setId(ID);
        entidad.setEmail(EMAIL);
        entidad.setName("Denise");

        UsuarioResponseDTO out = UsuarioResponseDTO.builder()
                .id(ID)
                .name("Denise")
                .email(EMAIL)
                .created(LocalDateTime.now())
                .isActive(true)
                .build();

        given(usuarioService.crearUsuario(any(UsuarioDTO.class))).willReturn(entidad);
        given(usuarioService.mapToResponseDTO(entidad)).willReturn(out);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @DisplayName("signUp: email faltante -> 400 Bad Request")
    void signUp_invalidEmail_returns400() throws Exception {
        UsuarioDTO in = new UsuarioDTO(); // email y password vacíos
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("listarUsuarios: devuelve lista")
    void listarUsuarios_ok() throws Exception {
        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(ID)
                .email(EMAIL)
                .name("Denise")
                .build();

        given(usuarioService.listarTodos()).willReturn(List.of(dto));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    @DisplayName("getByEmail: encuentra usuario por email")
    void getByEmail_ok() throws Exception {
        Usuario entidad = new Usuario();
        entidad.setEmail(EMAIL);
        entidad.setName("Denise");

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .email(EMAIL)
                .name("Denise")
                .build();

        given(usuarioService.getEntidadPorEmail(EMAIL)).willReturn(entidad);
        given(usuarioService.mapToResponseDTO(entidad)).willReturn(dto);

        mockMvc.perform(get("/email/{email}", EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @DisplayName("perfil: devuelve perfil del usuario")
    void perfil_ok() throws Exception {
        Usuario entidad = new Usuario();
        entidad.setEmail(EMAIL);
        entidad.setName("Denise");

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .email(EMAIL)
                .name("Denise")
                .build();

        given(usuarioService.getEntidadPorEmail(EMAIL)).willReturn(entidad);
        given(usuarioService.mapToResponseDTO(entidad)).willReturn(dto);

        mockMvc.perform(get("/perfil/{email}", EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }
}
