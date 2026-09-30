package com.ddungyomi.petshop.domain.order.adapter.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddungyomi.petshop.domain.order.adapter.in.web.dto.req.CreateOrderReq;
import com.ddungyomi.petshop.domain.order.adapter.in.web.dto.res.OrderRes;
import com.ddungyomi.petshop.domain.order.application.port.in.CreateOrderUseCase;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    @Operation(summary = "주문 생성", description = "상품 목록으로 주문을 생성하고 총액을 계산한다. (FR-03)")
    @PostMapping
    public OrderRes create(@Valid @RequestBody CreateOrderReq req) {
        return OrderRes.fromCommand(createOrderUseCase.create(req.toCommand()));
    }
}
