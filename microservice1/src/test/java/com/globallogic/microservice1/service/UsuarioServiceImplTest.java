package com.globallogic.microservice1.service;


import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.exception.ClienteException;
import com.globallogic.microservice1.model.Telefono;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.repository.IUsuarioRepository;
import com.globallogic.microservice1.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    IUsuarioRepository usuarioRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UsuarioServiceImpl service;

    @Captor
    ArgumentCaptor<Usuario> userCaptor;


    @Nested
    class CrearUsuario {

        @Test
        @DisplayName("crea usuario cuando email no existe")
        void crear_ok() {
            // stub necesario sólo aquí
            given(passwordEncoder.encode(anyString())).willReturn("hashedPwd");
            given(usuarioRepo.existsByEmail("denise@example.com")).willReturn(false);
            given(usuarioRepo.save(any(Usuario.class)))
                    .willAnswer(inv -> inv.getArgument(0));

            UsuarioDTO dto = buildDto();

            Usuario saved = service.crearUsuario(dto);

            verify(usuarioRepo).save(userCaptor.capture());
            Usuario value = userCaptor.getValue();
            assertThat(value.getPassword()).isEqualTo("hashedPwd");
            assertThat(saved).isSameAs(value);
        }


        @Test
        @DisplayName("lanza excepción si email duplica")
        void crear_emailDuplicado() {


            given(usuarioRepo.existsByEmail("denise@example.com")).willReturn(true);


            assertThatThrownBy(() -> service.crearUsuario(buildDto()))
                    .isInstanceOf(ClienteException.class)
                    .extracting("status").isEqualTo(HttpStatus.CONFLICT);

            //  verificamos que NO se intentó persistir ni encriptar
            then(usuarioRepo).shouldHaveNoMoreInteractions();
            then(passwordEncoder).shouldHaveNoInteractions();
        }


        @Test
        @DisplayName("iniciarSesion actualiza lastLogin")
        void iniciarSesion() {
            Usuario u = buildEntity();
            given(usuarioRepo.findByEmail("denise@example.com")).willReturn(Optional.of(u));
            given(usuarioRepo.save(any(Usuario.class))).willAnswer(inv -> inv.getArgument(0));

            Usuario updated = service.iniciarSesion("denise@example.com");

            assertThat(updated.getLastLogin()).isNotNull();
            verify(usuarioRepo).save(updated);
        }




        @Nested
        class Buscar {

            @Test
            void porId_ok() {
                Usuario u = buildEntity();
                given(usuarioRepo.findById(1L)).willReturn(Optional.of(u));

                assertThat(service.buscarPorId(1L)).isSameAs(u);
            }

            @Test
            void porId_noExiste() {
                given(usuarioRepo.findById(99L)).willReturn(Optional.empty());

                assertThatThrownBy(() -> service.buscarPorId(99L))
                        .isInstanceOf(ClienteException.class)
                        .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
            }

            @Test
            void perfilPorEmail_ok() {
                given(usuarioRepo.findByEmail("denise@example.com"))
                        .willReturn(Optional.of(buildEntity()));

                UsuarioResponseDTO dto =
                        service.buscarPerfilPorEmail("denise@example.com");

                assertThat(dto.getTelefonos()).hasSize(1);
            }
        }


        // helpers --------------------------------------------------------------
        private UsuarioDTO buildDto() {
            TelefonoDTO tel = new TelefonoDTO();
            tel.setNumber(123L);
            tel.setCitycode(54);
            tel.setContrycode("+54");

            UsuarioDTO dto = new UsuarioDTO();
            dto.setName("Denise");
            dto.setEmail("denise@example.com");
            dto.setPassword("a2asfGfdfdf4");
            dto.setTelefonos(List.of(tel));
            return dto;
        }

        private Usuario buildEntity() {
            Telefono tel = new Telefono();
            tel.setNumber(123L);
            tel.setCitycode(54);
            tel.setContrycode("+54");

            UUID expectedId = UUID.fromString("00000000-0000-0000-0000-000000000001");

            Usuario u = new Usuario();
            u.setId(expectedId);
            u.setName("Denise");
            u.setEmail("denise@example.com");
            u.setCreated(LocalDateTime.now().minusDays(1));
            u.setLastLogin(LocalDateTime.now().minusHours(1));
            u.setIsActive(true);
            u.setTelefonos(Set.of(tel));
            return u;
        }
    }
}
