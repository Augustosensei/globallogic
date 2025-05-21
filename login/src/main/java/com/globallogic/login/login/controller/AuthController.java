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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO loginResponse = authService.login(request);

        ResponseCookie jwtCookie = ResponseCookie.from("TOKEN", loginResponse.getToken())
                .httpOnly(true)
                .secure(false) // secure=false para localhost
                .path("/")
                .maxAge(3600) // duración del token en segundos (1 hora)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(loginResponse);
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "TOKEN", required = false) String token,
                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                       HttpServletResponse response) {


        String realToken = token;
        if ((realToken == null || realToken.isEmpty()) && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            realToken = authorizationHeader.substring(7);
        }

        if (realToken != null && !realToken.isEmpty()) {
            jwtBlacklistService.blacklistToken(realToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }





}
