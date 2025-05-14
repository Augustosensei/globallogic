
package com.globallogic.microservice1.security;




import com.globallogic.microservice1.config.PasswordConfig;  // opcional, solo para claridad
import com.globallogic.microservice1.exception.ClienteException;
import com.globallogic.microservice1.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;   // ya viene de PasswordConfig
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtils jwtUtils;
    private final IUsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;



    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .headers().frameOptions().disable()
        .and()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
            .antMatchers("/sign-up", "/login").permitAll()
            .antMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
            .anyRequest().authenticated()
        .and()
          .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
          .userDetailsService(userDetailsService())
          .passwordEncoder(passwordEncoder);   // aprovechamos el bean de PasswordConfig
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepo.findByEmail(username)
            .map(entidad -> org.springframework.security.core.userdetails.User.builder()
                .username(entidad.getEmail())
                .password(entidad.getPassword())
                .authorities("ROLE_USER")
                .build()
            )
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuario no encontrado: " + username)
            );
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtils, userDetailsService());
    }

  
}
