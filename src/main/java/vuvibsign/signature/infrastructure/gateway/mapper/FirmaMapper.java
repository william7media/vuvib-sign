package vuvibsign.signature.infrastructure.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vuvibsign.signature.domain.model.FirmaInfo;
import vuvibsign.signature.infrastructure.gateway.payload.FirmaResponse;

@Mapper(componentModel = "spring")
public interface FirmaMapper {

    @Mapping(target = "solicitudId", source = "idSolicitud")
    @Mapping(target = "estadoDetalleFirma", source = "estado")
    @Mapping(target = "fechaHoraFirma", source = "fechaHora")
    @Mapping(target = "firmaTareaId", source = "idTarea")
    @Mapping(target = "firmaTareaNombreTarea", source = "nombreTarea")
    @Mapping(target = "perfilUsuarioNombreUsuario", source = "nombreUsuario")
    @Mapping(target = "perfilUsuarioCargoUsuario", source = "cargoUsuario")
    FirmaResponse toPayload(FirmaInfo firmaInfo);

    @Mapping(source = "solicitudId", target = "idSolicitud")
    @Mapping(source = "estadoDetalleFirma", target = "estado")
    @Mapping(source = "fechaHoraFirma", target = "fechaHora")
    @Mapping(source = "firmaTareaId", target = "idTarea")
    @Mapping(source = "firmaTareaNombreTarea", target = "nombreTarea")
    @Mapping(source = "perfilUsuarioNombreUsuario", target = "nombreUsuario")
    @Mapping(source = "perfilUsuarioCargoUsuario", target = "cargoUsuario")
    FirmaInfo toObjectDomain(FirmaResponse firmaResponse);

}
