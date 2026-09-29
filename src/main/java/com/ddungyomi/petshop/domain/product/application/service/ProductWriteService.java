package com.ddungyomi.petshop.domain.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddungyomi.petshop.domain.product.application.command.req.CreateProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;
import com.ddungyomi.petshop.domain.product.application.port.in.CreateProductUseCase;
import com.ddungyomi.petshop.domain.product.application.port.out.SaveProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductWriteService implements CreateProductUseCase {

    private final SaveProductPort saveProductPort;

    @Override
    public List<ProductResCommand> createList(List<CreateProductReqCommand> commandList) {
        List<Product> productList = commandList.stream()
                .map(command -> Product.create(
                        ProductCategory.fromCode(command.categoryCode()),
                        command.name(),
                        command.price(),
                        command.imageUrl()
                ))
                .toList();

        return saveProductPort.saveList(productList).stream()
                .map(ProductResCommand::from)
                .toList();
    }
}
