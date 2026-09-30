package com.ddungyomi.petshop.domain.order.application.command.req;

public record OrderItemReqCommand(
        Integer productSeq,
        Integer quantity
) {
    public static OrderItemReqCommand from(Integer productSeq, Integer quantity) {
        return new OrderItemReqCommand(productSeq, quantity);
    }
}
