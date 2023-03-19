package vuvibsign.security.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAuth {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String accessToken;

}
