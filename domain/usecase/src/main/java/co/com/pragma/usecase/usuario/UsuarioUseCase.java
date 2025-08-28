package co.com.pragma.usecase.usuario;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.LoggingUtil;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.usecase.rol.RolUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolUseCase rolUseCase;

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal(Constantes.SALARY_MIN);
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal(Constantes.SALARY_MAX);
    private static final String MSG_SALARIO = Constantes.MSG_SALARY_RANGE;

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId) {
        LoggingUtil.info("UsuarioUseCase", "Iniciando registro de usuario con roleId: " + roleId);

        if (usuario == null) {
            LoggingUtil.warn("UsuarioUseCase", "Intento de registro con usuario nulo", new IllegalArgumentException(Constantes.MSG_USUARIO_NULL));
            return Mono.error(new IllegalArgumentException(Constantes.MSG_USUARIO_NULL));
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            LoggingUtil.warn("UsuarioUseCase", "Intento de registro con email vacío", new IllegalArgumentException(Constantes.MSG_EMAIL_EMPTY));
            return Mono.error(new IllegalArgumentException(Constantes.MSG_EMAIL_EMPTY));
        }
        if (roleId == null) {
            LoggingUtil.warn("UsuarioUseCase", "Intento de registro con roleId nulo", new IllegalArgumentException(Constantes.MSG_ROLE_ID_NULL));
            return Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_ID_NULL));
        }

        LoggingUtil.info("UsuarioUseCase", "Validando datos para usuario: " + usuario.getEmail());

        return validarSalario(usuario)
                .then(validarEmailDuplicado(usuario.getEmail()))
                .then(rolUseCase.validarRolExiste(roleId))
                .then(usuarioRepository.registrarUsuarioCompleto(usuario, roleId))
                .doOnSuccess(usuarioRegistrado ->
                        LoggingUtil.info("UsuarioUseCase", "Usuario registrado exitosamente: " + usuarioRegistrado.getEmail())
                )
                .doOnError(error ->
                        LoggingUtil.error("UsuarioUseCase", "Error al registrar usuario: " + error.getMessage(), error)
                );
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        BigDecimal salario = usuario.getSalarioBase();

        if (salario == null) {
            return Mono.error(new IllegalArgumentException(Constantes.MSG_SALARY_NULL));
        }

        if (salario.compareTo(SALARIO_MINIMO) < 0 || salario.compareTo(SALARIO_MAXIMO) > 0) {
            return Mono.error(new IllegalArgumentException(
                    MSG_SALARIO + SALARIO_MINIMO.toPlainString() + " y " + SALARIO_MAXIMO.toPlainString()
            ));
        }

        return Mono.empty();
    }

    private Mono<Void> validarEmailDuplicado(String email) {
        return usuarioRepository.findByEmail(email)
                .hasElement()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException(Constantes.MSG_EMAIL_DUPLICATE));
                    }
                    return Mono.empty();
                });
    }

    public Mono<Usuario> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constantes.MSG_EMAIL_INVALID));
        }
        return usuarioRepository.findByEmail(email);
    }

}