package co.com.pragma.autenticacion.usecase.usuario;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.autenticacion.usecase.rol.RolUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = Logger.getLogger(UsuarioUseCase.class.getName());
    private final RolUseCase rolUseCase;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {

        if (isNullOrEmpty(usuario.getNombre()) ||
                isNullOrEmpty(usuario.getApellido()) ||
                isNullOrEmpty(usuario.getEmail()) ||
                usuario.getSalarioBase() == null) {
            return Mono.error(badRequest("Campos obligatorios: nombres, apellidos, correo_electronico, salario_base"));
        }

        if (!isValidEmail(usuario.getEmail())) {
            return Mono.error(badRequest("Correo electronico invalido"));
        }

        if (usuario.getSalarioBase() < 0 || usuario.getSalarioBase() > 15_000_000L) {
            return Mono.error(badRequest("salario_base fuera de rango (0 - 15000000)"));
        }

        if (usuario.getFechaNacimiento() != null &&
                usuario.getFechaNacimiento().isAfter(LocalDate.now())) {
            return Mono.error(badRequest("fecha_nacimiento no puede ser futura"));
        }

        return usuarioRepository.findByEmail(usuario.getEmail())
                .flatMap(u -> Mono.<Usuario>error(badRequest("El correo ya esta registrado")))
                .switchIfEmpty(usuarioRepository.save(usuario)
                        .flatMap(saved ->
                                rolUseCase.getRolById(saved.getIdRol())
                                        .map(rol -> {
                                            saved.setRol(rol);
                                            return saved;
                                        })
                        ))
                .doOnSuccess(saved -> logger.info("Usuario registrado id=" + saved.getIdUsuario()))
                .doOnError(e -> logger.log(Level.WARNING, "Error registrando usuario: " + e.getMessage()));
    }

    public Flux<Usuario> listarUsuarios() {
        logger.info("Listando Usuarios");
        return usuarioRepository.findAll()
                .flatMap(usuario ->
                        rolUseCase.getRolById(usuario.getIdRol())
                                .map(rol -> {
                                    usuario.setRol(rol);
                                    return usuario;
                                })
                );
    }

    public Mono<Usuario> obtenerUsuarioPorId(Integer id) {
        logger.info("Buscando Usuario por id: " +id);
        return usuarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(usuario ->
                        rolUseCase.getRolById(usuario.getIdRol())
                                .map(rol -> {
                                    usuario.setRol(rol);
                                    return usuario;
                                })
                );
    }

    public Mono<Usuario> obtenerUsuarioPorEmail(String email) {
        logger.info("Buscando Usuario por email: " +email);
        return usuarioRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(usuario ->
                        rolUseCase.getRolById(usuario.getIdRol())
                                .map(rol -> {
                                    usuario.setRol(rol);
                                    return usuario;
                                })
                );
    }

    public Mono<Void> eliminarUsuario(Integer id) {
        logger.info("Eliminado usuario por id: " +id);
        return usuarioRepository.deleteById(id);
    }

    private boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches();
    }
    private IllegalArgumentException badRequest(String msg) { return new IllegalArgumentException(msg); }
}
