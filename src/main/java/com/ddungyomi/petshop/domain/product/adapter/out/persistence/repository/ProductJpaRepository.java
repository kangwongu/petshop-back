package com.ddungyomi.petshop.domain.product.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddungyomi.petshop.domain.product.adapter.out.persistence.entity.ProductJpaEntity;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Integer> {

    List<ProductJpaEntity> findListByCategoryCode(Integer categoryCode);
}
