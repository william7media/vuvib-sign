package vuvibsign.signature.infrastructure.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vuvibsign.signature.application.gateway.DocumentoApiPort;
import vuvibsign.signature.domain.model.Documento;
import vuvibsign.signature.infrastructure.gateway.mapper.DocumentoMapper;
import vuvibsign.signature.infrastructure.gateway.payload.UploadSignedRequest;
import vuvibsign.signature.infrastructure.gateway.payload.UploadSignedResponse;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class DocumentoApiAdapter implements DocumentoApiPort {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    @Value("${api.documentos.url}")
    private String baseUrl;
    private final DocumentoMapper documentoMapper;

    public DocumentoApiAdapter(WebClient.Builder webClientBuilder,
                               CircuitBreakerFactory<?, ?> factory,
                               DocumentoMapper documentoMapper) {
        this.webClient = webClientBuilder.build();
        this.circuitBreaker = factory.create("cb-documentos");
        this.documentoMapper = documentoMapper;
    }

    @Override
    public Optional<InputStreamResource> downloadResource(@NonNull String filename) {
        String uriResource = String.format("%s/descargar?filename=%s", baseUrl, filename);

        Mono<InputStreamResource> mono =
                circuitBreaker.run(
                        () -> webClient
                                .get()
                                .uri(uriResource)
                                .retrieve()
                                .bodyToMono(InputStreamResource.class),
                        Mono::error);

        return mono.blockOptional();
    }

    @Override
    public Optional<Documento> uploadSigned(UploadSignedRequest requestPayload) {

        String uri =
                Optional.ofNullable(requestPayload.getFileName())
                        .map(fileName -> String.format("%s/cargarnombre", baseUrl))
                        .orElse(String.format("%s/cargar", baseUrl));

        Mono<UploadSignedResponse> mono =
                circuitBreaker.run(
                        () -> {
                            try {
                                return webClient
                                        .post()
                                        .uri(uri)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .body(BodyInserters.fromMultipartData(requestPayload.getMultipartBodyBuilder().build()))
                                        .accept(MediaType.APPLICATION_JSON)
                                        .retrieve()
                                        .bodyToMono(UploadSignedResponse.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        Mono::error);

        UploadSignedResponse response = mono.block();
        log.info(response.toString());

        return documentoMapper.toObjectDomain(response).toOptional();
    }
}
