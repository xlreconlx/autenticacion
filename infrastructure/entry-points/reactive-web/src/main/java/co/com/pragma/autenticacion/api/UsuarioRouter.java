package co.com.pragma.autenticacion.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UsuarioRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UsuarioHandler handler) {
        return route(POST("api/v1/usuarios"), handler::registrarUsuario)
                .andRoute(GET("/api/v1/usuarios"), handler::listarUsuarios)
                .andRoute(DELETE("/api/v1/usuarios/eliminar/{id}"), handler::eliminarUsuario);
    }
}
