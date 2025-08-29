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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(rol)
                .build();

        usuarioEntity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(1)
                .build();

        rolEntity = new RolEntity(1, "ADMIN", "Administrator");
    }

    // ========== PRUEBAS PARA SAVE ==========

    @Test
    void save_shouldReturnSavedUsuario_whenSuccessful() {
        // Arrange
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(usuarioEntity));

        // Act & Assert
        StepVerifier.create(adapter.save(usuario))
                .expectNextMatches(saved ->
                        saved.getIdUsuario().equals(usuario.getIdUsuario()) &&
                                saved.getNombre().equals(usuario.getNombre()) &&
                                saved.getApellido().equals(usuario.getApellido()) &&
                                saved.getEmail().equals(usuario.getEmail()) &&
                                saved.getDocumentoIdentidad().equals(usuario.getDocumentoIdentidad()) &&
                                saved.getTelefono().equals(usuario.getTelefono()) &&
                                saved.getSalarioBase().equals(usuario.getSalarioBase()) &&
                                saved.getRol().getIdRol().equals(usuario.getRol().getIdRol())
                )
                .verifyComplete();

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void save_shouldSaveNewUser_whenIdUsuarioIsNull() {
        // Arrange
        Usuario nuevoUsuario = Usuario.builder()
                .idUsuario(null) // Usuario nuevo sin ID
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(rol)
                .build();

        UsuarioEntity savedEntity = UsuarioEntity.builder()
                .idUsuario(5) // ID generado por la BD
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(1)
                .build();

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));

        // Act & Assert
        StepVerifier.create(adapter.save(nuevoUsuario))
                .expectNextMatches(saved ->
                        saved.getIdUsuario().equals(5) &&
                                saved.getEmail().equals(nuevoUsuario.getEmail()) &&
                                saved.getRol() != null
                )
                .verifyComplete();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void save_shouldThrowException_whenRolIsNull() {
        // Arrange
        Usuario usuarioSinRol = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(null) // Sin rol
                .build();

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioSinRol))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().contains("El usuario debe tener un rol asignado")
                )
                .verify();

        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    void save_shouldThrowException_whenRolIdIsNull() {
        // Arrange
        Rol rolSinId = Rol.builder()
                .idRol(null) // Sin ID
                .nombre("ADMIN")
                .descripcion("Administrator")
                .build();

        Usuario usuarioConRolSinId = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(rolSinId)
                .build();

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioConRolSinId))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().contains("El usuario debe tener un rol asignado")
                )
                .verify();

        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    void save_shouldHandleRepositoryError() {
        // Arrange
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(adapter.save(usuario))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    // ========== PRUEBAS PARA FINDBYEMAIL ==========

    @Test
    void findByEmail_shouldReturnUsuario_whenFoundWithRol() {
        // Arrange
        when(usuarioRepository.findByEmail("juan.perez@example.com"))
                .thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rolEntity));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan.perez@example.com"))
                .expectNextMatches(found ->
                        found.getIdUsuario().equals(usuario.getIdUsuario()) &&
                                found.getNombre().equals(usuario.getNombre()) &&
                                found.getApellido().equals(usuario.getApellido()) &&
                                found.getEmail().equals(usuario.getEmail()) &&
                                found.getDocumentoIdentidad().equals(usuario.getDocumentoIdentidad()) &&
                                found.getTelefono().equals(usuario.getTelefono()) &&
                                found.getSalarioBase().equals(usuario.getSalarioBase()) &&
                                found.getRol().getIdRol().equals(1) &&
                                found.getRol().getNombre().equals("ADMIN") &&
                                found.getRol().getDescripcion().equals("Administrator")
                )
                .verifyComplete();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository).findById(1);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("nonexistent@example.com"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail("nonexistent@example.com");
        verify(rolRepository, never()).findById(anyInt());
    }

    @Test
    void findByEmail_shouldThrowException_whenUserHasNoRol() {
        // Arrange
        UsuarioEntity usuarioSinRol = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(null) // Sin rol
                .build();

        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(usuarioSinRol));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan.perez@example.com"))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().equals("Usuario encontrado sin rol asignado")
                )
                .verify();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository, never()).findById(anyInt());
    }

    @Test
    void findByEmail_shouldHandleRolNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan.perez@example.com"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository).findById(1);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailIsNull() {
        // Act & Assert
        StepVerifier.create(adapter.findByEmail(null))
                .verifyComplete();

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void findByEmail_shouldHandleRepositoryError() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan.perez@example.com"))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
    }

    // ========== PRUEBAS PARA REGISTRARUSUARIOCOMPLETO ==========

    @Test
    void registrarUsuarioCompleto_shouldRegisterUser_whenRoleExists() {
        // Arrange
        Integer roleId = 2;
        UsuarioEntity savedEntity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(roleId) // roleId assigned
                .build();

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));

        // Act & Assert
        StepVerifier.create(adapter.registrarUsuarioCompleto(usuario, roleId))
                .expectNextMatches(registered ->
                        registered.getEmail().equals(usuario.getEmail()) &&
                                registered.getRol().getIdRol().equals(2)
                )
                .verifyComplete();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void registrarUsuarioCompleto_shouldHandleRepositoryError() {
        // Arrange
        Integer roleId = 2;
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(adapter.registrarUsuarioCompleto(usuario, roleId))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void registrarUsuarioCompleto_shouldAssignRoleAndRegisterUser() {
        // Arrange
        Integer roleId = 2;
        Usuario usuarioSinRolInicial = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(null) // Sin rol inicial
                .build();

        UsuarioEntity savedEntity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(roleId) // roleId assigned
                .build();

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));

        // Act & Assert
        StepVerifier.create(adapter.registrarUsuarioCompleto(usuarioSinRolInicial, roleId))
                .expectNextMatches(registered ->
                        registered.getEmail().equals(usuarioSinRolInicial.getEmail()) &&
                                registered.getRol().getIdRol().equals(2)
                )
                .verifyComplete();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }


    // ========== PRUEBAS DE CASOS EDGE ==========

    @Test
    void save_shouldHandleSpecialCharactersInUserData() {
        // Arrange
        Usuario usuarioEspecial = Usuario.builder()
                .idUsuario(1)
                .nombre("José María")
                .apellido("González-Rodríguez")
                .email("jose.maria@compañía.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .rol(rol)
                .build();

        UsuarioEntity entityEspecial = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("José María")
                .apellido("González-Rodríguez")
                .email("jose.maria@compañía.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal(5000000))
                .idRol(1)
                .build();

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(entityEspecial));

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioEspecial))
                .expectNextMatches(saved ->
                        saved.getNombre().equals("José María") &&
                                saved.getApellido().equals("González-Rodríguez") &&
                                saved.getEmail().equals("jose.maria@compañía.com")
                )
                .verifyComplete();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }
}