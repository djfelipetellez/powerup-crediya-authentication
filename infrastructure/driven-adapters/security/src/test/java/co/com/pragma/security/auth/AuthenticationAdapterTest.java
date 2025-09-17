package co.com.pragma.security.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationAdapterTest {

    @Mock
    private UsuarioCredencialRepository usuarioCredencialRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private LogGateway logGateway;

    @InjectMocks
    private AuthenticationAdapter authenticationAdapter;

    private LoginCredenciales loginCredenciales;
    private UsuarioCredencial usuarioCredencial;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        loginCredenciales = new LoginCredenciales("test@example.com", "password123");

        Rol rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setDocumentoIdentidad("12345678");
        usuario.setRol(rol);

        usuarioCredencial = UsuarioCredencial.builder()
                .id(1)
                .email("test@example.com")
                .password("hashedPassword123")
                .idUsuario(1)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void authenticateLogIn_shouldReturnToken_whenValidCredentials() {
        String expectedToken = "jwt.token.here";

        when(usuarioCredencialRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(usuarioCredencial));
        when(passwordEncoder.matches("password123", "hashedPassword123"))
                .thenReturn(true);
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuario));
        when(jwtProvider.generateToken("test@example.com", 1, "ADMIN", "12345678"))
                .thenReturn(expectedToken);

        StepVerifier.create(authenticationAdapter.authenticateLogIn(loginCredenciales))
                .expectNextMatches(token -> token.token().equals(expectedToken))
                .verifyComplete();

        verify(usuarioCredencialRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword123");
        verify(usuarioRepository).findById(1);
        verify(jwtProvider).generateToken("test@example.com", 1, "ADMIN", "12345678");
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_START + "test@example.com");
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_SUCCESS + "test@example.com");
    }

    @Test
    void authenticateLogIn_shouldThrowException_whenCredentialNotFound() {
        when(usuarioCredencialRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authenticationAdapter.authenticateLogIn(loginCredenciales))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_CREDENTIALS)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_START + "test@example.com");
        verify(logGateway).error(eq("AuthenticationAdapter"), anyString(), any(AuthenticationException.class));
    }

    @Test
    void authenticateLogIn_shouldThrowException_whenPasswordIncorrect() {
        when(usuarioCredencialRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(usuarioCredencial));
        when(passwordEncoder.matches("password123", "hashedPassword123"))
                .thenReturn(false);

        StepVerifier.create(authenticationAdapter.authenticateLogIn(loginCredenciales))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_CREDENTIALS)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword123");
        verify(usuarioRepository, never()).findById(any());
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_START + "test@example.com");
        verify(logGateway).error(eq("AuthenticationAdapter"), anyString(), any(AuthenticationException.class));
    }

    @Test
    void authenticateLogIn_shouldThrowException_whenUserInactive() {
        usuarioCredencial = usuarioCredencial.toBuilder().active(false).build();

        when(usuarioCredencialRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(usuarioCredencial));

        StepVerifier.create(authenticationAdapter.authenticateLogIn(loginCredenciales))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_USER_INACTIVE)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_START + "test@example.com");
        verify(logGateway).error(eq("AuthenticationAdapter"), anyString(), any(AuthenticationException.class));
    }

    @Test
    void authenticateLogIn_shouldThrowException_whenUsuarioNotFound() {
        when(usuarioCredencialRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(usuarioCredencial));
        when(passwordEncoder.matches("password123", "hashedPassword123"))
                .thenReturn(true);
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.empty());

        StepVerifier.create(authenticationAdapter.authenticateLogIn(loginCredenciales))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_CREDENTIALS)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword123");
        verify(usuarioRepository).findById(1);
        verify(logGateway).info("AuthenticationAdapter", Constantes.LOG_AUTH_START + "test@example.com");
        verify(logGateway).error(eq("AuthenticationAdapter"), anyString(), any(AuthenticationException.class));
    }

    @Test
    void validateToken_shouldReturnValidResult_whenTokenValid() {
        String token = "valid.jwt.token";
        Claims claims = new DefaultClaims();
        claims.setSubject("test@example.com");
        claims.setExpiration(new Date(System.currentTimeMillis() + 3600000));
        claims.put("userId", 1);
        claims.put("documentoIdentidad", "12345678");
        claims.put("roles", List.of(Map.of("authority", "ROLE_ADMIN")));

        when(jwtProvider.isTokenExpired(token)).thenReturn(false);
        when(jwtProvider.getClaims(token)).thenReturn(claims);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn(1);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("test@example.com");
        when(jwtProvider.getDocumentoIdentidadFromToken(token)).thenReturn("12345678");

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        result.valid() &&
                                result.userId().equals(1) &&
                                result.email().equals("test@example.com") &&
                                result.role().equals("ADMIN") &&
                                result.documentoIdentidad().equals("12345678") &&
                                result.error() == null
                )
                .verifyComplete();

        verify(jwtProvider).isTokenExpired(token);
        verify(jwtProvider).getClaims(token);
        verify(jwtProvider).getUserIdFromToken(token);
        verify(jwtProvider).getUsernameFromToken(token);
        verify(jwtProvider).getDocumentoIdentidadFromToken(token);
        verify(logGateway).info("AuthenticationAdapter", "Token válido para usuario: test@example.com, rol: ADMIN");
    }

    @Test
    void validateToken_shouldReturnInvalidResult_whenTokenExpired() {
        String token = "expired.jwt.token";

        when(jwtProvider.isTokenExpired(token)).thenReturn(true);

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        !result.valid() &&
                                result.error().equals("Token expired")
                )
                .verifyComplete();

        verify(jwtProvider).isTokenExpired(token);
        verify(jwtProvider, never()).getClaims(any());
        verify(logGateway).info("AuthenticationAdapter", "Token expirado");
    }

    @Test
    void validateToken_shouldReturnInvalidResult_whenTokenInvalid() {
        String token = "invalid.jwt.token";

        when(jwtProvider.isTokenExpired(token)).thenThrow(new RuntimeException("Invalid token format"));

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        !result.valid() &&
                                result.error().equals("Invalid token")
                )
                .verifyComplete();

        verify(jwtProvider).isTokenExpired(token);
        verify(logGateway).error(eq("AuthenticationAdapter"), anyString(), any(RuntimeException.class));
    }

    @Test
    void validateToken_shouldHandleEmptyRoles() {
        String token = "valid.jwt.token";
        Claims claims = new DefaultClaims();
        claims.setSubject("test@example.com");
        claims.setExpiration(new Date(System.currentTimeMillis() + 3600000));
        claims.put("userId", 1);
        claims.put("documentoIdentidad", "12345678");
        claims.put("roles", List.of()); // Empty roles

        when(jwtProvider.isTokenExpired(token)).thenReturn(false);
        when(jwtProvider.getClaims(token)).thenReturn(claims);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn(1);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("test@example.com");
        when(jwtProvider.getDocumentoIdentidadFromToken(token)).thenReturn("12345678");

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        result.valid() &&
                                result.role().equals("UNKNOWN")
                )
                .verifyComplete();
    }

    @Test
    void validateToken_shouldHandleNullRoles() {
        String token = "valid.jwt.token";
        Claims claims = new DefaultClaims();
        claims.setSubject("test@example.com");
        claims.setExpiration(new Date(System.currentTimeMillis() + 3600000));
        claims.put("userId", 1);
        claims.put("documentoIdentidad", "12345678");
        claims.put("roles", null); // Null roles

        when(jwtProvider.isTokenExpired(token)).thenReturn(false);
        when(jwtProvider.getClaims(token)).thenReturn(claims);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn(1);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("test@example.com");
        when(jwtProvider.getDocumentoIdentidadFromToken(token)).thenReturn("12345678");

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        result.valid() &&
                                result.role().equals("UNKNOWN")
                )
                .verifyComplete();
    }

    @Test
    void validateToken_shouldStripRolePrefix() {
        String token = "valid.jwt.token";
        Claims claims = new DefaultClaims();
        claims.setSubject("cliente@example.com");
        claims.setExpiration(new Date(System.currentTimeMillis() + 3600000));
        claims.put("userId", 2);
        claims.put("documentoIdentidad", "87654321");
        claims.put("roles", List.of(Map.of("authority", "ROLE_CLIENTE")));

        when(jwtProvider.isTokenExpired(token)).thenReturn(false);
        when(jwtProvider.getClaims(token)).thenReturn(claims);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn(2);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("cliente@example.com");
        when(jwtProvider.getDocumentoIdentidadFromToken(token)).thenReturn("87654321");

        StepVerifier.create(authenticationAdapter.validateToken(token))
                .expectNextMatches(result ->
                        result.valid() &&
                                result.role().equals("CLIENTE")
                )
                .verifyComplete();
    }
}