package com.globallogic.login.login.controller;


import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.TelefonoDTO;
import com.globallogic.login.login.service.IAuthService;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.impl.JwtBlacklistService;
import com.globallogic.login.login.client.UsuarioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private IAuthService authService;

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private AuthController controller;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO("denise@example.com", "123456");

        loginResponse = LoginResponseDTO.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("fake-jwt-token")
                .isActive(true)
                .name("Denise")
                .email("denise@example.com")
                .password("hashed-password")
                .phones(Collections.singletonList(TelefonoDTO.builder()
                        .number(1234567890L)
                        .citycode(11)
                        .contrycode("+54")
                        .build()))
                .build();
    }

    @Test
    void testLogin_exitoso() {
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<LoginResponseDTO> response = controller.login(loginRequest, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(loginResponse);
        assertThat(response.getHeaders().get("Set-Cookie")).isNotEmpty();

        verify(authService).login(loginRequest);
    }

    @Test
    void testLogout_conToken() {
        String token = "valid-jwt-token";

        ResponseEntity<Void> response = controller.logout(token);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        assertThat(response.getHeaders().get("Set-Cookie")).isNotEmpty();

        verify(jwtBlacklistService).blacklistToken(token);
    }

    @Test
    void testLogout_sinToken() {
        String token = null;

        ResponseEntity<Void> response = controller.logout(token);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        assertThat(response.getHeaders().get("Set-Cookie")).isNotEmpty();

        verify(jwtBlacklistService, never()).blacklistToken(any());
    }
}
