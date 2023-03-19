package vuvibsign.security.infrastructure.gateway.persistence.mapper;

import org.mapstruct.*;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.security.infrastructure.gateway.persistence.entity.KeystoreEntity;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface KeystoreEntityMapper {

    KeystoreEntity toEntity(Keystore keystore);

    Keystore toObjectDomain(KeystoreEntity keystoreEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    KeystoreEntity partialUpdate(Keystore keystore, @MappingTarget KeystoreEntity keystoreEntity);

}