package com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.res;

import com.ddungyomi.petshop.domain.payment.application.command.res.PaymentResCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 정보 반환 DTO")
public record PaymentRes(
        @Schema(description = "결제 ID", example = "1")
        Integer paymentId,

        @Schema(description = "Toss 결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwyorNK0AGzE0pj3Kwv1M9E")
        String paymentKey,

        @Schema(description = "주문 ID", example = "1")
        Integer orderId,

        @Schema(description = "결제 금액", example = "25000")
        Long amount,

        @Schema(description = "결제 상태", example = "PAID")
        String status,

        @Schema(description = "결제 승인 시각", example = "2026-09-29T15:31:00Z")
        String approvedAt
) {
    public static PaymentRes fromCommand(PaymentResCommand command) {
        return new PaymentRes(
                command.paymentId(), command.paymentKey(), command.orderId(),
                command.amount(), command.status(), command.approvedAt()
        );
    }
}
