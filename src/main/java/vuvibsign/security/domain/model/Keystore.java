package vuvibsign.security.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keystore {

    private Long id;
    @NotBlank
    @Size(max = 50)
    private String username;
    private String keystoreName;
    @JsonIgnore
    private String keystorePass;
    private String keystoreUrl;

}