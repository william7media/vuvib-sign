package vuvibsign.remote.infrastructure.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vuvibsign.remote.domain.model.Usuario;
import vuvibsign.remote.infrastructure.gateway.api.payload.LoginResponse;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "codigoUsuario", target = "codigo")
    @Mapping(source = "nombreUsuario", target = "nombre")
    @Mapping(source = "telefonoUsuario", target = "telefono")
    @Mapping(source = "correoUsuario", target = "correo")
    @Mapping(source = "cedulaUsuario", target = "cedula")
    @Mapping(source = "pasaporteUsuario", target = "pasaporte")
    @Mapping(source = "usernameUsuario", target = "username")
    @Mapping(source = "estadoUsuario", target = "estado")
    @Mapping(source = "cargoUsuario", target = "cargo")
    Usuario toObjectDomain(LoginResponse loginResponse);

}
