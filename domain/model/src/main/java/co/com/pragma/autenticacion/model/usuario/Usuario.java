package co.com.pragma.autenticacion.model.usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Long salarioBase;
    private Integer idRol;
    private String password;
    private LocalDate fechaNacimiento;
}
