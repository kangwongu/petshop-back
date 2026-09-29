package com.ddungyomi.petshop.domain.product.application.port.in;

import java.util.List;

import com.ddungyomi.petshop.domain.product.application.command.req.GetProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;

public interface GetProductUseCase {

    ProductResCommand findBySeq(Integer seq);

    List<ProductResCommand> findList(GetProductReqCommand command);
}
