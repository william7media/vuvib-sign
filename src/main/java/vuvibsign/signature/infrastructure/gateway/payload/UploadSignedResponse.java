package vuvibsign.signature.infrastructure.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSignedResponse {

    private Long id;
    private String codigoDocumento;
    private String tipoDocumento;
    private String descripcionDocumento;
    private String rutaDocumento;
    private String numeroDocumento;
    private String fechaCreacion;

}
