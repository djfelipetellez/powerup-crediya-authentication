package co.com.pragma.config;

import adapter.LoggingAdapter;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.rol.gateways.RolValidator;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import co.com.pragma.usecase.usuario.validator.CompositeUsuarioValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioEmailValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioParameterValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioSalaryValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public LoggingGateway loggingGateway() {
        return new LoggingAdapter();
    }

    // Validator beans
    @Bean
    public UsuarioParameterValidator usuarioParameterValidator(LoggingGateway loggingGateway) {
        return new UsuarioParameterValidator(loggingGateway);
    }

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
            UsuarioParameterValidator parameterValidator,
            UsuarioSalaryValidator salaryValidator,
            UsuarioEmailValidator emailValidator) {
        return new CompositeUsuarioValidator(List.of(
                parameterValidator,
                salaryValidator,
                emailValidator
        ));
    }

    // Use case beans
    @Bean
    public RolUseCase rolUseCase(RolRepository rolRepository, LoggingGateway loggingGateway) {
        return new RolUseCase(rolRepository, loggingGateway);
    }

    @Bean
    public RolValidator rolValidator(RolUseCase rolUseCase) {
        return rolUseCase;
    }

    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository, RolValidator rolValidator, UsuarioValidator usuarioValidator, LoggingGateway loggingGateway) {
        return new UsuarioUseCase(usuarioRepository, rolValidator, usuarioValidator, loggingGateway);
    }
}