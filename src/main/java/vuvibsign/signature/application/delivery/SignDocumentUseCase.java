package vuvibsign.signature.application.delivery;

import vuvibsign.signature.domain.model.Position;

import java.io.IOException;

public interface SignDocumentUseCase {

    String signDocument(String username,
                        String idPerfil,
                        String idDocumento,
                        Position position)
            throws IOException;

    String signDocument(byte[] keystoreFile,
                        String keystorePass,
                        byte[] documentFile,
                        Position position)
            throws IOException;

}
