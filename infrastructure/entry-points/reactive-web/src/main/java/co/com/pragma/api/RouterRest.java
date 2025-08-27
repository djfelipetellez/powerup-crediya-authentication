package co.com.pragma.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> usuarioRoutes(Handler handler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), handler::registrarUsuario);
    }

    @Bean
    public RouterFunction<ServerResponse> rolRoutes(Handler handler) {
        return route(POST("/api/v1/roles").and(accept(MediaType.APPLICATION_JSON)), handler::registrarRol);
    }

}