package vuvibsign.signature.infrastructure.delivery.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vuvibsign.signature.domain.model.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignDocumentCommand {
    private String username;
    private String idPerfil;
    private String idDocumento;
    private Position position;
}
