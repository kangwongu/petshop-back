package com.ddungyomi.petshop.domain.product.domain;

public record Product(
        Integer seq,
        ProductCategory category,
        String name,
        Long price,
        String imageUrl,
        Long createEpoch,
        Long updateEpoch
) {
    public static Product create(ProductCategory category, String name, Long price, String imageUrl) {
        return new Product(
                null, category, name, price, imageUrl,
                System.currentTimeMillis(), System.currentTimeMillis()
        );
    }
}
