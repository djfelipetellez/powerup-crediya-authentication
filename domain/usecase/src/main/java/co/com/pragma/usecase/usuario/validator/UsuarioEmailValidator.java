package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.exceptions.BusinessRuleException;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioEmailValidator implements UsuarioValidator {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<Void> validate(Usuario usuario, Integer roleId) {
        return usuarioRepository.findByEmail(usuario.getEmail())
                .hasElement()
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessRuleException(Constantes.MSG_EMAIL_DUPLICATE)))
                .then();
    }
}