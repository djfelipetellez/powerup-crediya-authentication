package co.com.pragma.api;

import co.com.pragma.api.config.RolPath;
import co.com.pragma.api.config.UsuarioPath;
import co.com.pragma.api.util.OpenApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UsuarioPath usuarioPath;
    private final RolPath rolPath;

    @Bean
    public RouterFunction<ServerResponse> usuarioRoutes(Handler handler) {
        return route().POST(usuarioPath.getUsuarios(),
                handler::registrarUsuario, OpenApiUtil::registrarUsuario).build();
    }

    @Bean
    public RouterFunction<ServerResponse> rolRoutes(Handler handler) {
        return route().POST(rolPath.getRoles(),
                handler::registrarRol, OpenApiUtil::registrarRol).build();
    }

    @Bean
    public RouterFunction<ServerResponse> validacionRoutes(Handler handler) {
        return route()
                .POST(usuarioPath.getValidarExistenciaUsuario(), accept(MediaType.APPLICATION_JSON),
                        handler::validarExistenciaUsuario, OpenApiUtil::validarDatosUsuario)
                .build();
    }
}