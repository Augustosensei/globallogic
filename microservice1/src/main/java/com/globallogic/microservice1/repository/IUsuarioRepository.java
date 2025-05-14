package com.globallogic.microservice1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.globallogic.microservice1.model.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
}
