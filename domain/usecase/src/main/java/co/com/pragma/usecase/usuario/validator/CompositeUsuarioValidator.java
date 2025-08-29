package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CompositeUsuarioValidator implements UsuarioValidator {
    
    private final List<UsuarioValidator> validators;
    
    @Override
    public Mono<Void> validate(Usuario usuario, Integer roleId) {
        return Flux.fromIterable(validators)
                .flatMap(validator -> validator.validate(usuario, roleId))
                .then();
    }
}