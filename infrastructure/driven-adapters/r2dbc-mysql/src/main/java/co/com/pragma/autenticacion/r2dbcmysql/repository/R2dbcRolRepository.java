package co.com.pragma.autenticacion.r2dbcmysql.repository;

import co.com.pragma.autenticacion.r2dbcmysql.entity.R2dbcRol;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcRolRepository extends ReactiveCrudRepository<R2dbcRol, Integer> {
}
