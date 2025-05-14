package com.globallogic.microservice1.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Utilidades para generar y validar tokens JWT.
 */
@Component
public class JwtUtils {

    /** Clave secreta para firmar el token (configurable en application.properties). */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /** Duración del token en milisegundos (configurable en application.properties). */
    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    /**
     * Extrae cualquier claim usando la función proporcionada.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae el nombre de usuario (email) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Genera un nuevo token para el usuario proporcionado.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }

    /**
     * Valida que el token corresponda al usuario y no esté expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Comprueba si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Parsea el token y devuelve todos los claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
    }
}
