package vuvibsign.signature.infrastructure.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vuvibsign.signature.application.gateway.FirmaApiPort;
import vuvibsign.signature.domain.model.FirmaInfo;
import vuvibsign.signature.infrastructure.gateway.mapper.FirmaMapper;
import vuvibsign.signature.infrastructure.gateway.payload.FirmaResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FirmaApiAdapter implements FirmaApiPort {

    public static final String OBJECT_LIST_NOT_FOUND = "Lista de documentos firma no encontrada!";
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    @Value("${api.firma.url}")
    private String baseUrl;
    private final FirmaMapper mapper;

    public FirmaApiAdapter(WebClient.Builder webClientBuilder,
                           CircuitBreakerFactory<?, ?> factory,
                           FirmaMapper mapper) {
        this.webClient = webClientBuilder.build();
        this.circuitBreaker = factory.create("cb-firma");
        this.mapper = mapper;
    }

    private List<FirmaInfo> getFirmas(String uriResource) {
        Flux<FirmaResponse> firmaResponseFlux =
                circuitBreaker.run(
                        () -> webClient
                                .get()
                                .uri(uriResource)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToFlux(FirmaResponse.class),
                        Flux::error);

        return firmaResponseFlux
                .collectList()
                .blockOptional()
                .orElseThrow(() -> new RuntimeException(OBJECT_LIST_NOT_FOUND))
                .stream()
                .map(mapper::toObjectDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FirmaInfo> findByIdPerfil(Long idPerfil) {
        String uriResource = String.format("%s/perfilusuario?idPerfilUsuario=%s", baseUrl, idPerfil);

        return getFirmas(uriResource);
    }

    @Override
    public List<FirmaInfo> findByIdUsuario(Long idUsuario) {
        String uriResource = String.format("%s/usuario?idUsuario=%s", baseUrl, idUsuario);

        return getFirmas(uriResource);
    }

    @Override
    public Optional<FirmaInfo> changeToSignedStatus(String idDetalleFirma) {
        String uriResource =
                String.format("%s/firmado?idDetalleFirma=%s", baseUrl, idDetalleFirma);
        Mono<FirmaResponse> mono =
                circuitBreaker.run(
                        () -> webClient
                                .post()
                                .uri(uriResource)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(FirmaResponse.class),
                        Mono::error);

        return mapper.toObjectDomain(mono.block()).toOptional();
    }

}
