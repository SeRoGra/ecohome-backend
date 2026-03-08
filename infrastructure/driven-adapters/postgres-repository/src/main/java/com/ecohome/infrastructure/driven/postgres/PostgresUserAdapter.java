package com.ecohome.infrastructure.driven.postgres;

import com.ecohome.domain.model.User;
import com.ecohome.domain.model.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostgresUserAdapter implements UserRepository {

    private final UserR2dbcRepository r2dbcRepository;

    @Override
    public Mono<User> findByEmail(String email) {
        return r2dbcRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return r2dbcRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entity.setId(null); // dejar que PostgreSQL genere el UUID
        entity.setCreatedAt(LocalDateTime.now());
        return r2dbcRepository.save(entity)
                .map(UserMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return r2dbcRepository.existsByEmail(email);
    }
}
