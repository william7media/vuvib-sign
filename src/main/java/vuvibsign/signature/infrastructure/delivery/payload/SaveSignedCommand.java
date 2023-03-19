package vuvibsign.signature.infrastructure.delivery.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveSignedCommand {
    private Long idSolicitud;
    private String subFolder;
    private String fileName;
    private File archivo;
    private Long idFirma;
}
