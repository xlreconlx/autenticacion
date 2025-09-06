package co.com.pragma.autenticacion.r2dbcmysql.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import co.com.pragma.autenticacion.model.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("usuario")
@NoArgsConstructor
@AllArgsConstructor
public class R2dbcUsuario {
    @Id
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

    public static R2dbcUsuario fromModel(Usuario usuario) {
        return new R2dbcUsuario(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getDocumentoIdentidad(),
                usuario.getTelefono(),
                usuario.getSalarioBase(),
                usuario.getIdRol(),
                usuario.getPassword(),
                usuario.getFechaNacimiento()
        );
    }

    public Usuario toModel() {
        return new Usuario(
                this.idUsuario,
                this.nombre,
                this.apellido,
                this.email,
                this.documentoIdentidad,
                this.telefono,
                this.salarioBase,
                this.idRol,
                this.password,
                this.fechaNacimiento,
                null
        );
    }
}
