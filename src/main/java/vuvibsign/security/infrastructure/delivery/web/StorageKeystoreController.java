package vuvibsign.security.infrastructure.delivery.web;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuvibsign.security.application.delivery.StorageKeystoreUseCase;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.security.infrastructure.delivery.payload.SaveKeystoreCommand;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/signer/api/v1/keystore")
public class StorageKeystoreController {

    public static final String KEYSTORE_NOT_FOUND = "Keystore no encontrado!";
    public static final String KEYSTORE_NOT_SAVED = "No se pudo guardar el Keystore!";
    private final StorageKeystoreUseCase useCase;

    public StorageKeystoreController(StorageKeystoreUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/find")
    public ResponseEntity<Keystore> findByUsername(@RequestParam String username) {

        Keystore keystoreFound =
                useCase.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_FOUND));

        return ResponseEntity.ok(keystoreFound);
    }

    @GetMapping("/load")
    public ResponseEntity<Resource> load(@RequestParam String filename) {
        Resource resource =
                useCase.load(filename).orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_FOUND));
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Keystore> save(
            @RequestPart MultipartFile keystoreFile,
            @RequestPart String keystorePass,
            @RequestPart String username) {

        SaveKeystoreCommand command =
                SaveKeystoreCommand.builder()
                        .keystoreFile(keystoreFile)
                        .keystorePass(keystorePass)
                        .username(username)
                        .build();
        Keystore keystoreSaved = useCase.save(command)
                .orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_SAVED));

        return ResponseEntity.ok(keystoreSaved);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String username) {

        Keystore keystoreFound =
                useCase.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_FOUND));
        useCase.delete(keystoreFound);

        return ResponseEntity.ok().build();
    }

}
