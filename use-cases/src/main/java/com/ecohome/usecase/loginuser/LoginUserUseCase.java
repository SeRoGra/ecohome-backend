package com.ecohome.usecase.loginuser;

import com.ecohome.domain.model.User;
import com.ecohome.domain.model.exceptions.UnauthorizedException;
import com.ecohome.domain.model.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public Mono<LoginResponse> execute(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Credenciales inválidas")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                        return Mono.error(new UnauthorizedException("Credenciales inválidas"));
                    }
                    String token = tokenGenerator.generateToken(user);
                    return Mono.just(new LoginResponse(token, user.getRole(), user.getEmail()));
                });
    }

    // Puertos internos — implementados en entry-point/infrastructure
    public interface PasswordEncoder {
        boolean matches(String rawPassword, String encodedPassword);
    }

    public interface TokenGenerator {
        String generateToken(User user);
    }

    public record LoginResponse(String token, String role, String email) {}
}
