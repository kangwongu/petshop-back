package com.ddungyomi.petshop.domain.order.domain;

public record OrderItem(
        Integer seq,
        Integer orderSeq,
        Integer productSeq,
        Integer quantity,
        Long price,
        Long createEpoch
) {
    public static OrderItem create(Integer productSeq, Integer quantity, Long price) {
        return new OrderItem(null, null, productSeq, quantity, price, System.currentTimeMillis());
    }
}
