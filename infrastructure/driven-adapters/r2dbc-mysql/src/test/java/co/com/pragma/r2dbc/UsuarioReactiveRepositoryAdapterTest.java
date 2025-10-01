package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
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
    private RolRepository rolRepository;

    @Mock
    private UsuarioCredencialRepository usuarioCredencialRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private LogGateway logGateway;

    private UsuarioReactiveRepositoryAdapter adapter;

    private Usuario usuario;
    private UsuarioEntity usuarioEntity;
    private Rol rol;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioReactiveRepositoryAdapter(usuarioRepository, rolRepository, usuarioCredencialRepository, mapper, logGateway);

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
    }

    // ========== PRUEBAS PARA SAVE ==========

    @Test
    void save_shouldReturnSavedUsuario_whenSuccessful() {
        // Arrange
        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(usuarioEntity));

        // Act
        Mono<Usuario> result = adapter.save(usuario);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getIdUsuario().equals(usuario.getIdUsuario()) &&
                        saved.getNombre().equals(usuario.getNombre()) &&
                        saved.getApellido().equals(usuario.getApellido()) &&
                        saved.getEmail().equals(usuario.getEmail()) &&
                        saved.getDocumentoIdentidad().equals(usuario.getDocumentoIdentidad()) &&
                        saved.getTelefono().equals(usuario.getTelefono()) &&
                        saved.getSalarioBase().equals(usuario.getSalarioBase()))
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

        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(savedEntity);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));

        // Act
        Mono<Usuario> result = adapter.save(nuevoUsuario);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getIdUsuario().equals(5) &&
                        saved.getEmail().equals(nuevoUsuario.getEmail()))
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

        // Act
        Mono<Usuario> result = adapter.save(usuarioSinRol);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("El usuario debe tener un rol asignado"))
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

        // Act
        Mono<Usuario> result = adapter.save(usuarioConRolSinId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("El usuario debe tener un rol asignado"))
                .verify();

        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    void save_shouldHandleRepositoryError() {
        // Arrange
        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Usuario> result = adapter.save(usuario);

        // Assert
        StepVerifier.create(result)
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
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));

        // Act
        Mono<Usuario> result = adapter.findByEmail("juan.perez@example.com");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(found -> found.getIdUsuario().equals(usuario.getIdUsuario()) &&
                        found.getNombre().equals(usuario.getNombre()) &&
                        found.getApellido().equals(usuario.getApellido()) &&
                        found.getEmail().equals(usuario.getEmail()) &&
                        found.getDocumentoIdentidad().equals(usuario.getDocumentoIdentidad()) &&
                        found.getTelefono().equals(usuario.getTelefono()) &&
                        found.getSalarioBase().equals(usuario.getSalarioBase()) &&
                        found.getRol().getIdRol().equals(1) &&
                        found.getRol().getNombre().equals("ADMIN") &&
                        found.getRol().getDescripcion().equals("Administrator"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository).findById(1);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = adapter.findByEmail("nonexistent@example.com");

        // Assert
        StepVerifier.create(result)
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

        // Act
        Mono<Usuario> result = adapter.findByEmail("juan.perez@example.com");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("Usuario encontrado sin rol asignado"))
                .verify();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository, never()).findById(anyInt());
    }

    @Test
    void findByEmail_shouldThrowException_whenRolNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = adapter.findByEmail("juan.perez@example.com");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("Usuario encontrado sin rol asignado"))
                .verify();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
        verify(rolRepository).findById(1);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailIsNull() {

        // Act
        Mono<Usuario> result = adapter.findByEmail(null);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void findByEmail_shouldHandleRepositoryError() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Usuario> result = adapter.findByEmail("juan.perez@example.com");

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).findByEmail("juan.perez@example.com");
    }

    // ========== PRUEBAS PARA REGISTRARUSUARIOCOMPLETO ==========

    @Test
    void registrarUsuarioCompleto_shouldRegisterUser_whenRoleExists() {
        // Arrange
        Integer roleId = 2;
        Rol userRol = Rol.builder()
                .idRol(roleId)
                .nombre("USER")
                .descripcion("User role")
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

        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(savedEntity);
        when(rolRepository.findById(roleId)).thenReturn(Mono.just(userRol));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));
        when(usuarioRepository.findById(1)).thenReturn(Mono.just(savedEntity));
        when(usuarioCredencialRepository.save(any(UsuarioCredencial.class))).thenReturn(Mono.just(UsuarioCredencial.builder().build()));

        // Act
        Mono<Usuario> result = adapter.registrarUsuarioCompleto(usuario, roleId, "testPassword");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(registered -> registered.getEmail().equals(usuario.getEmail()))
                .verifyComplete();

        verify(rolRepository, times(2)).findById(roleId);
        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void registrarUsuarioCompleto_shouldThrowError_whenRoleNotFound() {
        // Arrange
        Integer roleId = 999;
        when(rolRepository.findById(roleId)).thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = adapter.registrarUsuarioCompleto(usuario, roleId, "testPassword");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Rol no encontrado con ID: " + roleId))
                .verify();

        verify(rolRepository).findById(roleId);
        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    void registrarUsuarioCompleto_shouldHandleRepositoryError() {
        // Arrange
        Integer roleId = 2;
        Rol userRolError = Rol.builder()
                .idRol(roleId)
                .nombre("USER")
                .descripcion("User role")
                .build();

        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(rolRepository.findById(roleId)).thenReturn(Mono.just(userRolError));
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Usuario> result = adapter.registrarUsuarioCompleto(usuario, roleId, "testPassword");

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(rolRepository).findById(roleId);
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

        Rol userRolAssign = Rol.builder()
                .idRol(roleId)
                .nombre("USER")
                .descripcion("User role")
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

        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(savedEntity);
        when(rolRepository.findById(roleId)).thenReturn(Mono.just(userRolAssign));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));
        when(usuarioRepository.findById(1)).thenReturn(Mono.just(savedEntity));
        when(usuarioCredencialRepository.save(any(UsuarioCredencial.class))).thenReturn(Mono.just(UsuarioCredencial.builder().build()));

        // Act
        Mono<Usuario> result = adapter.registrarUsuarioCompleto(usuarioSinRolInicial, roleId, "testPassword");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(registered -> registered.getEmail().equals(usuarioSinRolInicial.getEmail()))
                .verifyComplete();

        verify(rolRepository, times(2)).findById(roleId);
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

        when(mapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(entityEspecial);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(entityEspecial));

        // Act
        Mono<Usuario> result = adapter.save(usuarioEspecial);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getNombre().equals("José María") &&
                        saved.getApellido().equals("González-Rodríguez") &&
                        saved.getEmail().equals("jose.maria@compañía.com"))
                .verifyComplete();

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    // ========== PRUEBAS PARA FINDBYID ==========

    @Test
    void findById_shouldReturnUsuario_whenFoundWithRol() {
        // Arrange
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));

        // Act
        Mono<Usuario> result = adapter.findById(1);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(found -> found.getIdUsuario().equals(usuario.getIdUsuario()) &&
                        found.getNombre().equals(usuario.getNombre()) &&
                        found.getApellido().equals(usuario.getApellido()) &&
                        found.getEmail().equals(usuario.getEmail()) &&
                        found.getDocumentoIdentidad().equals(usuario.getDocumentoIdentidad()) &&
                        found.getTelefono().equals(usuario.getTelefono()) &&
                        found.getSalarioBase().equals(usuario.getSalarioBase()) &&
                        found.getRol().getIdRol().equals(1) &&
                        found.getRol().getNombre().equals("ADMIN") &&
                        found.getRol().getDescripcion().equals("Administrator"))
                .verifyComplete();

        verify(usuarioRepository).findById(1);
        verify(rolRepository).findById(1);
    }

    @Test
    void findById_shouldReturnEmpty_whenUserNotFound() {
        // Arrange
        when(usuarioRepository.findById(anyInt()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = adapter.findById(999);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(usuarioRepository).findById(999);
        verify(rolRepository, never()).findById(anyInt());
    }

    @Test
    void findById_shouldThrowException_whenUserHasNoRol() {
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

        when(usuarioRepository.findById(anyInt()))
                .thenReturn(Mono.just(usuarioSinRol));

        // Act
        Mono<Usuario> result = adapter.findById(1);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("Usuario encontrado sin rol asignado"))
                .verify();

        verify(usuarioRepository).findById(1);
        verify(rolRepository, never()).findById(anyInt());
    }

    @Test
    void findById_shouldThrowException_whenRolNotFound() {
        // Arrange
        when(usuarioRepository.findById(anyInt()))
                .thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = adapter.findById(1);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("Usuario encontrado sin rol asignado"))
                .verify();

        verify(usuarioRepository).findById(1);
        verify(rolRepository).findById(1);
    }

    @Test
    void findById_shouldHandleRepositoryError() {
        // Arrange
        when(usuarioRepository.findById(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Usuario> result = adapter.findById(1);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).findById(1);
    }

}