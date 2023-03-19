package vuvibsign.signature.infrastructure.delivery.web;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuvibsign.signature.application.delivery.ManageDocumentUseCase;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.delivery.payload.UploadSignedCommand;

import javax.websocket.server.PathParam;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/signer/api/v1/documentos")
public class ManageDocumentController {

    private final ManageDocumentUseCase useCase;

    public ManageDocumentController(ManageDocumentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadResource(@PathParam("filename") String filename) {

        Resource fileResource =
                useCase.downloadResource(filename)
                        .orElseThrow(() -> new RuntimeException(String.format("Documento no encontrado: %s", filename)));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment;filename='%s'", fileResource.getFilename()))
                .body(fileResource);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Documento> uploadSigned(
            @RequestParam Long idSolicitud,
            @RequestParam String subFolder,
            @RequestParam String fileName,
            @RequestParam MultipartFile archivo,
            @RequestParam Long idFirma) {

        Path tempPath;
        try {
//            tempPath = Files.createTempFile(fileName,null);
//            archivo.transferTo(tempPath);

            UploadSignedCommand command = UploadSignedCommand.builder()
                    .idSolicitud(idSolicitud)
                    .subFolder(subFolder)
                    .fileName(fileName)
                    .archivo(archivo)
                    .idFirma(idFirma)
                    .build();
            Documento documentoUploaded =
                    useCase.uploadSigned(command)
                            .orElseThrow(() -> new RuntimeException("No se pudo enviar el archivo!"));

            return ResponseEntity.ok(documentoUploaded);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Archivo no encontrado!", e);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo temporalmente en el servidor!", e);
        }
    }

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Documento> uploadSigned(
//            @RequestParam Long idSolicitud,
//            @RequestParam String subFolder,
//            @RequestParam String fileName,
//            @RequestParam MultipartFile archivo,
//            @RequestParam Long idFirma) {
//
//        Path tempPath;
//        try {
//            tempPath = Files.createTempFile(fileName,null);
//            archivo.transferTo(tempPath);
//
//            SaveSignedCommand command = SaveSignedCommand.builder()
//                    .idSolicitud(idSolicitud)
//                    .subFolder(subFolder)
//                    .fileName(fileName)
//                    .archivo(tempPath.toFile())
//                    .idFirma(idFirma)
//                    .build();
//            Documento documentoUploaded =
//                    useCase.uploadSigned(command)
//                            .orElseThrow(() -> new RuntimeException("No se pudo enviar el archivo!"));
//
//            return ResponseEntity.ok(documentoUploaded);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("Archivo no encontrado!", e);
//        } catch (IOException e) {
//            throw new RuntimeException("No se pudo guardar el archivo temporalmente en el servidor!", e);
//        }
//    }

}
