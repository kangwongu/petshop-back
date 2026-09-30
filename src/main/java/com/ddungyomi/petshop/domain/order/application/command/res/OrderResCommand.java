package com.ddungyomi.petshop.domain.order.application.command.res;

import com.ddungyomi.petshop.domain.order.domain.Order;

public record OrderResCommand(
        Integer seq,
        String orderNumber,
        String buyerName,
        String status,
        Long totalAmount
) {
    public static OrderResCommand from(Order order) {
        return new OrderResCommand(
                order.seq(),
                order.orderNumber(),
                order.buyerName(),
                order.status().name(),
                order.totalAmount()
        );
    }
}
