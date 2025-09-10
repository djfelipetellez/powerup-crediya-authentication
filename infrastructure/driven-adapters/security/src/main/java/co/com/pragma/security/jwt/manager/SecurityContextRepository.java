package co.com.pragma.security.jwt.manager;

import co.com.pragma.model.common.gateways.LogGateway;
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
    private final LogGateway logGateway;

    public SecurityContextRepository(JwtAuthenticationManager jwtAuthenticationManager, LogGateway logGateway) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.logGateway = logGateway;
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
            logGateway.info("security-context", "Cargando contexto de seguridad para token");

            // Crear authentication object con el token
            Authentication auth = new UsernamePasswordAuthenticationToken(token, token);

            // Usar el JwtAuthenticationManager para autenticar
            return jwtAuthenticationManager.authenticate(auth)
                    .doOnNext(authentication -> logGateway.info("security-context",
                            "Contexto de seguridad creado para: " + authentication.getName() +
                                    " con roles: " + authentication.getAuthorities()))
                    .map(SecurityContextImpl::new);
        }

        logGateway.debug("security-context", "No hay token en el exchange");
        return Mono.empty();
    }
}