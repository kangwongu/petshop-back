package com.ddungyomi.petshop.domain.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddungyomi.petshop.domain.product.application.command.req.GetProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;
import com.ddungyomi.petshop.domain.product.application.port.in.GetProductUseCase;
import com.ddungyomi.petshop.domain.product.application.port.out.GetProductPort;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductReadService implements GetProductUseCase {

    private final GetProductPort getProductPort;

    @Override
    public ProductResCommand findBySeq(Integer seq) {
        return ProductResCommand.from(getProductPort.findBySeq(seq));
    }

    @Override
    public List<ProductResCommand> findList(GetProductReqCommand command) {
        return getProductPort.findList(command.categoryCode()).stream()
                .map(ProductResCommand::from)
                .toList();
    }
}
