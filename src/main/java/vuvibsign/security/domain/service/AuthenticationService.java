package vuvibsign.security.domain.service;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vuvibsign.remote.application.gateway.AuthRemoteApiPort;
import vuvibsign.remote.domain.model.Usuario;
import vuvibsign.remote.infrastructure.gateway.api.payload.LoginRequest;
import vuvibsign.security.application.delivery.AuthenticationUseCase;
import vuvibsign.security.domain.model.UserAuth;
import vuvibsign.security.infrastructure.delivery.payload.AuthRequest;
import vuvibsign.security.infrastructure.delivery.payload.AuthResponse;
import vuvibsign.security.infrastructure.delivery.payload.SignupRequest;
import vuvibsign.security.infrastructure.gateway.jwt.JwtUtils;
import vuvibsign.security.infrastructure.gateway.jwt.UserDetailsAdapter;
import vuvibsign.security.infrastructure.gateway.persistence.entity.ERole;
import vuvibsign.security.infrastructure.gateway.persistence.entity.Role;
import vuvibsign.security.infrastructure.gateway.persistence.entity.User;
import vuvibsign.security.infrastructure.gateway.persistence.repository.RoleRepository;
import vuvibsign.security.infrastructure.gateway.persistence.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements AuthenticationUseCase {

    public static final String USER_COULD_NOT_BE_AUTHENTICATED = "Usuario no pudo ser autenticado!";
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthRemoteApiPort authRemoteApiPort;

    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, AuthRemoteApiPort authRemoteApiPort) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authRemoteApiPort = authRemoteApiPort;
    }

    @Override
    public Optional<AuthResponse> signin(AuthRequest authRequest) {

        Authentication authentication =
                authenticationManager
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        authRequest.getUsername(),
                                        authRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsAdapter userDetailsAdapter = (UserDetailsAdapter) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetailsAdapter);

        List<String> roles =
                userDetailsAdapter
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
//        return new JwtResponse(jwtCookie.toString(),
//                userDetailsAdapter.getId(),
//                userDetailsAdapter.getUsername(),
//                userDetailsAdapter.getEmail(),
//                roles);

        UserAuth userAuth =
                UserAuth.builder()
                        .id(userDetailsAdapter.getId())
                        .username(userDetailsAdapter.getUsername())
                        .email(userDetailsAdapter.getEmail())
                        .roles(roles)
                        .accessToken(jwtCookie.getValue())
                        .build();

        return Optional.ofNullable(
                AuthResponse
                        .builder()
                        .authCookie(jwtCookie.toString())
                        .userAuth(userAuth)
                        .build());
    }

    @Override
    public Optional<AuthResponse> signinup(AuthRequest authRequest) {
        // Autenticar remotamente
        LoginRequest loginRequest =
                LoginRequest.builder()
                        .username(authRequest.getUsername())
                        .email(authRequest.getEmail())
                        .password(authRequest.getPassword())
                        .build();

        Usuario usuario = authRemoteApiPort.login(loginRequest)
                .orElseThrow(() -> new RuntimeException(USER_COULD_NOT_BE_AUTHENTICATED));

        // Completar requerimiento de autenticación
        AuthRequest newAuthRequest =
                AuthRequest.builder()
                        .id(usuario.getId())
                        .username(usuario.getUsername())
                        .email(usuario.getCorreo())
                        .password(authRequest.getPassword())
                        .build();
        // Registrar usuario
        if (!userRepository.existsByUsername(usuario.getUsername())) {
            SignupRequest signupRequest =
                    SignupRequest.builder()
                            .id(usuario.getId())
                            .username(usuario.getUsername())
                            .password(authRequest.getPassword())
                            .email(usuario.getCorreo())
                            .build();
            this.signup(signupRequest);
        }

        return this.signin(newAuthRequest);
    }

    //@Override
    private void signup(SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user =
                new User(signupRequest.getId(),
                        signupRequest.getUsername(),
                        signupRequest.getEmail(),
                        encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole =
                    roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole =
                                roleRepository.findByName(ERole.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole =
                                roleRepository.findByName(ERole.ROLE_MODERATOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole =
                                roleRepository.findByName(ERole.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public Optional<AuthResponse> signout() {
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        return Optional.ofNullable(
                AuthResponse
                        .builder()
                        .authCookie(jwtCookie.toString())
                        .build());
    }
}
