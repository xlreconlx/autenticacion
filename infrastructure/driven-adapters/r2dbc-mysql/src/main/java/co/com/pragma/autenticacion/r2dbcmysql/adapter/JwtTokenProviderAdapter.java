package co.com.pragma.autenticacion.r2dbcmysql.adapter;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import co.com.pragma.autenticacion.model.usuario.gateways.TokenProviderRepository;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProviderRepository {

    @Override
    public String generateToken(Usuario usuario) {
        return "";
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public String extractEmail(String token) {
        return "";
    }
}
