package co.com.pragma.autenticacion.r2dbcmysql.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.com.pragma.autenticacion.r2dbcmysql.entity.R2dbcUsuario;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcUsuarioRepository extends ReactiveCrudRepository<R2dbcUsuario, Integer> {
    Mono<R2dbcUsuario> findByEmail(String email);
}
