package com.ddungyomi.petshop.domain.order.adapter.in.web.dto.req;

import com.ddungyomi.petshop.domain.order.application.command.req.OrderItemReqCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "주문 상품 항목 요청 DTO")
public record OrderItemReq(
        @Schema(description = "상품 ID", example = "1")
        @NotNull
        Integer productId,

        @Schema(description = "수량", example = "2")
        @NotNull
        Integer quantity
) {
    public OrderItemReqCommand toCommand() {
        return OrderItemReqCommand.from(this.productId, this.quantity);
    }
}
