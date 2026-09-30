package com.ddungyomi.petshop.domain.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void create_주문상품목록의_가격_수량_합계로_totalAmount를_계산한다() {
        List<OrderItem> orderItemList = List.of(
                OrderItem.create(1, 2, 5000L),
                OrderItem.create(2, 1, 15000L)
        );

        Order order = Order.create("홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123", orderItemList);

        assertThat(order.totalAmount()).isEqualTo(25000L);
    }

    @Test
    void create_초기상태는_PENDING이다() {
        Order order = Order.create(
                "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                List.of(OrderItem.create(1, 1, 5000L))
        );

        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void create_주문번호는_ORD_타임스탬프14자리_접미사6자리_포맷을_따른다() {
        Order order = Order.create(
                "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                List.of(OrderItem.create(1, 1, 5000L))
        );

        assertThat(order.orderNumber()).matches("^ORD-\\d{14}-[A-Z0-9]{6}$");
    }

    @Test
    void pay_상태만_PAID로_전이하고_나머지_필드는_그대로_유지한다() {
        List<OrderItem> orderItemList = List.of(OrderItem.create(1, 1, 5000L));
        Order pending = new Order(
                1, "ORD-20260101000000-ABC123", "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                5000L, OrderStatus.PENDING, orderItemList, 1000L, 1000L
        );

        Order paid = pending.pay();

        assertThat(paid.status()).isEqualTo(OrderStatus.PAID);
        assertThat(paid.seq()).isEqualTo(pending.seq());
        assertThat(paid.orderNumber()).isEqualTo(pending.orderNumber());
        assertThat(paid.totalAmount()).isEqualTo(pending.totalAmount());
        assertThat(paid.orderItemList()).isEqualTo(pending.orderItemList());
        assertThat(paid.createEpoch()).isEqualTo(pending.createEpoch());
    }

    @Test
    void fail_상태만_FAILED로_전이하고_나머지_필드는_그대로_유지한다() {
        List<OrderItem> orderItemList = List.of(OrderItem.create(1, 1, 5000L));
        Order pending = new Order(
                1, "ORD-20260101000000-ABC123", "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                5000L, OrderStatus.PENDING, orderItemList, 1000L, 1000L
        );

        Order failed = pending.fail();

        assertThat(failed.status()).isEqualTo(OrderStatus.FAILED);
        assertThat(failed.seq()).isEqualTo(pending.seq());
        assertThat(failed.orderNumber()).isEqualTo(pending.orderNumber());
        assertThat(failed.totalAmount()).isEqualTo(pending.totalAmount());
        assertThat(failed.orderItemList()).isEqualTo(pending.orderItemList());
        assertThat(failed.createEpoch()).isEqualTo(pending.createEpoch());
    }
}
