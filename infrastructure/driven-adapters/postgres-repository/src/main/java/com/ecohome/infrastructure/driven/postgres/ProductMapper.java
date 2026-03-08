package com.ecohome.infrastructure.driven.postgres;

import com.ecohome.domain.model.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static Product toDomain(ProductEntity e) {
        return Product.builder()
                .id(e.getId())
                .name(e.getName())
                .price(e.getPrice())
                .stock(e.getStock())
                .available(e.getAvailable())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public static ProductEntity toEntity(Product p) {
        return ProductEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .stock(p.getStock() != null ? p.getStock() : 0)
                .available(p.getAvailable() != null ? p.getAvailable() : true)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
