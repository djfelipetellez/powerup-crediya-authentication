package co.com.pragma.usecase.usuario;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;

    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioUseCase = new UsuarioUseCase(usuarioRepository, rolRepository);
    }

    @Test
    void registrarUsuarioExitoso() {
        Usuario usuario = Usuario.builder()
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .build();

        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .build();

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void registrarUsuarioFallaPorCorreoExistente() {
        Usuario usuario = Usuario.builder()
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .build();

        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .build();
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(usuario));
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void registrarUsuarioFallaPorNombreInvalido() {
        Usuario usuario = Usuario.builder()
                .nombre("")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .build();

        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .build();

        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void registrarUsuarioFallaPorRolNoEncontrado() {
        Usuario usuario = Usuario.builder()
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .build();

        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 99))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Rol no encontrado"))
                .verify();
    }
}