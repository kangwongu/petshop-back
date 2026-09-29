package com.ddungyomi.petshop.domain.product.application.command.res;

import com.ddungyomi.petshop.domain.product.domain.Product;

public record ProductResCommand(
        Integer seq,
        Integer categoryId,
        String categoryName,
        String name,
        Long price,
        String imageUrl,
        Long createEpoch
) {
    public static ProductResCommand from(Product product) {
        return new ProductResCommand(
                product.seq(),
                product.category().code(),
                product.category().label(),
                product.name(),
                product.price(),
                product.imageUrl(),
                product.createEpoch()
        );
    }
}
