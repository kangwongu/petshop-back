package com.ddungyomi.petshop.domain.product.application.port.in;

import java.util.List;

import com.ddungyomi.petshop.domain.product.application.command.req.CreateProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;

public interface CreateProductUseCase {

    List<ProductResCommand> createList(List<CreateProductReqCommand> commandList);
}
