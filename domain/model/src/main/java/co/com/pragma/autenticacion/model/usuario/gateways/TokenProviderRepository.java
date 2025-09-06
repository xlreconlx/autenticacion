package co.com.pragma.autenticacion.model.usuario.gateways;

import co.com.pragma.autenticacion.model.usuario.Usuario;

public interface TokenProviderRepository {
    String generateToken(Usuario usuario);
    boolean validateToken(String token);
    String extractEmail(String token);
}
