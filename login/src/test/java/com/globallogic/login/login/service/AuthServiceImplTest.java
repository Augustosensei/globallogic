package com.globallogic.login.login.service;


import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.UsuarioDTO;
import com.globallogic.login.login.exception.ClienteException;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String EMAIL = "denise@example.com";
    private static final String RAW_PASSWORD = "a2asfGfdfdf4";
    private static final String HASHED_PASSWORD = "$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String PROVISIONAL_JWT = "provisional.jwt";
    private static final String FINAL_JWT = "final.jwt";

    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /* ---------- Caso feliz ---------- */
    @Test
    void login_ok_returns_response_and_sets_context() {
        // given
        LoginRequestDTO req = new LoginRequestDTO(EMAIL, RAW_PASSWORD);
        UsuarioDTO usuario = UsuarioDTO.builder()
                .email(EMAIL)
                .password(HASHED_PASSWORD)
                .build();

        // mocks
        given(jwtUtils.generateToken(EMAIL))
                .willReturn(PROVISIONAL_JWT)   // primera llamada
                .willReturn(FINAL_JWT);        // segunda llamada
        given(usuarioClient.getByEmail(EMAIL)).willReturn(usuario);
        given(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).willReturn(true);

        // when
        LoginResponseDTO res = authService.login(req);

        // then
        assertThat(res.getToken()).isEqualTo(FINAL_JWT);
        assertThat(res.getEmail()).isEqualTo(EMAIL);
        assertThat(res.getLastLogin())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        // y el contexto realmente contiene el token final
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getCredentials()).isEqualTo(FINAL_JWT);
        assertThat(auth.getPrincipal()).isEqualTo(EMAIL);

        // verify interactions
        then(usuarioClient).should().getByEmail(EMAIL);
        then(passwordEncoder).should().matches(RAW_PASSWORD, HASHED_PASSWORD);
        then(jwtUtils).should(times(2)).generateToken(EMAIL);
    }


    @Test
    void login_badPassword_throwsClienteExceptionUnauthorized() {
        // given
        LoginRequestDTO req = new LoginRequestDTO(EMAIL, RAW_PASSWORD);
        UsuarioDTO usuario = UsuarioDTO.builder().email(EMAIL).password(HASHED_PASSWORD).build();

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
