package com.ddungyomi.petshop.domain.payment.application.port.out;

import com.ddungyomi.petshop.domain.payment.domain.Payment;

public interface GetPaymentPort {

    Payment findNullableByPaymentKey(String paymentKey);
}
