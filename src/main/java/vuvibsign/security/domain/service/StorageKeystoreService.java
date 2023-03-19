package vuvibsign.security.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vuvibsign.security.application.delivery.StorageKeystoreUseCase;
import vuvibsign.security.application.gateway.KeystoreRepositoryPort;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.security.infrastructure.delivery.payload.SaveKeystoreCommand;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
public class StorageKeystoreService implements StorageKeystoreUseCase {

    public static final String FILE_NAME_ALREADY_EXISTS = "Ya existe un archivo con el mismo nombre.";
    public static final String INITIALIZE_FOLDER_ERROR = "No se pudo inicializar directorio para carga!";
    public static final String MALFORMED_URL_ERROR = "URL Malformada. ";
    public static final String DELETE_FILE_ERROR = "No se pudo eliminar el archivo. Está siendo utilizado en otro proceso.";
    public static final String READ_FILE_ERROR = "No se pudo leer o no existe el archivo!";
    public static final String KEYSTORE_NOT_FOUND = "Keystore no encontrado.";
    public static final String COPY_FILE_ERROR = "Error al copiar el archivo PFX.";
    private final Path root = Paths.get(System.getProperty("java.io.tmpdir"), "keystores");
    private final KeystoreRepositoryPort port;

    public StorageKeystoreService(KeystoreRepositoryPort port) {
        this.port = port;
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException(INITIALIZE_FOLDER_ERROR);
        }
    }

    @Override
    public Optional<Keystore> findByUsername(String username) {
        return port.findByUsername(username);
    }

    @Override
    public Optional<Resource> load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return Optional.of(resource);
            } else {
                throw new RuntimeException(READ_FILE_ERROR);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(MALFORMED_URL_ERROR + e.getMessage());
        }
    }

    @Override
    public Optional<Keystore> save(@NonNull SaveKeystoreCommand command) {

        MultipartFile file = command.getKeystoreFile();
        Path path = root.resolve(file.getOriginalFilename());
        Keystore keystoreFound =
                port.findByUsername(command.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException(KEYSTORE_NOT_FOUND));

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path);

            keystoreFound.setKeystoreName(file.getOriginalFilename());
            keystoreFound.setKeystoreUrl(path.toString());
            keystoreFound.setKeystorePass(command.getKeystorePass());

            return port.save(keystoreFound);
        } catch (FileAlreadyExistsException e) {
            log.warn(FILE_NAME_ALREADY_EXISTS);
            this.delete(keystoreFound);
            return this.save(command);
//            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(COPY_FILE_ERROR, e);
        }
    }

    @Override
    public void delete(Keystore keystore) {
        try {
            Path path = root.resolve(keystore.getKeystoreUrl());
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                File f = new File(path.toUri());
                if (!f.delete())
                    throw new RuntimeException(DELETE_FILE_ERROR);
                keystore.setKeystoreName(null);
                keystore.setKeystorePass(null);
                keystore.setKeystoreUrl(null);
                port.save(keystore);
            } else {
                throw new RuntimeException(READ_FILE_ERROR);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(MALFORMED_URL_ERROR + e.getMessage());
        }
    }
}
