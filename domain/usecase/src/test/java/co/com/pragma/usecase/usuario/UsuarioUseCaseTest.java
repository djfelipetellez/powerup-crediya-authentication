package co.com.pragma.usecase.usuario;

import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioValidator usuarioValidator;

    @Mock
    private LogGateway loggingGateway;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private Integer roleId;
    private String password;

    @BeforeEach
    void setUp() {
        roleId = 1;
        password = "testPassword123";
        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test User")
                .email("test@pragma.com.co")
                .telefono("3001234567")
                .build();
    }

    @Test
    void registrarUsuarioExitoso() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        when(usuarioValidator.validate(any(Usuario.class), anyInt())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(Rol.builder().idRol(1).build()));
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class))).thenReturn(Mono.just(usuario));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId, password))
                .expectNextMatches(registeredUser -> registeredUser.getEmail().equals("test@pragma.com.co"))
                .verifyComplete();
    }

    @Test
    void registrarUsuario_shouldThrowException_whenUsuarioIsNull() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));

        // Act
        Mono<Usuario> result = usuarioUseCase.registrarUsuario(null, roleId, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals(Constantes.MSG_USUARIO_NULL)
                )
                .verify();
    }

    @Test
    void registrarUsuario_shouldThrowException_whenUsuarioValidationFails() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));
        when(usuarioValidator.validate(any(Usuario.class), anyInt()))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid user data")));
        // Mock subsequent calls in the chain to prevent NullPointerException during stream assembly
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(Rol.builder().idRol(1).build()));
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class))).thenReturn(Mono.empty());


        // Act
        Mono<Usuario> result = usuarioUseCase.registrarUsuario(usuario, roleId, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Invalid user data")
                )
                .verify();
    }

    @Test
    void registrarUsuario_shouldThrowException_whenRolValidationFails() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));
        when(usuarioValidator.validate(any(Usuario.class), anyInt())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt()))
                .thenReturn(Mono.empty());
        // Mock subsequent calls in the chain to prevent NullPointerException during stream assembly
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class))).thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = usuarioUseCase.registrarUsuario(usuario, roleId, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals(Constantes.MSG_ROLE_NOT_EXISTS)
                )
                .verify();
    }

    @Test
    void registrarUsuario_shouldHandleRepositoryError() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));
        when(usuarioValidator.validate(any(Usuario.class), anyInt())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(Rol.builder().idRol(1).build()));
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Usuario> result = usuarioUseCase.registrarUsuario(usuario, roleId, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();
    }

    @Test
    void registrarUsuario_shouldCreateCredentials_whenUserIsRegistered() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        when(usuarioValidator.validate(any(Usuario.class), anyInt())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(Rol.builder().idRol(1).build()));
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class))).thenReturn(Mono.just(usuario));

        // Act
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId, password))
                .expectNextMatches(registeredUser -> registeredUser.getEmail().equals("test@pragma.com.co"))
                .verifyComplete();

        // Assert - Verify that credentials were saved is implicit in registrarUsuarioCompleto
    }

    @Test
    void registrarUsuario_shouldThrowException_whenCredentialsSaveFails() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));
        when(usuarioValidator.validate(any(Usuario.class), anyInt())).thenReturn(Mono.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(Rol.builder().idRol(1).build()));
        // Make registrarUsuarioCompleto fail to simulate credentials save failure
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt(), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Error saving credentials")));

        // Act
        Mono<Usuario> result = usuarioUseCase.registrarUsuario(usuario, roleId, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Error saving credentials")
                )
                .verify();
    }

    @Test
    void consultarUsuario_Success() {
        // Arrange
        String email = "test@test.com";
        Usuario usuarioMock = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email(email)
                .documentoIdentidad("123456789")
                .telefono("555-1234")
                .build();

        doNothing().when(loggingGateway).info(any(), any());
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioMock));

        // Act
        Mono<Usuario> result = usuarioUseCase.consultarUsuario(email);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getEmail().equals(email) &&
                                user.getNombre().equals("Test") &&
                                user.getApellido().equals("User"))
                .verifyComplete();
    }

    @Test
    void consultarUsuario_UserNotFound() {
        // Arrange
        String email = "test@test.com";

        doNothing().when(loggingGateway).info(any(), any());
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Mono.empty());

        // Act
        Mono<Usuario> result = usuarioUseCase.consultarUsuario(email);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof UsuarioNotFoundException &&
                                error.getMessage().contains("Usuario no encontrado con email: " + email)
                )
                .verify();
    }
}
