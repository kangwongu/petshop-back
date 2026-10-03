package com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.req;

import com.ddungyomi.petshop.domain.payment.application.command.req.PaymentWebhookReqCommand;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhookReq(
        String eventType,
        Data data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String paymentKey,
            String orderId
    ) {
    }

    public PaymentWebhookReqCommand toCommand() {
        return PaymentWebhookReqCommand.from(this.data.paymentKey(), this.data.orderId());
    }
}
