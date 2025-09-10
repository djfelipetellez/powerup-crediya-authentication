package co.com.pragma.security.jwt.manager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager jwtAuthenticationManager;

    public SecurityContextRepository(JwtAuthenticationManager jwtAuthenticationManager) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // No necesitamos implementar save para JWT (stateless)
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        // Obtener el token que el JwtFilter puso en los atributos del exchange
        String token = exchange.getAttribute("token");

        if (token != null) {
            // Crear authentication object con el token
            Authentication auth = new UsernamePasswordAuthenticationToken(token, token);

            // Usar el JwtAuthenticationManager para autenticar
            return jwtAuthenticationManager.authenticate(auth)
                    .map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}