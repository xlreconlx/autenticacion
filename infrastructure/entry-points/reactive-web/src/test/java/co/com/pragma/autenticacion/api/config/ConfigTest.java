package co.com.pragma.autenticacion.api.config;

import co.com.pragma.autenticacion.api.UsuarioHandler;
import co.com.pragma.autenticacion.api.dto.LoginRequest;
import co.com.pragma.autenticacion.api.dto.LoginResponse;
import co.com.pragma.autenticacion.api.dto.UsuarioDTO;
import co.com.pragma.autenticacion.api.util.JwtUtil;
import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;


class ConfigTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioHandler usuarioHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 🔹 Test login exitoso
    @Test
    void loginSuccess() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "1234");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@email.com");
        usuario.setPassword("encodedPassword");
        Rol rol = new Rol();
        rol.setNombre("USER");
        usuario.setRol(rol);

        when(usuarioUseCase.obtenerUsuarioPorEmail(loginRequest.getEmail()))
                .thenReturn(Mono.just(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                .thenReturn(true);
        when(jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().getNombre()))
                .thenReturn("fake-jwt");

        // 🔹 Montamos el handler en un router para probarlo con WebTestClient
        RouterFunction<ServerResponse> route = RouterFunctions.route()
                .POST("/login", usuarioHandler::login)
                .build();

        WebTestClient client = WebTestClient.bindToRouterFunction(route).build();

        client.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .consumeWith(response ->
                        assertEquals("fake-jwt", response.getResponseBody().getToken()));
    }

    // 🔹 Test login fallido (password inválido)
    @Test
    void loginInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("wrong@email.com", "bad");

        Usuario usuario = new Usuario();
        usuario.setEmail("wrong@email.com");
        usuario.setPassword("encodedPassword");

        when(usuarioUseCase.obtenerUsuarioPorEmail(loginRequest.getEmail()))
                .thenReturn(Mono.just(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                .thenReturn(false);

        // 🔹 Montamos el handler en un router temporal
        RouterFunction<ServerResponse> route = RouterFunctions.route()
                .POST("/login", usuarioHandler::login)
                .build();

        WebTestClient client = WebTestClient.bindToRouterFunction(route).build();

        client.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> body = response.getResponseBody();
                    assert body != null;
                    assertEquals("Credenciales invalidas", body.get("error"));
                });
    }

    // 🔹 Test listar usuarios
    @Test
    void listarUsuariosOk() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("test@email.com");

        when(usuarioUseCase.listarUsuarios()).thenReturn(Flux.just(usuario));

        // 🔹 Montamos el handler en un router temporal
        RouterFunction<ServerResponse> route = RouterFunctions.route()
                .GET("/usuarios", usuarioHandler::listarUsuarios)
                .build();

        WebTestClient client = WebTestClient.bindToRouterFunction(route).build();

        client.get()
                .uri("/usuarios")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioDTO.class) // o Map si prefieres validar el JSON crudo
                .consumeWith(response -> {
                    var usuarios = response.getResponseBody();
                    assert usuarios != null;
                    assertFalse(usuarios.isEmpty());
                    assertEquals("test@email.com", usuarios.get(0).getEmail());
                });
    }

    // 🔹 Test eliminar usuario
    @Test
    void eliminarUsuarioOk() {
        when(usuarioUseCase.eliminarUsuario(1)).thenReturn(Mono.empty());

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .build();

        Mono<ServerResponse> response = usuarioHandler.eliminarUsuario(request);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}