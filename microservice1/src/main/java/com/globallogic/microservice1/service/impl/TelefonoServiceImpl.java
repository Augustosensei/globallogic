package com.globallogic.microservice1.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.exception.ClienteException;
import com.globallogic.microservice1.model.Telefono;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.repository.ITelefonoRepository;
import com.globallogic.microservice1.repository.IUsuarioRepository;
import com.globallogic.microservice1.service.ITelefonoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelefonoServiceImpl implements ITelefonoService {

    private final ITelefonoRepository telefonoRepo;
    private final IUsuarioRepository usuarioRepo;

    @Override
    public Telefono crearTelefono(Long usuarioId, TelefonoDTO dto) {
        Usuario u = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        Telefono p = new Telefono();
        p.setNumber(dto.getNumber());
        p.setCitycode(dto.getCitycode());
        p.setContrycode(dto.getContrycode());
        p.setUsuario(u);

        return telefonoRepo.save(p);
    }

    @Override
    public List<Telefono> listarPorUsuario(Long usuarioId) {
        // Optional: validar existencia de usuario
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        return telefonoRepo.findByUsuarioId(usuarioId);
    }

    @Override
    public void eliminarTelefono(Long telefonoId) {
        if (!telefonoRepo.existsById(telefonoId)) {
            throw new ClienteException("Teléfono no encontrado", HttpStatus.NOT_FOUND);
        }
        telefonoRepo.deleteById(telefonoId);
    }
}
