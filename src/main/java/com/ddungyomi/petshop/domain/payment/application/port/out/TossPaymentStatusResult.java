package com.ddungyomi.petshop.domain.payment.application.port.out;

public record TossPaymentStatusResult(
        String status,
        Long approvedEpoch,
        Long canceledEpoch
) {
}
