package co.com.pragma.security.jwt.filter;

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
            // Solo loguear para rutas que no sean actuator para reducir el ruido
            if (!path.contains("/actuator")) {
                logGateway.info("jwt-filter", "=== RUTA PÚBLICA DETECTADA - Permitiendo acceso sin token: " + path + " ===");
            }
            return chain.filter(exchange);
        }

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            logGateway.warn("jwt-filter", "Token no encontrado o formato inválido para ruta: " + path, null);
            // No lanzar excepción aquí - dejar que Spring Security maneje la falta de autenticación
            return chain.filter(exchange);
        }

        String token = auth.replace("Bearer ", "");
        logGateway.debug("jwt-filter", Constantes.MSG_TOKEN_EXTRACTED + " para ruta: " + path);
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return path.contains("login") ||
                path.contains("validate-token") ||
                path.contains("validar-existencia") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/webjars") ||
                path.contains("/actuator");
    }
}