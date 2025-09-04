package co.com.pragma.config;

import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import co.com.pragma.usecase.usuario.validator.CompositeUsuarioValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioEmailValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioSalaryValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ValidatorsConfig {


    @Bean
    public UsuarioSalaryValidator usuarioSalaryValidator() {
        return new UsuarioSalaryValidator();
    }

    @Bean
    public UsuarioEmailValidator usuarioEmailValidator(UsuarioRepository usuarioRepository) {
        return new UsuarioEmailValidator(usuarioRepository);
    }

    @Bean
    public UsuarioValidator usuarioValidator(
            UsuarioSalaryValidator salaryValidator,
            UsuarioEmailValidator emailValidator) {
        return new CompositeUsuarioValidator(List.of(
                salaryValidator,
                emailValidator
        ));
    }
}