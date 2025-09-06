package co.com.pragma.autenticacion.api.dto;

import co.com.pragma.autenticacion.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Long salarioBase;
    private LocalDate fechaNacimiento;
    private String rol;
}
