package vuvibsign.security.infrastructure.delivery.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveKeystoreCommand {

    @NonNull
    private MultipartFile keystoreFile;
    @NonNull
    private String keystorePass;
    @NonNull
    private String username;

}
