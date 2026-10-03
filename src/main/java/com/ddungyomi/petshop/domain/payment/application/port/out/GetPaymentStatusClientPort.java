package com.ddungyomi.petshop.domain.payment.application.port.out;

public interface GetPaymentStatusClientPort {

    TossPaymentStatusResult findByPaymentKey(String paymentKey);
}
