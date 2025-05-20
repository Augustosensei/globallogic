package com.globallogic.microservice1.repository;

import com.globallogic.microservice1.model.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITelefonoRepository extends JpaRepository<Telefono, Long> {
    List<Telefono> findByUsuarioId(Long usuarioId);
}
