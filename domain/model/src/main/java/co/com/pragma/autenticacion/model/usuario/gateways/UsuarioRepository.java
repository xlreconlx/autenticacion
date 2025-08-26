package co.com.pragma.autenticacion.model.usuario.gateways;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario user);
    Mono<Usuario> findById(Integer id);
    Mono<Usuario> findByEmail(String email);
    Flux<Usuario> findAll();
    Mono<Void> deleteById(Integer id);
}
