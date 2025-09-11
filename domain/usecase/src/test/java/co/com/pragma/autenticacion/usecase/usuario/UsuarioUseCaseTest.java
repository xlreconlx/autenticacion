package co.com.pragma.autenticacion.usecase.usuario;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.autenticacion.usecase.rol.RolUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolUseCase rolUseCase;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@test.com");
        usuario.setSalarioBase(5000000L);
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setIdRol(1);
    }

    @Test
    void registrarUsuario_ok() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));
        when(rolUseCase.getRolById(1)).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario))
                .expectNextMatches(u ->
                        u.getEmail().equals("juan@test.com") &&
                                u.getRol() != null &&
                                "ADMIN".equals(u.getRol().getNombre()))
                .verifyComplete();
    }

    @Test
    void registrarUsuario_correoYaExiste() {
        // El correo ya existe
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El correo ya esta registrado"))
                .verify();


        //verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarUsuario_emailInvalido() {
        usuario.setEmail("correo_invalido");

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Correo electronico invalido"))
                .verify();
    }

    @Test
    void registrarUsuario_salarioFueraDeRango() {
        usuario.setSalarioBase(20_000_000L);

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("salario_base fuera de rango"))
                .verify();
    }

    @Test
    void registrarUsuario_fechaFutura() {
        usuario.setFechaNacimiento(LocalDate.now().plusDays(1));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("fecha_nacimiento no puede ser futura"))
                .verify();
    }

    @Test
    void listarUsuarios_ok() {
        when(usuarioRepository.findAll()).thenReturn(Flux.just(usuario));
        when(rolUseCase.getRolById(1)).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.listarUsuarios())
                .expectNextMatches(u ->
                        u.getEmail().equals("juan@test.com") &&
                                u.getRol() != null &&
                                "ADMIN".equals(u.getRol().getNombre()))
                .verifyComplete();
    }

    @Test
    void obtenerUsuarioPorId_ok() {
        when(usuarioRepository.findById(1)).thenReturn(Mono.just(usuario));
        when(rolUseCase.getRolById(1)).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorId(1))
                .expectNextMatches(u ->
                        u.getIdUsuario() == 1 &&
                                u.getRol() != null &&
                                "ADMIN".equals(u.getRol().getNombre()))
                .verifyComplete();
    }

    @Test
    void obtenerUsuarioPorId_noExiste() {
        when(usuarioRepository.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorId(1))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

    @Test
    void obtenerUsuarioPorEmail_ok() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.just(usuario));
        when(rolUseCase.getRolById(1)).thenReturn(Mono.just(rol));

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorEmail("juan@test.com"))
                .expectNextMatches(u ->
                        u.getEmail().equals("juan@test.com") &&
                                u.getRol() != null &&
                                "ADMIN".equals(u.getRol().getNombre()))
                .verifyComplete();
    }

    @Test
    void obtenerUsuarioPorEmail_noExiste() {
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorEmail("noexiste@test.com"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

    @Test
    void eliminarUsuario_ok() {
        when(usuarioRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.eliminarUsuario(1))
                .verifyComplete();
    }
}