package com.ecohome.infrastructure.driven.postgres;

import com.ecohome.domain.model.Product;
import com.ecohome.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostgresProductAdapter implements ProductRepository {

    private final ProductR2dbcRepository r2dbcRepository;

    @Override
    public Flux<Product> findAll() {
        return r2dbcRepository.findAll()
                .map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Product> findById(UUID id) {
        return r2dbcRepository.findById(id)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        entity.setId(null); // dejar que PostgreSQL genere el UUID
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return r2dbcRepository.save(entity)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Product> update(UUID id, Product product) {
        return r2dbcRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    if (product.getStock() != null)     existing.setStock(product.getStock());
                    if (product.getAvailable() != null) existing.setAvailable(product.getAvailable());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return r2dbcRepository.save(existing);
                })
                .map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return r2dbcRepository.deleteById(id);
    }
}
