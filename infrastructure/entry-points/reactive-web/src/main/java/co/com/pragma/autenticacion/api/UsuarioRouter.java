package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.media.Content;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UsuarioRouter {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = UsuarioHandler.class,
                    beanMethod = "registrarUsuario",
                    method = RequestMethod.POST,
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Registrar un usuario",
                            operationId = "registrarUsuario",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Datos del usuario a registrar",
                                    required = true,
                                    content = @io.swagger.v3.oas.annotations.media.Content(
                                            mediaType = "application/json",
                                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                                    implementation = Usuario.class
                                            )
                                    )
                            ),
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "201",
                                            description = "Usuario registrado correctamente",
                                            content = @io.swagger.v3.oas.annotations.media.Content(
                                                    mediaType = "application/json",
                                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                                            implementation = Usuario.class
                                                    )
                                            )
                                    ),
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos",
                                            content = @io.swagger.v3.oas.annotations.media.Content(
                                                    mediaType = "application/json"
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listarUsuarios",
                    method = RequestMethod.GET,
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Listar todos los usuarios",
                            operationId = "listarUsuarios",
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "200",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/exist",
                    beanClass = UsuarioHandler.class,
                    beanMethod = "obtenerUsuarioPorEmail",
                    method = RequestMethod.GET,
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Verificar existencia de usuario por email",
                            operationId = "obtenerUsuarioPorEmail",
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "email",
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
                                            required = true,
                                            description = "Email del usuario a verificar"
                                    )
                            },
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "200",
                                            content = @Content(mediaType = "application/json"),
                                            description = "Json con un valor de si existe o no"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/eliminar/{id}",
                    beanClass = UsuarioHandler.class,
                    beanMethod = "eliminarUsuario",
                    method = RequestMethod.DELETE,
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Eliminar un usuario por ID",
                            operationId = "eliminarUsuario",
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "id",
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
                                            required = true,
                                            description = "ID del usuario a eliminar"
                                    )
                            },
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "204",
                                            description = "Usuario eliminado correctamente",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UsuarioHandler handler) {
        return route(POST("/api/v1/usuarios"), handler::registrarUsuario)
                .andRoute(POST("/api/v1/login"), handler::login)
                .andRoute(GET("/api/v1/usuarios"), handler::listarUsuarios)
                .andRoute(GET("/api/v1/usuarios/exist"), handler::existeUsuarioPorEmail)
                .andRoute(GET("/api/v1/usuarios/search-by-email"), handler::usuarioPorEmail)
                .andRoute(DELETE("/api/v1/usuarios/eliminar/{id}"), handler::eliminarUsuario);
    }
}
