package co.com.pragma.autenticacion.model.rol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Rol {
    private Integer idRol;
    private String nombre;
    private String descripcion;
}
