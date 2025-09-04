package co.com.pragma.model.rol.gateways;

import co.com.pragma.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Integer id);

    Mono<Rol> save(Rol rol);
}