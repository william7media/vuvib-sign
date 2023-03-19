package vuvibsign.remote.infrastructure.gateway.api.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long id;
    private String codigoUsuario;
    private String nombreUsuario;
    private String telefonoUsuario;
    private String correoUsuario;
    private String cedulaUsuario;
    private String pasaporteUsuario;
    private String usernameUsuario;
    private String estadoUsuario;
    private String tipo;
    private String cargoUsuario;

}
