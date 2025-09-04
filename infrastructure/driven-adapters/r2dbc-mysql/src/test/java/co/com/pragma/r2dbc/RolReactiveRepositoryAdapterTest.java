package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.r2dbc.entity.RolEntity;
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
class RolReactiveRepositoryAdapterTest {

    @Mock
    private RolReactiveRepository rolReactiveRepository;

    @InjectMocks
    private RolReactiveRepositoryAdapter adapter;

    private Rol rol;
    private RolEntity rolEntity;

    @BeforeEach
    void setUp() {
        rol = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrator Role")
                .build();

        rolEntity = new RolEntity();
        rolEntity.setIdRol(1);
        rolEntity.setNombre("ADMIN");
        rolEntity.setDescripcion("Administrator Role");
    }

    @Test
    void findById_shouldReturnRol_whenFound() {
        // Arrange
        when(rolReactiveRepository.findById(anyInt())).thenReturn(Mono.just(rolEntity));

        // Act & Assert
        StepVerifier.create(adapter.findById(1))
                .expectNextMatches(result -> result.getNombre().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(rolReactiveRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findById(1))
                .verifyComplete();
    }

    @Test
    void save_shouldReturnSavedRol() {
        // Arrange
        when(rolReactiveRepository.save(any(RolEntity.class))).thenReturn(Mono.just(rolEntity));

        // Act & Assert
        StepVerifier.create(adapter.save(rol))
                .expectNextMatches(result -> result.getIdRol() == 1 && result.getNombre().equals("ADMIN"))
                .verifyComplete();
    }
}
