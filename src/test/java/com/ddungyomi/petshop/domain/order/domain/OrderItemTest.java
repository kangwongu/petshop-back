package com.ddungyomi.petshop.domain.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void create_orderSeq는_null인_상태로_생성된다() {
        OrderItem orderItem = OrderItem.create(1, 2, 5000L);

        assertThat(orderItem.orderSeq()).isNull();
        assertThat(orderItem.productSeq()).isEqualTo(1);
        assertThat(orderItem.quantity()).isEqualTo(2);
        assertThat(orderItem.price()).isEqualTo(5000L);
    }
}
