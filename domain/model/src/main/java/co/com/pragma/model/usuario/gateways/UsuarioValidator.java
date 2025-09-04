package co.com.pragma.model.usuario.gateways;

import co.com.pragma.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioValidator {
    Mono<Void> validate(Usuario usuario, Integer roleId);
}