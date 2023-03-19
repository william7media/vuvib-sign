package vuvibsign.signature.infrastructure.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.gateway.payload.UploadSignedResponse;

@Mapper(componentModel = "spring")
public interface DocumentoMapper {

    @Mapping(source = "codigoDocumento", target = "codigo")
    @Mapping(source = "tipoDocumento", target = "tipo")
    @Mapping(source = "descripcionDocumento", target = "descripcion")
    @Mapping(source = "rutaDocumento", target = "ruta")
    @Mapping(source = "numeroDocumento", target = "numero")
    Documento toObjectDomain(UploadSignedResponse uploadSignedResponse);

}
