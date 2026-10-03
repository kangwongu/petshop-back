package com.ddungyomi.petshop.domain.payment.adapter.out.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossConfirmRes(
        String approvedAt
) {
}
