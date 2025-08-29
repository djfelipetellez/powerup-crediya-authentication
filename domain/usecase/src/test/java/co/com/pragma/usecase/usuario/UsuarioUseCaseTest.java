package co.com.pragma.usecase.usuario;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.rol.gateways.RolValidator;
import co.com.pragma.model.usuario.Usuario;
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
    private RolValidator rolValidator;

    @Mock
    private UsuarioValidator usuarioValidator;

    @Mock
    private LoggingGateway loggingGateway;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private Integer roleId;

    @BeforeEach
    void setUp() {
        roleId = 1;
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
        when(rolValidator.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt())).thenReturn(Mono.just(usuario));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId))
                .expectNextMatches(registeredUser -> registeredUser.getEmail().equals("test@pragma.com.co"))
                .verifyComplete();
    }

    @Test
    void registrarUsuario_shouldThrowException_whenUsuarioIsNull() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any(Throwable.class));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(null, roleId))
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
        when(rolValidator.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt())).thenReturn(Mono.empty());


        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId))
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
        when(rolValidator.validarRolExiste(anyInt()))
                .thenReturn(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)));
        // Mock subsequent calls in the chain to prevent NullPointerException during stream assembly
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId))
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
        when(rolValidator.validarRolExiste(anyInt())).thenReturn(Mono.empty());
        when(usuarioRepository.registrarUsuarioCompleto(any(Usuario.class), anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario, roleId))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                        error.getMessage().equals("Database error")
                )
                .verify();
    }
}
