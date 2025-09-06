package co.com.pragma.autenticacion.r2dbcmysql.adapter;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.autenticacion.r2dbcmysql.entity.R2dbcUsuario;
import co.com.pragma.autenticacion.r2dbcmysql.repository.R2dbcUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final R2dbcUsuarioRepository dataRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        R2dbcUsuario entity = R2dbcUsuario.fromModel(usuario);
        return dataRepository.save(entity)
                .map(R2dbcUsuario::toModel).as(transactionalOperator::transactional);
    }

    @Override
    public Flux<Usuario> findAll() {
        return dataRepository.findAll()
                .map(R2dbcUsuario::toModel).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return dataRepository.deleteById(id).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Usuario> findById(Integer id) {
        return dataRepository.findById(id).map(R2dbcUsuario::toModel).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        return dataRepository.findByEmail(email).map(R2dbcUsuario::toModel).as(transactionalOperator::transactional);
    }

}
