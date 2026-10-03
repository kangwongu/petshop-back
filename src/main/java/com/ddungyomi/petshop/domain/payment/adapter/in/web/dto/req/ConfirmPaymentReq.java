package com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.req;

import com.ddungyomi.petshop.domain.payment.application.command.req.ConfirmPaymentReqCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "결제 승인 요청 DTO")
public record ConfirmPaymentReq(
        @Schema(description = "Toss 결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwyorNK0AGzE0pj3Kwv1M9E")
        @NotBlank
        String paymentKey,

        @Schema(description = "주문번호", example = "ORD-20260929153000-A1B2C3")
        @NotBlank
        String orderId,

        @Schema(description = "결제 금액", example = "25000")
        @NotNull
        Long amount
) {
    public ConfirmPaymentReqCommand toCommand() {
        return ConfirmPaymentReqCommand.from(this.paymentKey, this.orderId, this.amount);
    }
}
