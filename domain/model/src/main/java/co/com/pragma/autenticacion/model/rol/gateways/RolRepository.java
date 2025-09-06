package co.com.pragma.autenticacion.model.rol.gateways;

import co.com.pragma.autenticacion.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Integer id);
}
