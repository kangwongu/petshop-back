package com.ddungyomi.petshop.domain.order.application.command.req;

import java.util.List;

public record CreateOrderReqCommand(
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        List<OrderItemReqCommand> itemList
) {
    public static CreateOrderReqCommand from(
            String buyerName, String buyerPhone, String buyerAddress, List<OrderItemReqCommand> itemList
    ) {
        return new CreateOrderReqCommand(buyerName, buyerPhone, buyerAddress, itemList);
    }
}
