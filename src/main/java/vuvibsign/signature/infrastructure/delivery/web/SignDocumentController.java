package vuvibsign.signature.infrastructure.delivery.web;

import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuvibsign.signature.application.delivery.SignDocumentUseCase;
import vuvibsign.signature.domain.model.Position;
import vuvibsign.signature.infrastructure.delivery.payload.SignDocumentCommand;
import vuvibsign.shared.exception.ResourceNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/signer/api/v1/esign")
public class SignDocumentController {

    private final SignDocumentUseCase signDocumentUseCase;

    public SignDocumentController(SignDocumentUseCase signDocumentUseCase) {
        this.signDocumentUseCase = signDocumentUseCase;
    }

    @PostMapping(value = "/pdf1",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> signDocument(
            @RequestBody SignDocumentCommand command) {
        try {
            String uri =
                    signDocumentUseCase
                            .signDocument(
                                    command.getUsername(),
                                    command.getIdPerfil(),
                                    command.getIdDocumento(),
                                    command.getPosition());
            FileInputStream fileInputStream = new FileInputStream(uri);
            return ResponseEntity.ok(new InputStreamResource(fileInputStream));
        } catch (IOException e) {
            throw new ResourceNotFoundException
                    (String.format("Error al firmar el documento: %s", e.getMessage()));
        }
    }

    @PostMapping(value = "/pdf2",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> signDocument(
            @RequestPart(value = "keystoreFile") MultipartFile keystoreFile,
            @RequestPart(value = "keystorePass") String keystorePass,
            @RequestPart(value = "documentFile") MultipartFile documentFile,
            @RequestPart(value = "position") Position position) {

        try {
            String uri =
                    signDocumentUseCase
                            .signDocument(
                                    keystoreFile.getBytes(),
                                    keystorePass,
                                    documentFile.getBytes(),
                                    position);
            Resource fileResource = new FileUrlResource(uri);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment;filename='%s'", fileResource.getFilename()))
                    .body(fileResource);
        } catch (IOException e) {
            throw new ResourceNotFoundException
                    (String.format("Error al firmar el documento: %s", e.getMessage()));
        }
    }

}
