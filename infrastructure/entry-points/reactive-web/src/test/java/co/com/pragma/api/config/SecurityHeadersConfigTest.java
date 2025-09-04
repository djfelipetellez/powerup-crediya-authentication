package co.com.pragma.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityHeadersConfigTest {

    private SecurityHeadersConfig securityHeadersConfig;
    private WebFilterChain webFilterChain;

    @BeforeEach
    void setUp() {
        securityHeadersConfig = new SecurityHeadersConfig();
        webFilterChain = mock(WebFilterChain.class);
        when(webFilterChain.filter(org.mockito.ArgumentMatchers.any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldAddSecurityHeaders() {
        // Given
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));

        // When
        Mono<Void> result = securityHeadersConfig.filter(exchange, webFilterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();

        assertEquals("default-src 'self'; frame-ancestors 'self'; form-action 'self'",
                responseHeaders.getFirst("Content-Security-Policy"));
        assertEquals("max-age=31536000;",
                responseHeaders.getFirst("Strict-Transport-Security"));
        assertEquals("nosniff",
                responseHeaders.getFirst("X-Content-Type-Options"));
        assertEquals("",
                responseHeaders.getFirst("Server"));
        assertEquals("no-store",
                responseHeaders.getFirst("Cache-Control"));
        assertEquals("no-cache",
                responseHeaders.getFirst("Pragma"));
        assertEquals("strict-origin-when-cross-origin",
                responseHeaders.getFirst("Referrer-Policy"));
    }

    @Test
    void shouldNotOverwriteExistingHeaders() {
        // Given
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
        exchange.getResponse().getHeaders().set("Custom-Header", "custom-value");

        // When
        Mono<Void> result = securityHeadersConfig.filter(exchange, webFilterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();

        // Security headers should be added
        assertEquals("default-src 'self'; frame-ancestors 'self'; form-action 'self'",
                responseHeaders.getFirst("Content-Security-Policy"));

        // Existing headers should remain
        assertEquals("custom-value",
                responseHeaders.getFirst("Custom-Header"));
    }

    @Test
    void shouldCallNextFilterInChain() {
        // Given
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
        WebFilterChain mockChain = mock(WebFilterChain.class);
        when(mockChain.filter(exchange)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = securityHeadersConfig.filter(exchange, mockChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the next filter in chain was called
        org.mockito.Mockito.verify(mockChain).filter(exchange);
    }
}