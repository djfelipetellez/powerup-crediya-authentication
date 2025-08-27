package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.r2dbc.entity.RolEntity;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {

    @Mock
    private UsuarioReactiveRepository usuarioRepository;
    @Mock
    private RolReactiveRepository rolRepository;

    @InjectMocks
    private UsuarioReactiveRepositoryAdapter adapter;

    private Usuario usuario;
    private UsuarioEntity usuarioEntity;
    private Rol rol;
    private RolEntity rolEntity;

    @BeforeEach
    void setUp() {
        rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrator")
                .build();

        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .rol(rol)
                .build();

        usuarioEntity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(5000000.0)
                .idRol(1)
                .build();

        rolEntity = new RolEntity(1, "ADMIN", "Administrator");
    }

    @Test
    void save_shouldReturnSavedUsuario_whenSuccessful() {
        // Arrange
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(usuarioEntity));

        // Act & Assert
        StepVerifier.create(adapter.save(usuario))
                .expectNextMatches(saved -> saved.getEmail().equals(usuario.getEmail()) && saved.getRol() != null)
                .verifyComplete();
    }

    @Test
    void save_shouldThrowException_whenRolIsNull() {
        // Arrange
        usuario.setRol(null);

        // Act & Assert
        StepVerifier.create(adapter.save(usuario))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_shouldReturnUsuario_whenFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rolEntity));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .expectNextMatches(found -> found.getEmail().equals(usuario.getEmail()) && found.getRol().getNombre().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldThrowException_whenUserHasNoRol() {
        // Arrange
        usuarioEntity.setIdRol(null);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(usuarioEntity));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .expectError(IllegalStateException.class)
                .verify();
    }
}
