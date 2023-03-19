package vuvibsign.security.application.delivery;

import vuvibsign.security.infrastructure.delivery.payload.AuthRequest;
import vuvibsign.security.infrastructure.delivery.payload.AuthResponse;

import java.util.Optional;

public interface AuthenticationUseCase {

    Optional<AuthResponse> signin(AuthRequest loginRequest);

    Optional<AuthResponse> signinup(AuthRequest loginRequest);

    Optional<AuthResponse> signout();

}
