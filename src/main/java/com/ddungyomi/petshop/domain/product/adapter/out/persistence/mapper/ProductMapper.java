package com.ddungyomi.petshop.domain.product.adapter.out.persistence.mapper;

import com.ddungyomi.petshop.domain.product.adapter.out.persistence.entity.ProductJpaEntity;
import com.ddungyomi.petshop.domain.product.domain.Product;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

public class ProductMapper {

    public static Product mapToDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getSeq(),
                ProductCategory.fromCode(entity.getCategoryCode()),
                entity.getName(),
                entity.getPrice(),
                entity.getImageUrl(),
                entity.getCreateEpoch(),
                entity.getUpdateEpoch()
        );
    }

    public static ProductJpaEntity mapToJpaEntity(Product product) {
        return ProductJpaEntity.create(
                product.category().code(),
                product.name(),
                product.price(),
                product.imageUrl(),
                product.createEpoch(),
                product.updateEpoch()
        );
    }
}
