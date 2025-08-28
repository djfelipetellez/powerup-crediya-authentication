package co.com.pragma.config;

import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository, RolUseCase rolUseCase) {
        return new UsuarioUseCase(usuarioRepository, rolUseCase);
    }

    @Bean
    public RolUseCase rolUseCase(RolRepository rolRepository) {
        return new RolUseCase(rolRepository);
    }
}