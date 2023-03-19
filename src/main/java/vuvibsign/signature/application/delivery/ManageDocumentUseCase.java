package vuvibsign.signature.application.delivery;

import org.springframework.core.io.InputStreamResource;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.delivery.payload.UploadSignedCommand;

import java.io.IOException;
import java.util.Optional;

public interface ManageDocumentUseCase {

    Optional<Documento> uploadSigned(UploadSignedCommand command) throws IOException;

    Optional<InputStreamResource> downloadResource(String fileName);

}
