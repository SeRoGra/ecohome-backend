package com.ecohome.domain.model.gateways;

import com.ecohome.domain.model.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
    Mono<User> findByEmail(String email);
    Mono<User> findById(UUID id);
    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
}
