package vuvibsign.security.infrastructure.gateway.persistence;

import org.springframework.stereotype.Component;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.security.infrastructure.gateway.persistence.mapper.KeystoreEntityMapper;
import vuvibsign.security.infrastructure.gateway.persistence.repository.KeystoreRepository;

import java.util.Optional;

@Component
public class KeystoreAdapter implements vuvibsign.security.application.gateway.KeystoreRepositoryPort {

    private final KeystoreRepository repository;
    private final KeystoreEntityMapper mapper;

    public KeystoreAdapter(KeystoreRepository repository,
                           KeystoreEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Keystore> findByUsername(String username){
        return repository.findByUsernameIgnoreCase(username).map(mapper::toObjectDomain);
    }

    @Override
    public Optional<Keystore> save(Keystore keystore){
        return Optional.ofNullable(mapper.toObjectDomain(repository.save(mapper.toEntity(keystore))));
    }

}
