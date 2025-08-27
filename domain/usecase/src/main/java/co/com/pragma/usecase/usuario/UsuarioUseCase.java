package co.com.pragma.usecase.usuario;

import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    private static final long SALARIO_MINIMO = 0L;
    private static final long SALARIO_MAXIMO = 15000000L;
    private static final String MSG_SALARIO = "El salario base debe estar entre ";
    private static final String MSG_ROL_NOT_FOUND = "El rol no existe en el sistema.";
    private static final String MSG_EMAIL_USER_ALREADY_EXISTS = "Ya existe un usuario registrado con ese email.";

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId) {

        return validarSalario(usuario)
                .then(rolRepository.findById(roleId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(MSG_ROL_NOT_FOUND)))
                        .flatMap(rol -> {
                            usuario.setRol(rol);
                            return Mono.just(usuario);
                        }))
                .flatMap(userWithRol -> {
                    return usuarioRepository.findByEmail(userWithRol.getEmail())
                            .flatMap(existingUser -> Mono.<Usuario>error(new IllegalArgumentException(MSG_EMAIL_USER_ALREADY_EXISTS)))
                            .switchIfEmpty(usuarioRepository.save(userWithRol));
                });
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        if (usuario.getSalarioBase() == null || usuario.getSalarioBase() < SALARIO_MINIMO || usuario.getSalarioBase() > SALARIO_MAXIMO) {
            return Mono.error(new IllegalArgumentException(MSG_SALARIO + SALARIO_MINIMO + " y " + SALARIO_MAXIMO));
        }

        return Mono.empty();
    }
}