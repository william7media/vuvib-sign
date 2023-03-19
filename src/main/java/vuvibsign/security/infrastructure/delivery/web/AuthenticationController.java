package vuvibsign.security.infrastructure.delivery.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuvibsign.security.application.delivery.AuthenticationUseCase;
import vuvibsign.security.infrastructure.delivery.payload.AuthRequest;
import vuvibsign.security.infrastructure.delivery.payload.AuthResponse;
import vuvibsign.security.infrastructure.delivery.payload.MessageResponse;
import vuvibsign.shared.exception.ResourceNotFoundException;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/signer/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthenticationController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response =
                authenticationUseCase.signin(authRequest)
                        .orElseThrow(() -> new ResourceNotFoundException("Error de autenticación de usuario."));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getAuthCookie())
                .body(response.getUserAuth());
    }

    @PostMapping("/signinup")
    public ResponseEntity<?> signinup(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response =
                authenticationUseCase.signinup(authRequest)
                        .orElseThrow(() -> new ResourceNotFoundException("Error de autenticación de usuario."));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getAuthCookie())
                .body(response.getUserAuth());
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signUpRequest) {
//        try {
//            authenticationUseCase.signup(signUpRequest);
//            return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
//        } catch (SignupException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout() {
        AuthResponse response =
                authenticationUseCase.signout()
                        .orElseThrow(() -> new ResourceNotFoundException("Error al finalizar la sesión de usuario."));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getAuthCookie())
                .body(new MessageResponse("Usuario desconectado!"));
    }
}
