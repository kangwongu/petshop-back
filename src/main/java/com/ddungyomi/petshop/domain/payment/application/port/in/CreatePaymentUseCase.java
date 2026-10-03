package com.ddungyomi.petshop.domain.payment.application.port.in;

import com.ddungyomi.petshop.domain.payment.application.command.req.ConfirmPaymentReqCommand;
import com.ddungyomi.petshop.domain.payment.application.command.res.PaymentResCommand;

public interface CreatePaymentUseCase {

    PaymentResCommand create(ConfirmPaymentReqCommand command);
}
