package com.ddungyomi.petshop.domain.payment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.ddungyomi.petshop.domain.payment.domain.PaymentStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payments_order_seq", columnNames = "order_seq"),
        @UniqueConstraint(name = "uk_payments_payment_key", columnNames = "payment_key")
})
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "order_seq", nullable = false)
    private Integer orderSeq;

    @Column(name = "payment_key", nullable = false, length = 200)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    @Column(name = "approved_epoch")
    private Long approvedEpoch;

    @Column(nullable = false)
    private Long createEpoch;

    @Column(nullable = false)
    private Long updateEpoch;

    public static PaymentJpaEntity create(
            Integer seq, Integer orderSeq, String paymentKey, Long amount, PaymentStatus status,
            String failReason, Long approvedEpoch, Long createEpoch, Long updateEpoch
    ) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.seq = seq;
        entity.orderSeq = orderSeq;
        entity.paymentKey = paymentKey;
        entity.amount = amount;
        entity.status = status;
        entity.failReason = failReason;
        entity.approvedEpoch = approvedEpoch;
        entity.createEpoch = createEpoch;
        entity.updateEpoch = updateEpoch;
        return entity;
    }
}
