package com.globallogic.login.login.controller;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.service.IAuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock  private IAuthService authService;
    @InjectMocks private AuthController controller;

    @Test
    void login_ok_returnsResponseEntityAndDelegatesToService() {
        // Arrange
        LoginRequestDTO req = new LoginRequestDTO("denise@example.com", "a2asfGfdfdf4");
        LoginResponseDTO expected = LoginResponseDTO.builder()
                .token("jwt.token")
                .email("denise@example.com")
                .lastLogin(LocalDateTime.now())
                .build();

        when(authService.login(req)).thenReturn(expected);

        // Act
        ResponseEntity<LoginResponseDTO> response = controller.login(req);

        // Assert
        verify(authService).login(req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
