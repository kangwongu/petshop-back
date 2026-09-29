package com.ddungyomi.petshop.domain.product.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddungyomi.petshop.common.exception.NotFoundException;
import com.ddungyomi.petshop.domain.product.adapter.out.persistence.entity.ProductJpaEntity;
import com.ddungyomi.petshop.domain.product.adapter.out.persistence.mapper.ProductMapper;
import com.ddungyomi.petshop.domain.product.adapter.out.persistence.repository.ProductJpaRepository;
import com.ddungyomi.petshop.domain.product.application.port.out.GetProductPort;
import com.ddungyomi.petshop.domain.product.application.port.out.SaveProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements GetProductPort, SaveProductPort {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product findBySeq(Integer seq) {
        return productJpaRepository.findById(seq)
                .map(ProductMapper::mapToDomain)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. seq=" + seq));
    }

    @Override
    public List<Product> findList(Integer categoryCode) {
        List<ProductJpaEntity> entityList = categoryCode == null
                ? productJpaRepository.findAll()
                : productJpaRepository.findListByCategoryCode(categoryCode);

        return entityList.stream()
                .map(ProductMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Product> saveList(List<Product> productList) {
        List<ProductJpaEntity> entityList = productList.stream()
                .map(ProductMapper::mapToJpaEntity)
                .toList();

        return productJpaRepository.saveAll(entityList).stream()
                .map(ProductMapper::mapToDomain)
                .toList();
    }
}
