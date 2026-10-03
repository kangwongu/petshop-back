package com.ddungyomi.petshop.domain.payment.application.port.in;

import com.ddungyomi.petshop.domain.payment.application.command.req.PaymentWebhookReqCommand;

public interface ProcessPaymentWebhookUseCase {

    void process(PaymentWebhookReqCommand command);
}
