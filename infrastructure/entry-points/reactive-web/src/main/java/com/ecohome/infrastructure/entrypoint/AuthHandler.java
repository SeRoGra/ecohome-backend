package com.ecohome.infrastructure.entrypoint;

import com.ecohome.domain.model.User;
import com.ecohome.domain.model.exceptions.EmailAlreadyExistsException;
import com.ecohome.domain.model.exceptions.UnauthorizedException;
import com.ecohome.usecase.loginuser.LoginUserUseCase;
import com.ecohome.usecase.registeruser.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    // POST /api/auth/signup
    public Mono<ServerResponse> signup(ServerRequest request) {
        return request.bodyToMono(SignupRequest.class)
                .flatMap(body -> {
                    User user = User.builder()
                            .username(body.username())
                            .email(body.email())
                            .passwordHash(body.password())   // se hashea en el use-case
                            .role(body.role() != null ? body.role().toUpperCase() : "CLIENT")
                            .build();
                    return registerUserUseCase.execute(user);
                })
                .flatMap(user -> ServerResponse.status(201).bodyValue(Map.of(
                        "id",    user.getId(),
                        "email", user.getEmail(),
                        "role",  user.getRole()
                )))
                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        ServerResponse.status(409).bodyValue(Map.of("error", e.getMessage())))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage())));
    }

    // POST /api/auth/login
    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(body -> loginUserUseCase.execute(body.email(), body.password()))
                .flatMap(resp -> ServerResponse.ok().bodyValue(Map.of(
                        "token", resp.token(),
                        "role",  resp.role(),
                        "email", resp.email()
                )))
                .onErrorResume(UnauthorizedException.class, e ->
                        ServerResponse.status(401).bodyValue(Map.of("error", e.getMessage())));
    }

    record SignupRequest(String username, String email, String password, String role) {}
    record LoginRequest(String email, String password) {}
}
