package vuvibsign.signature.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmaInfo {

    public static final String UPLOAD_DOCS = "upload-docs/";
    @NonNull
    private Long id;
    @NonNull
    private Long idSolicitud;
    @NonNull
    private String estado;
    private Date fechaHora;
    @NonNull
    private Long idTarea;
    @NonNull
    private String nombreTarea;
    @NonNull
    private Long idDocumento;
    @NonNull
    private String tipoDocumento;
    @NonNull
    private String descripcionDocumento;
    @NonNull
    private String rutaDocumento;
    @NonNull
    private Long idPerfilUsuario;
    @NonNull
    private String nombreUsuario;

    private String cargoUsuario;

    /* Devuelve la ruta del documento sin el folder raíz */
    public String getRutaDocumento() {
        if (rutaDocumento.contains(UPLOAD_DOCS))
            return this.rutaDocumento.substring(UPLOAD_DOCS.length());
        else
            return this.rutaDocumento;
    }

    public Optional<FirmaInfo> toOptional() {
        return Optional.of(this);
    }

}
