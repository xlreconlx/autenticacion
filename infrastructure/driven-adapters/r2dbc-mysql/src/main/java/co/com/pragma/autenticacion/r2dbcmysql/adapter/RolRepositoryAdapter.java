package co.com.pragma.autenticacion.r2dbcmysql.adapter;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.r2dbcmysql.entity.R2dbcRol;
import co.com.pragma.autenticacion.r2dbcmysql.repository.R2dbcRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RolRepositoryAdapter implements RolRepository {
    private final R2dbcRolRepository rolRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Rol> findById(Integer id) {
        return rolRepository.findById(id).map(R2dbcRol::toModel).as(transactionalOperator::transactional);
    }
}
