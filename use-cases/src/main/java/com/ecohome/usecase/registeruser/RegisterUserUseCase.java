package com.ecohome.usecase.registeruser;

import com.ecohome.domain.model.User;
import com.ecohome.domain.model.exceptions.EmailAlreadyExistsException;
import com.ecohome.domain.model.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> execute(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException("El email es obligatorio"));
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            return Mono.error(new IllegalArgumentException("La contraseña es obligatoria"));
        }

        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailAlreadyExistsException(user.getEmail()));
                    }
                    String hashed = passwordEncoder.encode(user.getPasswordHash());
                    User toSave = User.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .passwordHash(hashed)
                            .role(user.getRole() != null ? user.getRole() : "CLIENT")
                            .build();
                    return userRepository.save(toSave);
                });
    }

    // Puerto interno para no acoplar el use-case a Spring Security
    public interface PasswordEncoder {
        String encode(String rawPassword);
    }
}
