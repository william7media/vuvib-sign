package vuvibsign.remote.infrastructure.gateway.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vuvibsign.remote.application.gateway.AuthRemoteApiPort;
import vuvibsign.remote.domain.model.Usuario;
import vuvibsign.remote.infrastructure.gateway.api.payload.LoginRequest;
import vuvibsign.remote.infrastructure.gateway.api.payload.LoginResponse;
import vuvibsign.remote.infrastructure.gateway.mapper.UsuarioMapper;

import java.util.Optional;

@Component
public class AuthRemoteApiAdapter implements AuthRemoteApiPort {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    @Value("${api.usuarios.url}")
    private String baseUrl;
    private final UsuarioMapper usuarioMapper;

    public AuthRemoteApiAdapter(WebClient.Builder webClientBuilder,
                                CircuitBreakerFactory<?, ?> factory,
                                UsuarioMapper usuarioMapper) {
        this.webClient = webClientBuilder.build();
        this.circuitBreaker = factory.create("cb-usuarios");
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Optional<Usuario> login(LoginRequest request) {
        String uriResource =
                String.format("/autenticar?correo=%s&clave=%s", request.getEmail(), request.getPassword());

        Mono<LoginResponse> mono =
                circuitBreaker.run(
                        () -> webClient
                                .get()
                                .uri(baseUrl + uriResource)
                                .retrieve()
                                .bodyToMono(LoginResponse.class),
                        Mono::error);

        return usuarioMapper.toObjectDomain(mono.block()).toOptional();
    }

}

