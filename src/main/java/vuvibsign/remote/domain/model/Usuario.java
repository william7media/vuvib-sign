package vuvibsign.remote.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    private Long id;
    private String codigo;
    private String nombre;
    private String telefono;
    private String correo;
    private String cedula;
    private String pasaporte;
    private String username;
    private String estado;
    private String tipo;
    private String cargo;

    public Optional<Usuario> toOptional() {
        return Optional.of(this);
    }

}
