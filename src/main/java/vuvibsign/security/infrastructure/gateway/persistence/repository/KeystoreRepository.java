package vuvibsign.security.infrastructure.gateway.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import vuvibsign.security.infrastructure.gateway.persistence.entity.KeystoreEntity;

import java.util.Optional;

@Repository
public interface KeystoreRepository extends JpaRepository<KeystoreEntity, Long> {

    Optional<KeystoreEntity> findByUsernameIgnoreCase(@NonNull String username);

}