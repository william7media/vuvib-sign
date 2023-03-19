package vuvibsign.security.application.delivery;

import org.springframework.core.io.Resource;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.security.infrastructure.delivery.payload.SaveKeystoreCommand;

import java.util.Optional;

public interface StorageKeystoreUseCase {
    //    void init();
    Optional<Keystore> findByUsername(String username);

    Optional<Resource> load(String filename);

    Optional<Keystore> save(SaveKeystoreCommand command);

    void delete(Keystore keystore);

}
