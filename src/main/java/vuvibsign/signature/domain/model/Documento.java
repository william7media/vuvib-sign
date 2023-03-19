package vuvibsign.signature.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @NotBlank
    private Long id;
    @NotBlank
    private String codigo;
    @NotBlank
    private String tipo;
    @NotBlank
    private String descripcion;
    @NotBlank
    private String ruta;
    private String numero;
    @NotBlank
    private String fechaCreacion;
    private Long idFirma;

    public Optional<Documento> toOptional() {
        return Optional.of(this);
    }

}
