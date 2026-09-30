package com.ddungyomi.petshop.domain.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void create_초기상태는_PENDING이고_approvedEpoch는_null이다() {
        Payment payment = Payment.create(1, "payment-key-1", 25000L);

        assertThat(payment.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.approvedEpoch()).isNull();
        assertThat(payment.failReason()).isNull();
    }

    @Test
    void confirm_상태를_PAID로_전이하고_approvedEpoch를_설정한다() {
        Payment pending = new Payment(1, 1, "payment-key-1", 25000L, PaymentStatus.PENDING, null, null, 1000L, 1000L);

        Payment confirmed = pending.confirm(2000L);

        assertThat(confirmed.status()).isEqualTo(PaymentStatus.PAID);
        assertThat(confirmed.approvedEpoch()).isEqualTo(2000L);
        assertThat(confirmed.failReason()).isEqualTo(pending.failReason());
        assertThat(confirmed.amount()).isEqualTo(pending.amount());
        assertThat(confirmed.createEpoch()).isEqualTo(pending.createEpoch());
    }

    @Test
    void fail_상태를_FAILED로_전이하고_failReason을_설정한다() {
        Payment pending = new Payment(1, 1, "payment-key-1", 25000L, PaymentStatus.PENDING, null, null, 1000L, 1000L);

        Payment failed = pending.fail("잔액 부족");

        assertThat(failed.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(failed.failReason()).isEqualTo("잔액 부족");
        assertThat(failed.approvedEpoch()).isEqualTo(pending.approvedEpoch());
        assertThat(failed.amount()).isEqualTo(pending.amount());
        assertThat(failed.createEpoch()).isEqualTo(pending.createEpoch());
    }
}
