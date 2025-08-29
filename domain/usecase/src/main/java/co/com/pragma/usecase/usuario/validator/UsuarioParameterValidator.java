package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioParameterValidator implements UsuarioValidator {
    
    private final LoggingGateway loggingGateway;
    
    @Override
    public Mono<Void> validate(Usuario usuario, Integer roleId) {
        return Mono.fromRunnable(() -> {
            if (usuario == null) {
                loggingGateway.warn("ParameterValidator", "Usuario nulo", new IllegalArgumentException(Constantes.MSG_USUARIO_NULL));
                throw new IllegalArgumentException(Constantes.MSG_USUARIO_NULL);
            }
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                loggingGateway.warn("ParameterValidator", "Email vacío", new IllegalArgumentException(Constantes.MSG_EMAIL_EMPTY));
                throw new IllegalArgumentException(Constantes.MSG_EMAIL_EMPTY);
            }
            if (roleId == null) {
                loggingGateway.warn("ParameterValidator", "RoleId nulo", new IllegalArgumentException(Constantes.MSG_ROLE_ID_NULL));
                throw new IllegalArgumentException(Constantes.MSG_ROLE_ID_NULL);
            }
        });
    }
}