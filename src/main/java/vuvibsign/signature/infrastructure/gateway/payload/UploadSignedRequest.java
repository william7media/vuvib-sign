package vuvibsign.signature.infrastructure.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSignedRequest {

    @NonNull
    private String idSolicitud;
    @NonNull
    private String subFolder;
    private String fileName;
    @NonNull
    private MultipartFile archivo;

    public Optional<UploadSignedRequest> toOptional() {
        return Optional.of(this);
    }

    public MultipartBodyBuilder getMultipartBodyBuilder() throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("idSolicitud", idSolicitud);
        builder.part("subFolder", subFolder);
        builder.part("fileName", fileName);
        builder.part("archivo", archivo.getResource());
        return builder;
    }

}

