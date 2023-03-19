package vuvibsign.security.infrastructure.delivery.payload;

import lombok.Builder;
import lombok.Data;
import vuvibsign.security.domain.model.UserAuth;

@Data
@Builder
public class AuthResponse {

    private String authCookie;
    private UserAuth userAuth;

}
