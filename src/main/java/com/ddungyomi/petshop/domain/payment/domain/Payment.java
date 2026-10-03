package com.ddungyomi.petshop.domain.payment.domain;

public record Payment(
        Integer seq,
        Integer orderSeq,
        String paymentKey,
        Long amount,
        PaymentStatus status,
        String failReason,
        Long approvedEpoch,
        Long createEpoch,
        Long updateEpoch
) {
    public static Payment create(Integer orderSeq, String paymentKey, Long amount) {
        return new Payment(
                null, orderSeq, paymentKey, amount, PaymentStatus.PENDING, null, null,
                System.currentTimeMillis(), System.currentTimeMillis()
        );
    }

    public Payment confirm(Long approvedEpoch) {
        return new Payment(
                this.seq, this.orderSeq, this.paymentKey, this.amount, PaymentStatus.PAID,
                this.failReason, approvedEpoch, this.createEpoch, System.currentTimeMillis()
        );
    }

    public Payment fail(String failReason) {
        return new Payment(
                this.seq, this.orderSeq, this.paymentKey, this.amount, PaymentStatus.FAILED,
                failReason, this.approvedEpoch, this.createEpoch, System.currentTimeMillis()
        );
    }

    public Payment cancel() {
        return new Payment(
                this.seq, this.orderSeq, this.paymentKey, this.amount, PaymentStatus.CANCELLED,
                this.failReason, this.approvedEpoch, this.createEpoch, System.currentTimeMillis()
        );
    }
}
