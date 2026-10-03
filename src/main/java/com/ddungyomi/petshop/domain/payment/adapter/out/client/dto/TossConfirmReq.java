package com.ddungyomi.petshop.domain.payment.adapter.out.client.dto;

public record TossConfirmReq(
        String paymentKey,
        String orderId,
        Long amount
) {
}
