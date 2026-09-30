package com.ddungyomi.petshop.domain.order.adapter.in.web.dto.req;

import java.util.List;

import com.ddungyomi.petshop.domain.order.application.command.req.CreateOrderReqCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderReq(
        @Schema(description = "주문자명", example = "홍길동")
        @NotBlank
        String buyerName,

        @Schema(description = "주문자 연락처", example = "010-1234-5678")
        @NotBlank
        String buyerPhone,

        @Schema(description = "배송 주소", example = "서울시 강남구 테헤란로 123")
        @NotBlank
        String buyerAddress,

        @Schema(description = "주문 상품 목록")
        @NotEmpty
        @Valid
        List<OrderItemReq> items
) {
    public CreateOrderReqCommand toCommand() {
        return CreateOrderReqCommand.from(
                this.buyerName, this.buyerPhone, this.buyerAddress,
                this.items.stream().map(OrderItemReq::toCommand).toList()
        );
    }
}
