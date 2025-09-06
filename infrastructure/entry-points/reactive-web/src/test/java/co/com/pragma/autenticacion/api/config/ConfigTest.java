package co.com.pragma.autenticacion.api.config;

import co.com.pragma.autenticacion.api.UsuarioHandler;
import co.com.pragma.autenticacion.api.UsuarioRouter;
import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UsuarioRouter.class, UsuarioHandler.class, UsuarioUseCase.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;
    @MockitoBean
    private UsuarioHandler usuarioHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UsuarioHandler handler = new UsuarioHandler(usuarioUseCase); // No debe ser null
        RouterFunction<ServerResponse> router = new UsuarioRouter().routerFunction(handler);
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(usuarioUseCase.listarUsuarios())
                .thenReturn(Flux.just(new Usuario(1, "test@email.com","","","","",1l,1,"", LocalDate.of(1996, 5, 10))));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}