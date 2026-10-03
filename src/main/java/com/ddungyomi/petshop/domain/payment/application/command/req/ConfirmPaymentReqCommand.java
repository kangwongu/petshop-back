package com.ddungyomi.petshop.domain.payment.application.command.req;

public record ConfirmPaymentReqCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
    public static ConfirmPaymentReqCommand from(String paymentKey, String orderId, Long amount) {
        return new ConfirmPaymentReqCommand(paymentKey, orderId, amount);
    }
}
