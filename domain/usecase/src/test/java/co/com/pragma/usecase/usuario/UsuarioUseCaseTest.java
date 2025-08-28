package co.com.pragma.usecase.usuario;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.usecase.rol.RolUseCase;
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
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolUseCase rolUseCase; // Changed from RolRepository

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private Usuario usuarioConRol;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();

        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrador")
                .build();

        usuarioConRol = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345")
                .salarioBase(BigDecimal.valueOf(5000000))
                .rol(rol)
                .build();
    }

    // ========== PRUEBAS PARA REGISTRAR USUARIO ==========

    @Test
    void registrarUsuario_shouldRegisterUser_whenAllValidationsPass() {
        // Arrange
        when(usuarioRepository.findByEmail(eq("test@example.com"))).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(eq(1))).thenReturn(Mono.empty()); // Changed
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), eq(1)))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextMatches(savedUser ->
                        savedUser.getRol() != null &&
                                savedUser.getRol().getNombre().equals("ADMIN") &&
                                savedUser.getEmail().equals("test@example.com")
                )
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1); // Changed
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    // ========== PRUEBAS DE VALIDACIÓN DE SALARIO ==========

    @Test
    void registrarUsuario_shouldThrowException_whenSalaryIsNull() {
        // Arrange
        usuario.setSalarioBase(null);
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El salario base no puede ser nulo")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldThrowException_whenSalaryIsBelowMinimum() {
        // Arrange
        usuario.setSalarioBase(BigDecimal.valueOf(-100));
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El salario base debe estar entre 0 y 15000000")
                )
                .verify();

        // En un flujo reactivo, todos los métodos se llaman para construir el pipeline
        // pero el error ocurre durante la ejecución en validarSalario()
        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldThrowException_whenSalaryIsAboveMaximum() {
        // Arrange
        usuario.setSalarioBase(BigDecimal.valueOf(20000000));
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El salario base debe estar entre 0 y 15000000")
                )
                .verify();

        // En un flujo reactivo, todos los métodos se llaman para construir el pipeline
        // pero el error ocurre durante la ejecución en validarSalario()
        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldRegisterUser_whenSalaryIsMinimum() {
        // Arrange
        usuario.setSalarioBase(BigDecimal.ZERO);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty()); // Changed
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt()))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextCount(1)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1); // Changed
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldRegisterUser_whenSalaryIsMaximum() {
        // Arrange
        usuario.setSalarioBase(BigDecimal.valueOf(15000000));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty()); // Changed
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt()))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextCount(1)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1); // Changed
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    // ========== PRUEBAS DE VALIDACIÓN DE EMAIL DUPLICADO ==========

    @Test
    void registrarUsuario_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(usuarioRepository.findByEmail(eq("test@example.com")))
                .thenReturn(Mono.just(usuarioConRol));
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Ya existe un usuario registrado con ese email")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldProceed_whenEmailIsUnique() {
        // Arrange
        when(usuarioRepository.findByEmail(eq("test@example.com"))).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(eq(1))).thenReturn(Mono.empty()); // Changed
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), eq(1)))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextCount(1)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1); // Changed
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    // ========== PRUEBAS DE VALIDACIÓN DE ROL ==========

    @Test
    void registrarUsuario_shouldThrowException_whenRoleDoesNotExist() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(eq(999))).thenReturn(Mono.error(new IllegalArgumentException("El rol especificado no existe en el sistema")));
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 999))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El rol especificado no existe en el sistema")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(999);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 999);
    }

    @Test
    void registrarUsuario_shouldProceed_whenRoleExists() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(eq(1))).thenReturn(Mono.empty()); // Changed
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), eq(1)))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectNextCount(1)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1); // Changed
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    // ========== PRUEBAS PARA BUSCAR POR EMAIL ==========

    @Test
    void buscarPorEmail_shouldReturnUser_whenEmailExists() {
        // Arrange
        when(usuarioRepository.findByEmail(eq("test@example.com")))
                .thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail("test@example.com"))
                .expectNextMatches(found ->
                        found.getEmail().equals("test@example.com") &&
                                found.getNombre().equals("Test")
                )
                .verifyComplete();

        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void buscarPorEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // Arrange
        when(usuarioRepository.findByEmail(eq("nonexistent@example.com")))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail("nonexistent@example.com"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void buscarPorEmail_shouldThrowException_whenEmailIsNull() {
        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail(null))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void buscarPorEmail_shouldThrowException_whenEmailIsEmpty() {
        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail(""))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void buscarPorEmail_shouldThrowException_whenEmailIsBlank() {
        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail("   "))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    // ========== PRUEBAS DE VALIDACIONES NULAS ==========

    @Test
    void registrarUsuario_shouldThrowException_whenUsuarioIsNull() {
        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(null, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El usuario no puede ser nulo")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(rolUseCase, never()).validarRolExiste(anyInt());
        verify(usuarioRepository, never()).registrarUsuarioCompleto(any(), anyInt());
    }

    @Test
    void registrarUsuario_shouldThrowException_whenRoleIdIsNull() {
        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, null))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El ID del rol no puede ser nulo")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(rolUseCase, never()).validarRolExiste(anyInt());
        verify(usuarioRepository, never()).registrarUsuarioCompleto(any(), anyInt());
    }

    @Test
    void registrarUsuario_shouldThrowException_whenEmailIsNull() {
        // Arrange
        usuario.setEmail(null);

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email del usuario no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(rolUseCase, never()).validarRolExiste(anyInt());
        verify(usuarioRepository, never()).registrarUsuarioCompleto(any(), anyInt());
    }

    @Test
    void registrarUsuario_shouldThrowException_whenEmailIsEmpty() {
        // Arrange
        usuario.setEmail("");

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email del usuario no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(rolUseCase, never()).validarRolExiste(anyInt());
        verify(usuarioRepository, never()).registrarUsuarioCompleto(any(), anyInt());
    }

    @Test
    void registrarUsuario_shouldThrowException_whenEmailIsBlank() {
        // Arrange
        usuario.setEmail("   ");

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El email del usuario no puede estar vacío")
                )
                .verify();

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(rolUseCase, never()).validarRolExiste(anyInt());
        verify(usuarioRepository, never()).registrarUsuarioCompleto(any(), anyInt());
    }

    // ========== PRUEBAS DE MANEJO DE ERRORES ==========

    @Test
    void registrarUsuario_shouldHandleRepositoryError_whenFindByEmailFails() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));
        // Configurar todos los mocks necesarios para el pipeline reactivo
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuarioConRol));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database connection failed")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void registrarUsuario_shouldHandleRepositoryError_whenRegistrarUsuarioCompletoFails() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database save failed")));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database save failed")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
        verify(rolUseCase).validarRolExiste(1);
        verify(usuarioRepository).registrarUsuarioCompleto(usuario, 1);
    }

    @Test
    void buscarPorEmail_shouldHandleRepositoryError() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database query failed")));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.buscarPorEmail("test@example.com"))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database query failed")
                )
                .verify();

        verify(usuarioRepository).findByEmail("test@example.com");
    }

}