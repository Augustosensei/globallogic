package com.globallogic.microservice1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.globallogic.microservice1.model.Telefono;

public interface ITelefonoRepository extends JpaRepository<Telefono, Long> {
    List<Telefono> findByUsuarioId(Long usuarioId);
}
