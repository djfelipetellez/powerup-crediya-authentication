package co.com.pragma.security.jwt.manager;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.security.jwt.provider.JwtProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;
    private final LogGateway logGateway;

    public JwtAuthenticationManager(JwtProvider jwtProvider, LogGateway logGateway) {
        this.jwtProvider = jwtProvider;
        this.logGateway = logGateway;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> jwtProvider.getClaims(auth.getCredentials().toString()))
                .doOnNext(claims -> logGateway.info("jwt-auth-manager",
                        "Token autenticado para usuario: " + claims.getSubject()))
                .onErrorResume(e -> {
                    logGateway.error("jwt-auth-manager", "Token inválido: " + e.getMessage(), e);
                    return Mono.error(new RuntimeException("bad token"));
                })
                .map(claims -> new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        Stream.of(claims.get("roles"))
                                .filter(Objects::nonNull)
                                .map(role -> (List<Map<String, String>>) role)
                                .flatMap(role -> role.stream()
                                        .map(r -> r.get("authority"))
                                        .filter(Objects::nonNull)
                                        .map(SimpleGrantedAuthority::new))
                                .toList())
                );
    }
}