package co.com.pragma.usecase.rol;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolUseCaseTest {

    @Mock
    private RolRepository rolRepository;

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
        when(rolRepository.save(any(Rol.class))).thenReturn(Mono.just(rol));

        // Act & Assert
        StepVerifier.create(rolUseCase.registrarRol(rol))
                .expectNextMatches(savedRol -> savedRol.getNombre().equals("TEST_ROLE"))
                .verifyComplete();
    }

    @Test
    void validarRolExiste_shouldReturnEmptyMono_whenRoleExists() {
        // Arrange
        when(rolRepository.findById(anyInt())).thenReturn(Mono.just(rol));

        // Act & Assert
        StepVerifier.create(rolUseCase.validarRolExiste(1))
                .verifyComplete();
    }

    @Test
    void validarRolExiste_shouldThrowException_whenRoleDoesNotExist() {
        // Arrange
        when(rolRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(rolUseCase.validarRolExiste(999))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El rol especificado no existe en el sistema")
                )
                .verify();
    }

    @Test
    void registrarRol_shouldThrowException_whenRolIsNull() {
        // Arrange
        when(rolRepository.save(null))
                .thenReturn(Mono.error(new NullPointerException("Rol cannot be null")));

        // Act & Assert
        StepVerifier.create(rolUseCase.registrarRol(null))
                .expectErrorMatches(error -> error instanceof NullPointerException)
                .verify();
    }

    @Test
    void registrarRol_shouldHandleRepositoryError() {
        // Arrange
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

    @Test
    void validarRolExiste_shouldHandleRepositoryError() {
        // Arrange
        when(rolRepository.findById(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // Act & Assert
        StepVerifier.create(rolUseCase.validarRolExiste(1))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database connection failed")
                )
                .verify();
    }
}
