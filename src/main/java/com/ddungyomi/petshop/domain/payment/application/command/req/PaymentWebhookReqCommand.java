package com.ddungyomi.petshop.domain.payment.application.command.req;

public record PaymentWebhookReqCommand(
        String paymentKey,
        String orderId
) {
    public static PaymentWebhookReqCommand from(String paymentKey, String orderId) {
        return new PaymentWebhookReqCommand(paymentKey, orderId);
    }
}
