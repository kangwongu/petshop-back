package com.ddungyomi.petshop.domain.product.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products", indexes = @Index(name = "idx_products_category_code", columnList = "category_code"))
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "category_code", nullable = false)
    private Integer categoryCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Long createEpoch;

    @Column(nullable = false)
    private Long updateEpoch;

    public static ProductJpaEntity create(
            Integer categoryCode, String name, Long price, String imageUrl,
            Long createEpoch, Long updateEpoch
    ) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.categoryCode = categoryCode;
        entity.name = name;
        entity.price = price;
        entity.imageUrl = imageUrl;
        entity.createEpoch = createEpoch;
        entity.updateEpoch = updateEpoch;
        return entity;
    }
}
