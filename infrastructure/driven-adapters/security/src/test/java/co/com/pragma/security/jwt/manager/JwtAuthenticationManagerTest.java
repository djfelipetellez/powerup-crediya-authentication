package co.com.pragma.security.jwt.manager;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private LogGateway logGateway;

    private JwtAuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authenticationManager = new JwtAuthenticationManager(jwtProvider, logGateway);
    }

    @Test
    void authenticate_shouldReturnAuthentication_whenValidToken() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of(Map.of("authority", "ROLE_ADMIN")));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username) &&
                            resultAuth.getCredentials() == null &&
                            resultAuth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
                            resultAuth.getAuthorities().size() == 1;
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }

    @Test
    void authenticate_shouldReturnAuthentication_withMultipleRoles() {
        String token = "valid.jwt.token";
        String username = "admin@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of(
                Map.of("authority", "ROLE_ADMIN"),
                Map.of("authority", "ROLE_USER")
        ));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username) &&
                            resultAuth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
                            resultAuth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) &&
                            resultAuth.getAuthorities().size() == 2;
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }

    @Test
    void authenticate_shouldReturnError_whenInvalidToken() {
        String invalidToken = "invalid.jwt.token";
        JwtException jwtException = new JwtException("Invalid token format");

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, invalidToken);

        when(jwtProvider.getClaims(invalidToken)).thenThrow(jwtException);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("bad token")
                )
                .verify();

        verify(jwtProvider).getClaims(invalidToken);
        verify(logGateway).error(eq("jwt-auth-manager"), anyString(), eq(jwtException));
    }

    @Test
    void authenticate_shouldHandleEmptyRoles() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of()); // Empty roles list

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username) &&
                            resultAuth.getAuthorities().isEmpty();
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }

    @Test
    void authenticate_shouldHandleNullRoles() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", null); // Null roles

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username) &&
                            resultAuth.getAuthorities().isEmpty();
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }

    @Test
    void authenticate_shouldHandleSpecialCharactersInUsername() {
        String token = "valid.jwt.token";
        String username = "test+user@example-domain.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of(Map.of("authority", "ROLE_CLIENT")));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username);
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }

    @Test
    void authenticate_shouldHandleRuntimeException() {
        String token = "problematic.jwt.token";
        RuntimeException runtimeException = new RuntimeException("Unexpected error");

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenThrow(runtimeException);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("bad token")
                )
                .verify();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).error(eq("jwt-auth-manager"), anyString(), eq(runtimeException));
    }

    @Test
    void authenticate_shouldReturnAuthenticationWithCorrectPrincipal() {
        String token = "valid.jwt.token";
        String username = "user@test.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of(Map.of("authority", "ROLE_USER")));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken("existingPrincipal", token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    // Should use username from token, not from input authentication
                    return auth.getPrincipal().equals(username) &&
                            !auth.getPrincipal().equals("existingPrincipal");
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
    }

    @Test
    void authenticate_shouldSetCredentialsToNull() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        claims.put("roles", List.of(Map.of("authority", "ROLE_ADMIN")));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> auth.getCredentials() == null)
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
    }

    @Test
    void authenticate_shouldHandleRolesWithMissingAuthority() {
        String token = "valid.jwt.token";
        String username = "test@example.com";

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(username);
        // Role map without "authority" key - this will cause null authority
        claims.put("roles", List.of(Map.of("role", "ROLE_ADMIN")));

        UsernamePasswordAuthenticationToken inputAuth =
                new UsernamePasswordAuthenticationToken(null, token);

        when(jwtProvider.getClaims(token)).thenReturn(claims);

        StepVerifier.create(authenticationManager.authenticate(inputAuth))
                .expectNextMatches(auth -> {
                    UsernamePasswordAuthenticationToken resultAuth =
                            (UsernamePasswordAuthenticationToken) auth;
                    return resultAuth.getPrincipal().equals(username) &&
                            resultAuth.getAuthorities().isEmpty(); // Should filter out null authorities
                })
                .verifyComplete();

        verify(jwtProvider).getClaims(token);
        verify(logGateway).info("jwt-auth-manager", "Token autenticado para usuario: " + username);
    }
}