package co.com.pragma.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "api.paths.usuarios")
public class UsuarioPath {

    private String base;
    private String validarExistenciaUsuario;

    public String getUsuarios() {
        return base;
    }
}