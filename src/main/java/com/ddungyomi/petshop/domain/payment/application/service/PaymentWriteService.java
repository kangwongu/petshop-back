package com.ddungyomi.petshop.domain.payment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddungyomi.petshop.common.exception.BadRequestException;
import com.ddungyomi.petshop.domain.order.application.port.out.GetOrderPort;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderStatus;
import com.ddungyomi.petshop.domain.payment.application.command.req.ConfirmPaymentReqCommand;
import com.ddungyomi.petshop.domain.payment.application.command.req.PaymentWebhookReqCommand;
import com.ddungyomi.petshop.domain.payment.application.command.res.PaymentResCommand;
import com.ddungyomi.petshop.domain.payment.application.port.in.CreatePaymentUseCase;
import com.ddungyomi.petshop.domain.payment.application.port.in.ProcessPaymentWebhookUseCase;
import com.ddungyomi.petshop.domain.payment.application.port.out.ConfirmPaymentClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentStatusClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.SavePaymentPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossConfirmFailedException;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossPaymentStatusResult;
import com.ddungyomi.petshop.domain.payment.domain.Payment;
import com.ddungyomi.petshop.domain.payment.domain.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentWriteService implements CreatePaymentUseCase, ProcessPaymentWebhookUseCase {

    private static final String TOSS_STATUS_DONE = "DONE";
    private static final String TOSS_STATUS_CANCELED = "CANCELED";

    private final GetOrderPort getOrderPort;
    private final SaveOrderPort saveOrderPort;
    private final GetPaymentPort getPaymentPort;
    private final SavePaymentPort savePaymentPort;
    private final ConfirmPaymentClientPort confirmPaymentClientPort;
    private final GetPaymentStatusClientPort getPaymentStatusClientPort;

    @Override
    public PaymentResCommand create(ConfirmPaymentReqCommand command) {
        Order order = getOrderPort.findByOrderNumber(command.orderId());

        if (order.status() != OrderStatus.PENDING) {
            throw new BadRequestException("이미 처리된 주문입니다. orderNumber=" + command.orderId());
        }
        if (!order.totalAmount().equals(command.amount())) {
            throw new BadRequestException("주문 금액이 일치하지 않습니다.");
        }

        try {
            Long approvedEpoch = confirmPaymentClientPort.confirm(
                    command.paymentKey(), command.orderId(), command.amount()
            );
            Payment payment = Payment.create(order.seq(), command.paymentKey(), command.amount())
                    .confirm(approvedEpoch);
            saveOrderPort.save(order.pay());
            return PaymentResCommand.from(savePaymentPort.save(payment));
        } catch (TossConfirmFailedException exception) {
            Payment payment = Payment.create(order.seq(), command.paymentKey(), command.amount())
                    .fail(exception.getMessage());
            saveOrderPort.save(order.fail());
            return PaymentResCommand.from(savePaymentPort.save(payment));
        }
    }

    @Override
    public void process(PaymentWebhookReqCommand command) {
        TossPaymentStatusResult result = getPaymentStatusClientPort.findByPaymentKey(command.paymentKey());

        if (TOSS_STATUS_DONE.equals(result.status())) {
            handleDone(command, result.approvedEpoch());
        } else if (TOSS_STATUS_CANCELED.equals(result.status())) {
            handleCanceled(command);
        } else {
            log.info("웹훅 반영 대상이 아닌 상태 수신. paymentKey={}, status={}", command.paymentKey(), result.status());
        }
    }

    private void handleDone(PaymentWebhookReqCommand command, Long approvedEpoch) {
        Payment payment = getPaymentPort.findNullableByPaymentKey(command.paymentKey());
        if (payment != null) {
            log.info(
                    "이미 기록된 결제라 웹훅을 무시함. paymentKey={}, existingStatus={}",
                    command.paymentKey(), payment.status()
            );
            return;
        }

        Order order = getOrderPort.findByOrderNumber(command.orderId());
        Payment newPayment = Payment.create(order.seq(), command.paymentKey(), order.totalAmount())
                .confirm(approvedEpoch);
        saveOrderPort.save(order.pay());
        savePaymentPort.save(newPayment);
    }

    private void handleCanceled(PaymentWebhookReqCommand command) {
        Payment payment = getPaymentPort.findNullableByPaymentKey(command.paymentKey());
        if (payment == null || payment.status() != PaymentStatus.PAID) {
            log.info(
                    "취소 반영 대상이 아니라 웹훅을 무시함. paymentKey={}, existingStatus={}",
                    command.paymentKey(), payment == null ? null : payment.status()
            );
            return;
        }

        Order order = getOrderPort.findByOrderNumber(command.orderId());
        saveOrderPort.save(order.cancel());
        savePaymentPort.save(payment.cancel());
    }
}
