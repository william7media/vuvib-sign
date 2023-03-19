package vuvibsign.security.infrastructure.gateway.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vuvibsign.security.infrastructure.gateway.persistence.entity.ERole;
import vuvibsign.security.infrastructure.gateway.persistence.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
