package com.globallogic.microservice1.controller;




import com.globallogic.microservice1.model.Telefono;
import com.globallogic.microservice1.service.ITelefonoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/telefonos")
@RequiredArgsConstructor
public class TelefonoController {

    private final ITelefonoService telefonoService;

    // Listar teléfonos de un usuario por su ID
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Telefono>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(telefonoService.listarPorUsuario(usuarioId));
    }
}

