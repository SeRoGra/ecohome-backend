package com.ecohome.domain.model.gateways;

import com.ecohome.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository {
    Flux<Product> findAll();
    Mono<Product> findById(UUID id);
    Mono<Product> save(Product product);
    Mono<Product> update(UUID id, Product product);
    Mono<Void>    deleteById(UUID id);
}
