package com.ddungyomi.petshop.domain.payment.adapter.out.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentStatusRes(
        String status,
        String approvedAt,
        List<Cancel> cancels
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cancel(
            String canceledAt
    ) {
    }
}
