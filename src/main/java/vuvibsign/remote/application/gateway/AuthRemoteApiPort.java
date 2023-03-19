package vuvibsign.remote.application.gateway;

import vuvibsign.remote.domain.model.Usuario;
import vuvibsign.remote.infrastructure.gateway.api.payload.LoginRequest;

import java.util.Optional;

public interface AuthRemoteApiPort {

    Optional<Usuario> login(LoginRequest request);

}
