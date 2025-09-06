package co.com.pragma.autenticacion.usecase.auth;

import co.com.pragma.autenticacion.model.usuario.gateways.TokenProviderRepository;
import co.com.pragma.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TokenProviderRepository tokenProvider;

    public Mono<String> login(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    if (!usuario.getPassword().equals(password)) {
                        return Mono.error(new IllegalArgumentException("Credenciales invalidas"));
                    }
                    return Mono.just(tokenProvider.generateToken(usuario));
                });
    }

}
