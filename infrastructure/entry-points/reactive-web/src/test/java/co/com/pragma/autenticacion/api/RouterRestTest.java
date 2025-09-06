package co.com.pragma.autenticacion.api;


import co.com.pragma.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {UsuarioRouter.class, UsuarioHandler.class, UsuarioUseCase.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;
    @MockitoBean
    private UsuarioHandler usuarioHandler;

    @Test
    void testListenGETUseCase() {

    }

    @Test
    void testListenGETOtherUseCase() {

    }

    @Test
    void testListenPOSTUseCase() {

    }
}
