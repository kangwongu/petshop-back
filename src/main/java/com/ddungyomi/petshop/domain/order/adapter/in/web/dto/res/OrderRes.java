package com.ddungyomi.petshop.domain.order.adapter.in.web.dto.res;

import com.ddungyomi.petshop.domain.order.application.command.res.OrderResCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 정보 반환 DTO")
public record OrderRes(
        @Schema(description = "주문 ID", example = "1")
        Integer id,

        @Schema(description = "주문번호", example = "ORD-20260929153000-A1B2C3")
        String orderNumber,

        @Schema(description = "주문자명", example = "홍길동")
        String buyerName,

        @Schema(description = "주문 상태", example = "PENDING")
        String status,

        @Schema(description = "총 주문 금액", example = "25000")
        Long totalAmount
) {
    public static OrderRes fromCommand(OrderResCommand command) {
        return new OrderRes(
                command.seq(), command.orderNumber(), command.buyerName(),
                command.status(), command.totalAmount()
        );
    }
}
