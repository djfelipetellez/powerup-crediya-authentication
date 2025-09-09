package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.auth.UsuarioCredencial;
import reactor.core.publisher.Mono;

public interface UsuarioCredencialRepository {

    Mono<UsuarioCredencial> findByEmail(String email);

    Mono<UsuarioCredencial> save(UsuarioCredencial usuarioCredencial);

    Mono<UsuarioCredencial> updateLastLogin(Integer id);

}