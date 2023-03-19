package vuvibsign.security.infrastructure.delivery.payload;

import lombok.Data;

import java.util.List;

@Data
//@Builder
public class JwtResponse {
    private final String type = "Bearer";
    private final List<String> roles;
    private String token;
    private Long id;
    private String username;
    private String email;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}
