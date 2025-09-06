package co.com.pragma.autenticacion.api;


import co.com.pragma.autenticacion.api.dto.LoginRequest;
import co.com.pragma.autenticacion.api.dto.LoginResponse;
import co.com.pragma.autenticacion.api.mapper.UsuarioMapper;
import co.com.pragma.autenticacion.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UsuarioHandler {
    private final UsuarioUseCase usuarioUseCase;
    private final PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(loginRequest ->
                        usuarioUseCase.obtenerUsuarioPorEmail(loginRequest.getEmail())
                                .filter(usuario -> passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                                .map(usuario -> {
                                    String token = JwtUtil.generateToken(
                                            usuario.getEmail(),
                                            usuario.getRol().getNombre()
                                    );
                                    return new LoginResponse(token);
                                })
                                .flatMap(response -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response))
                                .switchIfEmpty(
                                        ServerResponse.status(401)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(Map.of("error","Credenciales invalidas")))
                );
    }

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        String rol = request.exchange().getAttribute("rol");

        if (rol == null || !(rol.toUpperCase().equals("ADMIN"))) {
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .bodyValue(Map.of("error", "No tienes permisos para registrar usuarios"));
        }

        return request.bodyToMono(Usuario.class)
                .map(usuario -> {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    return usuario;
                })
                .flatMap(usuarioUseCase::registrarUsuario)
                .map(UsuarioMapper::toDto)
                .flatMap(usuarioDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuarioDto));
    }

    public Mono<ServerResponse> listarUsuarios(ServerRequest request) {
        return usuarioUseCase.listarUsuarios()
                .map(UsuarioMapper::toDto)
                .collectList()
                .flatMap(usuariosDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuariosDto))
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> obtenerUsuarioPorId(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return usuarioUseCase.obtenerUsuarioPorId(id)
                .map(UsuarioMapper::toDto)
                .flatMap(usuarioDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuarioDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> usuarioPorEmail(ServerRequest request) {
        String email = request.queryParam("email")
                .orElseThrow(() -> new IllegalArgumentException("El parametro 'email' es requerido"));

        return usuarioUseCase.obtenerUsuarioPorEmail(email)
                .map(UsuarioMapper::toDto)
                .flatMap(usuarioDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuarioDTO))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> existeUsuarioPorEmail(ServerRequest request) {
        String email = request.queryParam("email")
                .orElseThrow(() -> new IllegalArgumentException("El parametro 'email' es requerido"));

        return usuarioUseCase.obtenerUsuarioPorEmail(email)
                .map(user -> true)
                .switchIfEmpty(Mono.just(false))
                .flatMap(exists -> {
                    Map<String, Boolean> response = Map.of("exists", exists);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }


    public Mono<ServerResponse> eliminarUsuario(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return usuarioUseCase.eliminarUsuario(id)
                .then(ServerResponse.noContent().build());
    }
}
