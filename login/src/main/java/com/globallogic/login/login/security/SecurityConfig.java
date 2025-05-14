
package com.globallogic.login.login.security;






import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // No requiere protección CSRF
            .headers().frameOptions().disable() // Por si usás H2
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT: sin sesión
            .and()
            .authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .anyRequest().authenticated(); // En caso de agregar otros endpoints
    }
}
