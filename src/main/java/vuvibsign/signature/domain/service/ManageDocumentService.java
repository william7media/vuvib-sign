package vuvibsign.signature.domain.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import vuvibsign.signature.application.delivery.ManageDocumentUseCase;
import vuvibsign.signature.application.gateway.DocumentoApiPort;
import vuvibsign.signature.application.gateway.FirmaApiPort;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.delivery.payload.UploadSignedCommand;
import vuvibsign.signature.infrastructure.gateway.payload.UploadSignedRequest;

import java.io.IOException;
import java.util.Optional;

@Service
public class ManageDocumentService implements ManageDocumentUseCase {

    private final DocumentoApiPort documentoApiPort;
    private final FirmaApiPort firmaApiPort;

    public ManageDocumentService(DocumentoApiPort documentoApiPort,
                                 FirmaApiPort firmaApiPort) {
        this.documentoApiPort = documentoApiPort;
        this.firmaApiPort = firmaApiPort;
    }

    @Override
    public Optional<Documento> uploadSigned(UploadSignedCommand command) throws IOException {

        UploadSignedRequest payload =
                UploadSignedRequest.builder()
                        .idSolicitud(command.getIdSolicitud().toString())
                        .subFolder(command.getSubFolder())
                        .fileName(command.getFileName())
                        .archivo(command.getArchivo())
                        .build();
        firmaApiPort.changeToSignedStatus(command.getIdFirma().toString())
                .orElseThrow(() -> new RuntimeException("No se pudo cambiar el estado del documento firmado."));

        return documentoApiPort.uploadSigned(payload);
    }

    @Override
    public Optional<InputStreamResource> downloadResource(@NonNull String fileName) {
        return documentoApiPort.downloadResource(fileName);
    }

}
