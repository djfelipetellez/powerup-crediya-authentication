package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.r2dbc.entity.UserCredentialEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioCredentialReactiveRepositoryAdapterTest {

    @Mock
    private UserCredentialReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LogGateway logGateway;

    @InjectMocks
    private UsuarioCredentialReactiveRepositoryAdapter adapter;

    private UsuarioCredencial usuarioCredencial;
    private UserCredentialEntity userCredentialEntity;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        usuarioCredencial = UsuarioCredencial.builder()
                .id(1)
                .email("test@example.com")
                .password("plainPassword")
                .idUsuario(100)
                .createdAt(now)
                .lastLoginAt(now.minusHours(1))
                .active(true)
                .build();

        userCredentialEntity = UserCredentialEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("hashedPassword")
                .idUsuario(100)
                .createdAt(now)
                .lastLoginAt(now.minusHours(1))
                .active(true)
                .build();
    }

    // ========== PRUEBAS PARA FINDBEEMAIL ==========

    @Test
    void findByEmail_shouldReturnUsuarioCredencial_whenFound() {
        // Arrange
        when(repository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(userCredentialEntity));
        when(mapper.map(userCredentialEntity, UsuarioCredencial.class))
                .thenReturn(usuarioCredencial);

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .expectNextMatches(result ->
                        result.getEmail().equals("test@example.com") &&
                                result.getIdUsuario().equals(100) &&
                                result.isActive()
                )
                .verifyComplete();

        verify(repository).findByEmail("test@example.com");
        verify(mapper).map(userCredentialEntity, UsuarioCredencial.class);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("nonexistent@example.com"))
                .verifyComplete();

        verify(repository).findByEmail("nonexistent@example.com");
        verify(mapper, never()).map(any(), any());
    }

    @Test
    void findByEmail_shouldHandleRepositoryError() {
        // Arrange
        when(repository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailIsEmpty() {
        // Arrange
        when(repository.findByEmail("")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail(""))
                .verifyComplete();

        verify(repository).findByEmail("");
    }

    // ========== PRUEBAS PARA SAVE ==========

    @Test
    void save_shouldHashPasswordAndSave_whenSuccessful() {
        // Arrange
        String hashedPassword = "hashedPlainPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .id(1)
                .email("test@example.com")
                .password(hashedPassword)
                .idUsuario(100)
                .createdAt(now)
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(usuarioCredencial.toBuilder().password(hashedPassword).build());

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioCredencial))
                .expectNextMatches(result ->
                        result.getEmail().equals("test@example.com") &&
                                result.getIdUsuario().equals(100) &&
                                result.isActive()
                )
                .verifyComplete();

        verify(passwordEncoder).encode("plainPassword");
        verify(repository).save(any(UserCredentialEntity.class));
        verify(logGateway).debug(eq("UsuarioCredentialAdapter"), contains("Guardando credenciales"));
        verify(logGateway).debug(eq("UsuarioCredentialAdapter"), contains("Credenciales guardadas exitosamente"));
    }

    @Test
    void save_shouldHandleEmptyPassword() {
        // Arrange
        UsuarioCredencial credencialPasswordVacio = usuarioCredencial.toBuilder()
                .password("")
                .build();

        String hashedEmptyPassword = "hashedEmptyPassword";
        when(passwordEncoder.encode("")).thenReturn(hashedEmptyPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .email("test@example.com")
                .password(hashedEmptyPassword)
                .idUsuario(100)
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(credencialPasswordVacio.toBuilder().password(hashedEmptyPassword).build());

        // Act & Assert
        StepVerifier.create(adapter.save(credencialPasswordVacio))
                .expectNextMatches(result ->
                        result.getEmail().equals("test@example.com") &&
                                result.getIdUsuario().equals(100)
                )
                .verifyComplete();

        verify(passwordEncoder).encode("");
        verify(repository).save(any(UserCredentialEntity.class));
    }

    @Test
    void save_shouldLogError_whenRepositoryFails() {
        // Arrange
        String hashedPassword = "hashedPlainPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);

        UserCredentialEntity entityToSave = UserCredentialEntity.builder()
                .email("test@example.com")
                .password(hashedPassword)
                .idUsuario(100)
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(entityToSave);

        RuntimeException dbError = new RuntimeException("Database connection error");
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.error(dbError));

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioCredencial))
                .expectError(RuntimeException.class)
                .verify();

        verify(passwordEncoder).encode("plainPassword");
        verify(repository).save(any(UserCredentialEntity.class));
        verify(logGateway).debug(eq("UsuarioCredentialAdapter"), contains("Guardando credenciales"));
        verify(logGateway).error(eq("UsuarioCredentialAdapter"), contains("Error guardando credenciales"), eq(dbError));
    }

    @Test
    void save_shouldPreserveAllFieldsExceptPassword() {
        // Arrange
        String hashedPassword = "hashedPassword123";
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .id(1)
                .email("test@example.com")
                .password(hashedPassword)
                .idUsuario(100)
                .createdAt(now)
                .lastLoginAt(now.minusHours(1))
                .active(true)
                .build();

        UsuarioCredencial expectedResult = UsuarioCredencial.builder()
                .id(1)
                .email("test@example.com")
                .password(hashedPassword)
                .idUsuario(100)
                .createdAt(now)
                .lastLoginAt(now.minusHours(1))
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(expectedResult);

        // Act & Assert
        StepVerifier.create(adapter.save(usuarioCredencial))
                .expectNextMatches(result ->
                        result.getId().equals(1) &&
                                result.getEmail().equals("test@example.com") &&
                                result.getIdUsuario().equals(100) &&
                                result.getCreatedAt().equals(now) &&
                                result.getLastLoginAt().equals(now.minusHours(1)) &&
                                result.isActive()
                )
                .verifyComplete();
    }

    // ========== PRUEBAS PARA UPDATELASTLOGIN ==========

    @Test
    void updateLastLogin_shouldUpdateAndReturnUsuarioCredencial_whenSuccessful() {
        // Arrange
        UserCredentialEntity updatedEntity = userCredentialEntity.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .build();

        UsuarioCredencial updatedCredencial = usuarioCredencial.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .build();

        when(repository.updateLastLoginById(1))
                .thenReturn(Mono.just(updatedEntity));
        when(mapper.map(updatedEntity, UsuarioCredencial.class))
                .thenReturn(updatedCredencial);

        // Act & Assert
        StepVerifier.create(adapter.updateLastLogin(1))
                .expectNextMatches(result ->
                        result.getId().equals(1) &&
                                result.getEmail().equals("test@example.com") &&
                                result.getLastLoginAt() != null
                )
                .verifyComplete();

        verify(repository).updateLastLoginById(1);
        verify(mapper).map(updatedEntity, UsuarioCredencial.class);
    }

    @Test
    void updateLastLogin_shouldReturnEmpty_whenUserNotFound() {
        // Arrange
        when(repository.updateLastLoginById(999))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.updateLastLogin(999))
                .verifyComplete();

        verify(repository).updateLastLoginById(999);
        verify(mapper, never()).map(any(), any());
    }

    @Test
    void updateLastLogin_shouldHandleRepositoryError() {
        // Arrange
        when(repository.updateLastLoginById(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        // Act & Assert
        StepVerifier.create(adapter.updateLastLogin(1))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).updateLastLoginById(1);
    }

    @Test
    void updateLastLogin_shouldHandleZeroId() {
        // Arrange
        when(repository.updateLastLoginById(0)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.updateLastLogin(0))
                .verifyComplete();

        verify(repository).updateLastLoginById(0);
    }

    // ========== PRUEBAS DE CASOS EDGE ==========

    @Test
    void save_shouldHandleLongPassword() {
        // Arrange
        String longPassword = "a".repeat(200); // Password muy largo
        String hashedLongPassword = "hashedLongPassword";

        UsuarioCredencial credencialPasswordLargo = usuarioCredencial.toBuilder()
                .password(longPassword)
                .build();

        when(passwordEncoder.encode(longPassword)).thenReturn(hashedLongPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .email("test@example.com")
                .password(hashedLongPassword)
                .idUsuario(100)
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(credencialPasswordLargo.toBuilder().password(hashedLongPassword).build());

        // Act & Assert
        StepVerifier.create(adapter.save(credencialPasswordLargo))
                .expectNextMatches(result ->
                        result.getEmail().equals("test@example.com") &&
                                result.getIdUsuario().equals(100)
                )
                .verifyComplete();

        verify(passwordEncoder).encode(longPassword);
    }

    @Test
    void save_shouldHandleSpecialCharactersInEmail() {
        // Arrange
        String specialEmail = "test+123@domain-example.com";
        UsuarioCredencial credencialEmailEspecial = usuarioCredencial.toBuilder()
                .email(specialEmail)
                .build();

        String hashedPassword = "hashedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .email(specialEmail)
                .password(hashedPassword)
                .idUsuario(100)
                .active(true)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(credencialEmailEspecial.toBuilder().password(hashedPassword).build());

        // Act & Assert
        StepVerifier.create(adapter.save(credencialEmailEspecial))
                .expectNextMatches(result ->
                        result.getEmail().equals(specialEmail) &&
                                result.getIdUsuario().equals(100)
                )
                .verifyComplete();

        verify(logGateway, times(2)).debug(eq("UsuarioCredentialAdapter"), anyString());
    }

    @Test
    void save_shouldHandleInactiveUser() {
        // Arrange
        UsuarioCredencial credencialInactivo = usuarioCredencial.toBuilder()
                .active(false)
                .build();

        String hashedPassword = "hashedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);

        UserCredentialEntity savedEntity = UserCredentialEntity.builder()
                .email("test@example.com")
                .password(hashedPassword)
                .idUsuario(100)
                .active(false)
                .build();

        when(mapper.map(any(UsuarioCredencial.class), eq(UserCredentialEntity.class)))
                .thenReturn(savedEntity);
        when(repository.save(any(UserCredentialEntity.class)))
                .thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, UsuarioCredencial.class))
                .thenReturn(credencialInactivo.toBuilder().password(hashedPassword).build());

        // Act & Assert
        StepVerifier.create(adapter.save(credencialInactivo))
                .expectNextMatches(result ->
                        result.getEmail().equals("test@example.com") &&
                                !result.isActive()
                )
                .verifyComplete();
    }
}