package com.globallogic.microservice1.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Filtro que extrae el JWT de la cabecera Authorization, lo valida
 * y, si es correcto, carga el UserDetails y establece la autenticación.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils,
                         UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        // 1. Obtener token de la cabecera Authorization
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        // 2. Si existe token y no hay autenticación en el contexto
        if (token != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            // 2.1 Extraer username del token
            String username = jwtUtils.extractUsername(token);

            if (username != null) {
                // 2.2 Cargar usuario de la base
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 2.3 Validar token
                if (jwtUtils.validateToken(token, userDetails)) {
                    // 2.4 Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 2.5 Asignar al contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
