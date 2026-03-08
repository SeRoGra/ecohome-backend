package com.ecohome.infrastructure.driven.postgres;

import com.ecohome.domain.model.User;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserEntity e) {
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .role(e.getRole())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static UserEntity toEntity(User u) {
        return UserEntity.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .passwordHash(u.getPasswordHash())
                .role(u.getRole())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
