package com.globallogic.microservice1.service;

import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.model.Telefono;

import java.util.List;

public interface ITelefonoService {

    Telefono crearTelefono(Long usuarioId, TelefonoDTO dto);

    List<Telefono> listarPorUsuario(Long usuarioId);

    void eliminarTelefono(Long telefonoId);
}
