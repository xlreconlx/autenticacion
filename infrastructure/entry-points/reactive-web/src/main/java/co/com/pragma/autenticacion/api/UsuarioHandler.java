package co.com.pragma.autenticacion.api;


import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioHandler {
    private final UsuarioUseCase usuarioUseCase;

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(Usuario.class)
                .flatMap(usuarioUseCase::registrarUsuario)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> listarUsuarios(ServerRequest request) {
        Flux<Usuario> usuarios = usuarioUseCase.listarUsuarios();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(usuarios, Usuario.class);
    }

    public Mono<ServerResponse> obtenerUsuarioPorId(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return usuarioUseCase.obtenerUsuarioPorId(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminarUsuario(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return usuarioUseCase.eliminarUsuario(id)
                .then(ServerResponse.noContent().build());
    }
}
