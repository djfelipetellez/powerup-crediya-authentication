package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class UsuarioSalaryValidator implements UsuarioValidator {
    
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal(Constantes.SALARY_MIN);
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal(Constantes.SALARY_MAX);
    private static final String MSG_SALARIO = Constantes.MSG_SALARY_RANGE;

    @Override
    public Mono<Void> validate(Usuario usuario, Integer roleId) {
        return Mono.fromCallable(usuario::getSalarioBase)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_SALARY_NULL)))
                .flatMap(salario -> {
                    if (salario.compareTo(SALARIO_MINIMO) < 0 || salario.compareTo(SALARIO_MAXIMO) > 0) {
                        return Mono.error(new IllegalArgumentException(
                                MSG_SALARIO + SALARIO_MINIMO.toPlainString() + " y " + SALARIO_MAXIMO.toPlainString()
                        ));
                    }
                    return Mono.empty();
                });
    }
}