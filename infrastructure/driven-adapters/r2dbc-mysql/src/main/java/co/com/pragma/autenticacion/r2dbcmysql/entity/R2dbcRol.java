package co.com.pragma.autenticacion.r2dbcmysql.entity;

import co.com.pragma.autenticacion.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("rol")
@NoArgsConstructor
@AllArgsConstructor
public class R2dbcRol {
    @Id
    private Integer idRol;
    private String nombre;
    private String descripcion;

    public static R2dbcRol fromModel(Rol rol) {
        return new R2dbcRol(rol.getIdRol(),rol.getNombre(), rol.getDescripcion());
    }

    public Rol toModel() {
        return new Rol(this.idRol, this.nombre, this.descripcion);
    }
}
