package vuvibsign.signature.infrastructure.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmaResponse {

    private Long id;
    private Long solicitudId;
    private String estadoDetalleFirma;
    private Date fechaHoraFirma;
    private Long firmaTareaId;
    private String firmaTareaNombreTarea;
    private Long idDocumento;
    private String tipoDocumento;
    private String descripcionDocumento;
    private String rutaDocumento;
    private Long idPerfilUsuario;
    private String perfilUsuarioNombreUsuario;
    private String perfilUsuarioCargoUsuario;

}
