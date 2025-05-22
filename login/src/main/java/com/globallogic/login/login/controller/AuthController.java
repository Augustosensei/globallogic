package com.globallogic.login.login.controller;

import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.UsuarioResponseDTO;
import com.globallogic.login.login.exception.ClienteException;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.IAuthService;
import com.globallogic.login.login.service.impl.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtBlacklistService jwtBlacklistService;
    private  final JwtUtils jwtUtils;
    private final UsuarioClient usuarioClient;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletResponse response) {

        LoginResponseDTO loginResponse = authService.login(request);

        ResponseCookie jwtCookie = ResponseCookie.from("TOKEN", loginResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(3600)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(loginResponse);       // si ya no lo necesitas, descarta el body
    }




    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "TOKEN", required = false) String token) {

        if (token != null && !token.isEmpty()) {
            jwtBlacklistService.blacklistToken(token);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }






}
