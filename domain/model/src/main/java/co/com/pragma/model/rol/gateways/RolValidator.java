package co.com.pragma.model.rol.gateways;

import reactor.core.publisher.Mono;

public interface RolValidator {
    Mono<Void> validarRolExiste(Integer roleId);
}