package co.com.pragma.usecase.rol;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolUseCaseTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private LogGateway loggingGateway;

    @InjectMocks
    private RolUseCase rolUseCase;

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = Rol.builder()
                .idRol(1)
                .nombre("TEST_ROLE")
                .descripcion("A role for testing")
                .build();
    }

    @Test
    void registrarRolExitoso() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        when(rolRepository.save(any(Rol.class))).thenReturn(Mono.just(rol));

        // Act & Assert
        StepVerifier.create(rolUseCase.registrarRol(rol))
                .expectNextMatches(savedRol -> savedRol.getNombre().equals("TEST_ROLE"))
                .verifyComplete();
    }

    @Test
    void registrarRol_shouldThrowException_whenRolIsNull() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any());
        when(rolRepository.save(null))
                .thenReturn(Mono.error(new NullPointerException("Rol cannot be null")));

        // Act & Assert
        StepVerifier.create(rolUseCase.registrarRol(null))
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void registrarRol_shouldHandleRepositoryError() {
        // Arrange
        doNothing().when(loggingGateway).info(any(), any());
        doNothing().when(loggingGateway).error(any(), any(), any());
        when(rolRepository.save(any(Rol.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(rolUseCase.registrarRol(rol))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();
    }

}
