package com.globallogic.microservice1.service;

import java.util.List;

import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.model.Telefono;

public interface ITelefonoService {

    Telefono crearTelefono(Long usuarioId, TelefonoDTO dto);
    List<Telefono> listarPorUsuario(Long usuarioId);
    void eliminarTelefono(Long telefonoId);
}
