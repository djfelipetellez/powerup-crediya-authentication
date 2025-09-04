package co.com.pragma.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UsuarioPath {

    private final String usuarios;
    private final String validarExistenciaUsuario;

    public UsuarioPath(@Value("${api.paths.usuarios}") String usuarios,
                       @Value("${api.paths.validacion.validar-existencia-usuario}") String validarExistenciaUsuario) {
        this.usuarios = usuarios;
        this.validarExistenciaUsuario = validarExistenciaUsuario;
    }
}