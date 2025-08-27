package co.com.pragma.usecase.usuario;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .build();

        rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrador")
                .build();
    }

    @Test
    void registrarUsuarioExitoso() {
        // Arrange
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextMatches(savedUser -> {
                    return savedUser.getNombre().equals("Test") && savedUser.getRol().getNombre().equals("ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void registrarUsuarioFalla_CuandoEmailYaExiste() {
        // Arrange
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(usuario)); // Simulate user exists
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario)); // por seguridad

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(IllegalArgumentException.class);
                    assertThat(error).hasMessage("Ya existe un usuario registrado con ese email.");
                })
                .verify();
    }

    @Test
    void registrarUsuarioFalla_CuandoRolNoExiste() {
        // Arrange
        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty()); // Simulate role not found

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 99))
                .expectErrorMessage("El rol no existe en el sistema.")
                .verify();
    }

    @Test
    void registrarUsuarioFalla_CuandoSalarioEsMenorAlMinimo() {
        // Arrange
        usuario.setSalarioBase(-100.0);

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMessage("El salario base debe estar entre 0 y 15000000")
                .verify();
    }

    @Test
    void registrarUsuarioFalla_CuandoSalarioEsMayorAlMaximo() {
        // Arrange
        usuario.setSalarioBase(20000000.0);

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMessage("El salario base debe estar entre 0 y 15000000")
                .verify();
    }

    @Test
    void registrarUsuarioFalla_CuandoSalarioEsNulo() {
        // Arrange
        usuario.setSalarioBase(null);

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMessage("El salario base debe estar entre 0 y 15000000")
                .verify();
    }
}
