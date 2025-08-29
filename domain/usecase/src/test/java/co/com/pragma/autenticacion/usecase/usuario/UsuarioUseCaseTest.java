package co.com.pragma.autenticacion.usecase.usuario;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioUseCaseTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioValido = new Usuario();
        usuarioValido.setIdUsuario(1);
        usuarioValido.setNombre("Anderson");
        usuarioValido.setApellido("Urrego");
        usuarioValido.setEmail("anderson@test.com");
        usuarioValido.setSalarioBase(5_000_000L);
        usuarioValido.setFechaNacimiento(LocalDate.of(2000, 1, 1));
    }

    @Test
    void registrarUsuario_deberiaRegistrarCorrectamente() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectNextMatches(u -> u.getIdUsuario() == 1 && u.getEmail().equals("anderson@test.com"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail(usuarioValido.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_deberiaFallarPorCamposObligatorios() {
        Usuario usuarioInvalido = new Usuario();
        usuarioInvalido.setNombre("");
        usuarioInvalido.setApellido("Perez");
        usuarioInvalido.setEmail(null);

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioInvalido))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Campos obligatorios"))
                .verify();
    }

    @Test
    void registrarUsuario_deberiaFallarPorEmailInvalido() {
        usuarioValido.setEmail("correo_invalido");

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Correo electronico invalido"))
                .verify();
    }

    @Test
    void registrarUsuario_deberiaFallarPorSalarioFueraDeRango() {
        usuarioValido.setSalarioBase(20_000_000L);

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("salario_base fuera de rango"))
                .verify();
    }

    @Test
    void registrarUsuario_deberiaFallarPorFechaFutura() {
        usuarioValido.setFechaNacimiento(LocalDate.now().plusDays(1));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("fecha_nacimiento no puede ser futura"))
                .verify();
    }

    @Test
    void registrarUsuario_deberiaFallarSiCorreoYaRegistrado() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail()))
                .thenReturn(Mono.just(usuarioValido));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(Mono.just(usuarioValido));

        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El correo ya esta registrado"))
                .verify();
    }

    @Test
    void listarUsuarios_deberiaRetornarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Flux.just(usuarioValido));

        StepVerifier.create(usuarioUseCase.listarUsuarios())
                .expectNextMatches(u -> u.getIdUsuario() == 1)
                .verifyComplete();

        verify(usuarioRepository).findAll();
    }

    @Test
    void obtenerUsuarioPorId_deberiaRetornarUsuario() {
        when(usuarioRepository.findById(1)).thenReturn(Mono.just(usuarioValido));

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorId(1))
                .expectNextMatches(u -> u.getEmail().equals("anderson@test.com"))
                .verifyComplete();

        verify(usuarioRepository).findById(1);
    }

    @Test
    void obtenerUsuarioPorEmail_deberiaRetornarUsuario() {
        when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Mono.just(usuarioValido));

        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorEmail(usuarioValido.getEmail()))
                .expectNextMatches(u -> u.getNombre().equals("Anderson"))
                .verifyComplete();

        verify(usuarioRepository).findByEmail(usuarioValido.getEmail());
    }

    @Test
    void eliminarUsuario_deberiaEliminarCorrectamente() {
        when(usuarioRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.eliminarUsuario(1))
                .verifyComplete();

        verify(usuarioRepository).deleteById(1);
    }



}
