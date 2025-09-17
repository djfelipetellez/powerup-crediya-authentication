package co.com.pragma.security.jwt.manager;

import co.com.pragma.model.common.gateways.LogGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityContextRepositoryTest {

    @Mock
    private JwtAuthenticationManager jwtAuthenticationManager;

    @Mock
    private LogGateway logGateway;

    @Mock
    private ServerWebExchange exchange;

    private SecurityContextRepository securityContextRepository;

    @BeforeEach
    void setUp() {
        securityContextRepository = new SecurityContextRepository(jwtAuthenticationManager, logGateway);
    }

    @Test
    void save_shouldReturnEmpty() {
        SecurityContext context = new SecurityContextImpl();

        StepVerifier.create(securityContextRepository.save(exchange, context))
                .verifyComplete();

        verifyNoInteractions(jwtAuthenticationManager);
        verifyNoInteractions(logGateway);
    }

    @Test
    void load_shouldReturnSecurityContext_whenTokenExists() {
        String token = "valid.jwt.token";
        String username = "test@example.com";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, authorities);

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(securityContext -> {
                    Authentication auth = securityContext.getAuthentication();
                    return auth.getName().equals(username) &&
                            auth.getAuthorities().equals(authorities);
                })
                .verifyComplete();

        verify(exchange).getAttribute("token");
        verify(jwtAuthenticationManager).authenticate(any(Authentication.class));
        verify(logGateway).info("security-context", "Cargando contexto de seguridad para token");
        verify(logGateway).info(eq("security-context"), contains("Contexto de seguridad creado para: " + username));
    }

    @Test
    void load_shouldReturnEmpty_whenNoToken() {
        when(exchange.getAttribute("token")).thenReturn(null);

        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verify(exchange).getAttribute("token");
        verify(jwtAuthenticationManager, never()).authenticate(any());
        verify(logGateway).debug("security-context", "No hay token en el exchange");
    }

    @Test
    void load_shouldReturnEmpty_whenTokenIsEmptyString() {
        when(exchange.getAttribute("token")).thenReturn("");

        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verify(exchange).getAttribute("token");
        verify(jwtAuthenticationManager, never()).authenticate(any());
        verify(logGateway).debug("security-context", "No hay token en el exchange");
    }

    @Test
    void load_shouldPropagateError_whenAuthenticationFails() {
        String token = "invalid.jwt.token";
        RuntimeException authException = new RuntimeException("bad token");

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.error(authException));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectError(RuntimeException.class)
                .verify();

        verify(exchange).getAttribute("token");
        verify(jwtAuthenticationManager).authenticate(any(Authentication.class));
        verify(logGateway).info("security-context", "Cargando contexto de seguridad para token");
    }

    @Test
    void load_shouldCreateCorrectAuthenticationToken() {
        String token = "test.jwt.token";
        String username = "user@example.com";
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, authorities);

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(argThat(auth -> {
            // Verify that the authentication object is created correctly
            return auth instanceof UsernamePasswordAuthenticationToken &&
                    auth.getPrincipal().equals(token) &&
                    auth.getCredentials().equals(token);
        }))).thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(securityContext -> {
                    Authentication auth = securityContext.getAuthentication();
                    return auth.getName().equals(username) &&
                            auth.getAuthorities().size() == 2 &&
                            auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) &&
                            auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"));
                })
                .verifyComplete();

        verify(jwtAuthenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void load_shouldHandleSpecialCharactersInToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test+token=";
        String username = "test+user@example.com";

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(securityContext -> {
                    Authentication auth = securityContext.getAuthentication();
                    return auth.getName().equals(username);
                })
                .verifyComplete();

        verify(exchange).getAttribute("token");
        verify(jwtAuthenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void load_shouldLogCorrectMessages() {
        String token = "valid.jwt.token";
        String username = "test@example.com";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, authorities);

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextCount(1)
                .verifyComplete();

        verify(logGateway).info("security-context", "Cargando contexto de seguridad para token");
        verify(logGateway).info("security-context",
                "Contexto de seguridad creado para: " + username + " con roles: " + authorities);
    }

    @Test
    void load_shouldHandleEmptyAuthorities() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, List.of()); // Empty authorities

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(securityContext -> {
                    Authentication auth = securityContext.getAuthentication();
                    return auth.getName().equals(username) &&
                            auth.getAuthorities().isEmpty();
                })
                .verifyComplete();

        verify(jwtAuthenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void load_shouldReturnEmptyForNullTokenAttribute() {
        // Explicitly test when getAttribute returns null
        when(exchange.getAttribute("token")).thenReturn(null);

        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verify(exchange).getAttribute("token");
        verifyNoInteractions(jwtAuthenticationManager);
        verify(logGateway).debug("security-context", "No hay token en el exchange");
    }

    @Test
    void load_shouldReturnSecurityContextImpl() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(exchange.getAttribute("token")).thenReturn(token);
        when(jwtAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.just(authenticatedAuth));

        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(SecurityContextImpl.class::isInstance)
                .verifyComplete();
    }
}