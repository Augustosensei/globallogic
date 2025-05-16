package com.globallogic.microservice1.controller;






import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.security.JwtUtils;
import com.globallogic.microservice1.service.IUsuarioService;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)   // desactiva filtros de seguridad
class UsuarioControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean IUsuarioService usuarioService;
    @MockBean JwtUtils jwtUtils;             // mock para satisfacer SecurityConfig

    /* ---------- POST /sign-up OK ---------- */
    @Test
    @DisplayName("signUp: crea usuario y responde 200 con cuerpo")
    void signUp_ok_returns200() throws Exception {
        // Entrada
        UsuarioDTO in = new UsuarioDTO();
        in.setName("Denise");
        in.setEmail("denise@example.com");
        in.setPassword("a2asfGfdfdf4");

        // Mocks service
        Usuario entidad = new Usuario();
        entidad.setId(1L);
        UsuarioResponseDTO out = UsuarioResponseDTO.builder()
                .id(1L)
                .name("Denise")
                .email("denise@example.com")
                .created(LocalDateTime.now())
                .isActive(true)
                .build();

        given(usuarioService.crearUsuario(any(UsuarioDTO.class))).willReturn(entidad);
        given(usuarioService.mapToResponseDTO(entidad)).willReturn(out);

        // Ejecución + asserts
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("denise@example.com"));
    }

    /* ---------- POST /sign-up 400 ---------- */
    @Test
    @DisplayName("signUp: email faltante -> 400 Bad Request")
    void signUp_invalidEmail_returns400() throws Exception {
        UsuarioDTO in = new UsuarioDTO(); // sin email ni password
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    /* ---------- GET / (listar) ---------- */
    @Test
    @DisplayName("listarUsuarios: devuelve lista")
    void listarUsuarios_ok() throws Exception {
        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(1L)
                .email("denise@example.com")
                .name("Denise")
                .build();
        given(usuarioService.listarTodos()).willReturn(List.of(dto));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("denise@example.com"));
    }

    /* ---------- GET /email/{email} ---------- */
    @Test
    @DisplayName("getByEmail: encuentra usuario por email")
    void getByEmail_ok() throws Exception {
        Usuario entidad = new Usuario();
        entidad.setEmail("denise@example.com");
        UsuarioDTO dto = new UsuarioDTO();
        dto.setEmail("denise@example.com");

        given(usuarioService.getEntidadPorEmail("denise@example.com")).willReturn(entidad);
        given(usuarioService.mapToDTO(entidad)).willReturn(dto);

        mockMvc.perform(get("/email/denise@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("denise@example.com"));
    }

    /* ---------- GET /perfil/{email} ---------- */
    @Test
    @DisplayName("perfil: devuelve perfil del usuario")
    void perfil_ok() throws Exception {
        Usuario entidad = new Usuario();
        entidad.setEmail("denise@example.com");
        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .email("denise@example.com")
                .name("Denise")
                .build();

        given(usuarioService.getEntidadPorEmail("denise@example.com")).willReturn(entidad);
        given(usuarioService.mapToResponseDTO(entidad)).willReturn(dto);

        mockMvc.perform(get("/perfil/denise@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("denise@example.com"));
    }
}
