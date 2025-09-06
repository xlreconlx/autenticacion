package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class RolUseCase {

    private final RolRepository rolRepository;
    private static final Logger logger = Logger.getLogger(RolUseCase.class.getName());

    public Mono<Rol> getRolById(Integer idRol){
        logger.info("Buscando Rol por id: " +idRol);
        return rolRepository.findById(idRol).switchIfEmpty(Mono.error(new RuntimeException("Rol no encontrado")));
    }
}
