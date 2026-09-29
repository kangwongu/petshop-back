package com.ddungyomi.petshop.domain.product.application.port.out;

import java.util.List;

import com.ddungyomi.petshop.domain.product.domain.Product;

public interface SaveProductPort {

    List<Product> saveList(List<Product> productList);
}
