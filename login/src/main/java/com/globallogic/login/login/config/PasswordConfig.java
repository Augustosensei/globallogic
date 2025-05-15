package com.globallogic.login.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;


@Configuration
public class SpringConfig {
    


@Bean
public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
}

}
