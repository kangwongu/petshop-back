package com.ddungyomi.petshop.domain.payment.application.command.res;

import java.time.Instant;

import com.ddungyomi.petshop.domain.payment.domain.Payment;

public record PaymentResCommand(
        Integer paymentId,
        String paymentKey,
        Integer orderId,
        Long amount,
        String status,
        String approvedAt
) {
    public static PaymentResCommand from(Payment payment) {
        return new PaymentResCommand(
                payment.seq(),
                payment.paymentKey(),
                payment.orderSeq(),
                payment.amount(),
                payment.status().name(),
                payment.approvedEpoch() == null ? null : Instant.ofEpochMilli(payment.approvedEpoch()).toString()
        );
    }
}
