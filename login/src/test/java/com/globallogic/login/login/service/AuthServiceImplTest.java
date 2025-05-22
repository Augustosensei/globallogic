package com.globallogic.login.login.service;


import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.*;
import com.globallogic.login.login.exception.ClienteException;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.impl.AuthServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String EMAIL = "denise@example.com";
    private static final String RAW_PASSWORD = "a2asfGfdfdf4";
    private static final String HASHED_PASSWORD = "$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String PROVISIONAL_JWT = "provisional.jwt";
    private static final String FINAL_JWT = "final.jwt";

    @Mock private UsuarioClient usuarioClient;
    @Mock private JwtUtils jwtUtils;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthServiceImpl authService;

    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /* ---------- Caso feliz ---------- */
    @Test
    void login_success_returnsLoginResponseDTO() {
        // given
        LoginRequestDTO req = new LoginRequestDTO(EMAIL, RAW_PASSWORD);

        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder()
                .id(UUID.randomUUID())
                .email(EMAIL)
                .name("Denise")
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .password(HASHED_PASSWORD)
                .phones(Collections.singletonList(
                        TelefonoDTO.builder()
                                .number(1234567890L)
                                .citycode(11)
                                .contrycode("+54")
                                .build()))
                .build();

        given(jwtUtils.generateToken(EMAIL)).willReturn(PROVISIONAL_JWT);
        given(usuarioClient.getByEmail(EMAIL)).willReturn(usuario);
        given(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).willReturn(true);
        given(jwtUtils.generateToken(EMAIL)).willReturn(FINAL_JWT);

        // when
        LoginResponseDTO response = authService.login(req);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(FINAL_JWT);
        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getName()).isEqualTo("Denise");
        assertThat(response.getPhones()).hasSize(1);
    }

    /* ---------- Feign error ---------- */
    @Test
    void login_usuarioClientThrowsFeignException_throwsClienteExceptionBadGateway() {
        // given
        LoginRequestDTO req = new LoginRequestDTO(EMAIL, RAW_PASSWORD);

        given(jwtUtils.generateToken(EMAIL)).willReturn(PROVISIONAL_JWT);
        given(usuarioClient.getByEmail(EMAIL)).willThrow(mock(FeignException.class));

        // when / then
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("No se pudo obtener el usuario")
                .extracting("status").isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    /* ---------- Password incorrecta ---------- */
    @Test
    void login_badPassword_throwsClienteExceptionUnauthorized() {
        // given
        LoginRequestDTO req = new LoginRequestDTO(EMAIL, RAW_PASSWORD);
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder()
                .email(EMAIL)
                .password(HASHED_PASSWORD)
                .build();

        given(jwtUtils.generateToken(EMAIL)).willReturn(PROVISIONAL_JWT);
        given(usuarioClient.getByEmail(EMAIL)).willReturn(usuario);
        given(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).willReturn(false);

        // when / then
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ClienteException.class)
                .hasMessageContaining("Credenciales inválidas")
                .extracting("status").isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
