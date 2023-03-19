package vuvibsign.remote.infrastructure.gateway.api.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

//    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
