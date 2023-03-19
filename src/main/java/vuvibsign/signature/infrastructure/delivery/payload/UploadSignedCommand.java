package vuvibsign.signature.infrastructure.delivery.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSignedCommand {
    private Long idSolicitud;
    private String subFolder;
    private String fileName;
    private MultipartFile archivo;
    private Long idFirma;
}
