package vuvibsign.signature.application.gateway;

import org.springframework.core.io.InputStreamResource;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.gateway.payload.UploadSignedRequest;

import java.io.IOException;
import java.util.Optional;

public interface DocumentoApiPort {

    Optional<Documento> uploadSigned(UploadSignedRequest requestPayload) throws IOException;

    Optional<InputStreamResource> downloadResource(String filename);

}
