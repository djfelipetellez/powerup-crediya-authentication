package co.com.pragma.security.jwt.filter;

import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

    private final LogGateway logGateway;

    public JwtFilter(LogGateway logGateway) {
        this.logGateway = logGateway;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Permitir acceso sin token a rutas públicas
        if (isPublicPath(path)) {
            logGateway.debug("jwt-filter", Constantes.MSG_ACCESS_PUBLIC_ROUTE + ": " + path);
            return chain.filter(exchange);
        }

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            logGateway.warn("jwt-filter", "Token no encontrado para ruta: " + path, null);
            return Mono.error(new AuthenticationException(Constantes.MSG_TOKEN_NOT_FOUND));
        }

        if (!auth.startsWith("Bearer ")) {
            logGateway.warn("jwt-filter", "Formato inválido para ruta: " + path, null);
            return Mono.error(new AuthenticationException(Constantes.MSG_INVALID_TOKEN_FORMAT));
        }

        String token = auth.replace("Bearer ", "");
        logGateway.debug("jwt-filter", Constantes.MSG_TOKEN_EXTRACTED + " para ruta: " + path);
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return path.contains("login") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/webjars") ||
                path.contains("/actuator");
    }
}