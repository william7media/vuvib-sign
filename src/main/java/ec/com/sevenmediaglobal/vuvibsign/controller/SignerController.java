package ec.com.sevenmediaglobal.vuvibsign.controller;

import ec.com.sevenmediaglobal.vuvibsign.conf.SignerConfig;
import ec.com.sevenmediaglobal.vuvibsign.exception.SignerPdfServiceException;
import ec.com.sevenmediaglobal.vuvibsign.service.SignerService;
import net.sf.jsignpdf.SignerOptionsFromCmdLine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping(value = "/api/v1/signer")
public class SignerController {

    private final SignerService signerService;

    public SignerController(SignerService signerService) {
        this.signerService = signerService;
    }

    @PostMapping(value = "/sign")
    public ResponseEntity<String> signDocument(
            @RequestParam(value = "keystoreFile") MultipartFile keystoreFile,
            @RequestParam(value = "keystorePass") String keystorePass,
            @RequestParam(value = "documentFile") MultipartFile documentFile/*,
            @RequestParam(value = "position") Position position*/)
            throws SignerPdfServiceException, IOException {

        SignerOptionsFromCmdLine options = new SignerOptionsFromCmdLine();
        // Directorio de keystore
        Path keystoreFilePath =
                signerService.handleUpload(
                        SignerConfig.KEYSTORE_DIRECTORY,
                        keystoreFile.getBytes(),
                        SignerConfig.PREFIX,
                        SignerConfig.KEYSTORE_SUFFIX);
        options.setKsFile(keystoreFilePath.toAbsolutePath().toString());
        options.setKsPasswd(keystorePass);
        // Directorio de archivos no firmados
        Path unsignedPath =
                signerService.handleUpload(
                        SignerConfig.UNSIGNED_DIRECTORY,
                        documentFile.getBytes(),
                        SignerConfig.PREFIX,
                        SignerConfig.DOCUMENT_SUFFIX);
        options.setFiles(new String[]{unsignedPath.toAbsolutePath().toString()});
        // Directorio de archivos firmados
        Path signedPath = signerService.handleSignedPath(SignerConfig.SIGNED_DIRECTORY);
        options.setOutPath(signedPath.toAbsolutePath().toString());
        // Mas opciones
        options.setKsType(SignerConfig.KEYSTORE_TYPE);
        options.setAppend(SignerConfig.SIGNATURE_APPEND);
        options.setHashAlgorithm(SignerConfig.HASH_ALGORITHM);
        options.setVisible(SignerConfig.SIGNATURE_VISIBLE);
        options.setPage(SignerConfig.PAGE);
        options.setL2TextFontSize(SignerConfig.FONT_SIZE.floatValue());
        //todo: manejar position
        options.setPositionLLX(10f);
        options.setPositionLLY(10f);
        options.setPositionURX(100f);
        options.setPositionURY(70f);

        signerService.signDocument(options);
        return ResponseEntity.ok(unsignedPath.toAbsolutePath().toString());
    }

}
