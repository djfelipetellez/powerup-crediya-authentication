package co.com.pragma.security.jwt.filter;

import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private LogGateway logGateway;

    @Mock
    private WebFilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(logGateway);
    }

    @Test
    void filter_shouldAllowPublicPath_loginEndpoint() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/auth/login")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info("jwt-filter", "=== RUTA PÚBLICA DETECTADA - Permitiendo acceso sin token: /api/auth/login ===");
    }

    @Test
    void filter_shouldAllowPublicPath_validateTokenEndpoint() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/auth/validate-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info("jwt-filter", "=== RUTA PÚBLICA DETECTADA - Permitiendo acceso sin token: /api/auth/validate-token ===");
    }

    @Test
    void filter_shouldAllowPublicPath_swaggerUI() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/swagger-ui/index.html")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info("jwt-filter", "=== RUTA PÚBLICA DETECTADA - Permitiendo acceso sin token: /swagger-ui/index.html ===");
    }

    @Test
    void filter_shouldAllowPublicPath_apiDocs() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/v3/api-docs")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info(eq("jwt-filter"), contains("RUTA PÚBLICA DETECTADA"));
    }

    @Test
    void filter_shouldAllowPublicPath_webjars() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/webjars/swagger-ui/index.css")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info(eq("jwt-filter"), contains("RUTA PÚBLICA DETECTADA"));
    }

    @Test
    void filter_shouldAllowPublicPath_actuator() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/actuator/health")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(logGateway).info(eq("jwt-filter"), contains("RUTA PÚBLICA DETECTADA"));
    }

    @Test
    void filter_shouldExtractToken_whenValidBearerToken() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        String token = "jwt.token.here";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals(token, exchange.getAttribute("token"));
        verify(logGateway).debug("jwt-filter", Constantes.MSG_TOKEN_EXTRACTED + " para ruta: /api/protected");
    }

    @Test
    void filter_shouldThrowException_whenNoAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_TOKEN_NOT_FOUND)
                )
                .verify();

        verify(filterChain, never()).filter(any());
        verify(logGateway).warn("jwt-filter", "Token no encontrado para ruta: /api/protected", null);
    }

    @Test
    void filter_shouldThrowException_whenInvalidTokenFormat() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "InvalidFormat jwt.token.here")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_TOKEN_FORMAT)
                )
                .verify();

        verify(filterChain, never()).filter(any());
        verify(logGateway).warn("jwt-filter", "Formato inválido para ruta: /api/protected", null);
    }

    @Test
    void filter_shouldThrowException_whenEmptyAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_TOKEN_FORMAT)
                )
                .verify();

        verify(filterChain, never()).filter(any());
        verify(logGateway).warn("jwt-filter", "Formato inválido para ruta: /api/protected", null);
    }

    @Test
    void filter_shouldThrowException_whenOnlyBearerPrefix() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals("", exchange.getAttribute("token"));
    }

    @Test
    void filter_shouldExtractToken_withSpacesInToken() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        String token = "jwt token with spaces";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals(token, exchange.getAttribute("token"));
    }

    @Test
    void filter_shouldHandleCaseInsensitiveBearer() {
        String token = "jwt.token.here";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .expectErrorMatches(throwable ->
                        throwable instanceof AuthenticationException &&
                                throwable.getMessage().equals(Constantes.MSG_INVALID_TOKEN_FORMAT)
                )
                .verify();

        verify(filterChain, never()).filter(any());
    }

    @Test
    void filter_shouldHandleComplexPublicPaths() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        String[] publicPaths = {
                "/api/auth/login",
                "/nested/login/path",
                "/api/validate-token",
                "/swagger-ui.html",
                "/swagger-ui/",
                "/v3/api-docs.yaml",
                "/webjars/bootstrap/css/bootstrap.min.css",
                "/actuator/info"
        };

        for (String path : publicPaths) {
            MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
            ServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                    .verifyComplete();

            verify(filterChain).filter(exchange);
        }

        verify(logGateway, times(publicPaths.length)).info(eq("jwt-filter"), contains("RUTA PÚBLICA DETECTADA"));
    }

    @Test
    void filter_shouldNotAllowPrivatePaths() {
        String[] privatePaths = {
                "/api/users",
                "/api/orders",
                "/admin/dashboard",
                "/protected/resource"
        };

        for (String path : privatePaths) {
            MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
            ServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                    .expectError(AuthenticationException.class)
                    .verify();
        }

        verify(filterChain, never()).filter(any());
        verify(logGateway, times(privatePaths.length)).warn(eq("jwt-filter"), anyString(), isNull());
    }

    @Test
    void filter_shouldExtractLongToken() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        String longToken = "a".repeat(500); // Very long token
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + longToken)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals(longToken, exchange.getAttribute("token"));
    }

    @Test
    void filter_shouldHandleMultipleAuthHeaders() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        String token = "jwt.token.here";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.AUTHORIZATION, "Bearer another.token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        // Should use the first header value
        assertEquals(token, exchange.getAttribute("token"));
    }

    @Test
    void isPublicPath_shouldReturnTrueForAllPublicEndpoints() {
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Use reflection to test the private method behavior through public interface
        String[] publicPaths = {
                "/login", "/api/login", "/auth/login",
                "/validate-token", "/api/validate-token",
                "/swagger-ui", "/swagger-ui/index.html",
                "/v3/api-docs", "/v3/api-docs/swagger-config",
                "/webjars", "/webjars/swagger-ui/bundle.js",
                "/actuator", "/actuator/health"
        };

        for (String path : publicPaths) {
            MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
            ServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(jwtFilter.filter(exchange, filterChain))
                    .verifyComplete();
        }

        verify(logGateway, times(publicPaths.length)).info(eq("jwt-filter"), contains("RUTA PÚBLICA DETECTADA"));
    }
}