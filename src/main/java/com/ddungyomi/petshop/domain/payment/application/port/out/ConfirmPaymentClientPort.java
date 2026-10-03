package com.ddungyomi.petshop.domain.payment.application.port.out;

public interface ConfirmPaymentClientPort {

    Long confirm(String paymentKey, String orderId, Long amount);
}
