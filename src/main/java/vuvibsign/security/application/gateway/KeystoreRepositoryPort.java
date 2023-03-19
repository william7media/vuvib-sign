package vuvibsign.security.application.gateway;

import vuvibsign.security.domain.model.Keystore;

import java.util.Optional;

public interface KeystoreRepositoryPort {
    Optional<Keystore> findByUsername(String username);

    Optional<Keystore> save(Keystore keystore);
}
