package com.ecohome.domain.model.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super("Producto no encontrado con id: " + id);
    }
}
