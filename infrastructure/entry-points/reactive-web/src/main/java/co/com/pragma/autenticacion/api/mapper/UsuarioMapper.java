package co.com.pragma.autenticacion.api.mapper;

import co.com.pragma.autenticacion.api.dto.UsuarioDTO;
import co.com.pragma.autenticacion.model.usuario.Usuario;

public class UsuarioMapper {
    public static UsuarioDTO toDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getDocumentoIdentidad(),
                usuario.getTelefono(),
                usuario.getSalarioBase(),
                usuario.getFechaNacimiento(),
                usuario.getRol() != null ? usuario.getRol().getNombre() : null
        );
    }
}
