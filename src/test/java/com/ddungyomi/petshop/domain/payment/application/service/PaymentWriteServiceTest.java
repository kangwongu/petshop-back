package com.ddungyomi.petshop.domain.payment.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddungyomi.petshop.common.exception.BadRequestException;
import com.ddungyomi.petshop.domain.order.application.port.out.GetOrderPort;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;
import com.ddungyomi.petshop.domain.payment.application.command.req.ConfirmPaymentReqCommand;
import com.ddungyomi.petshop.domain.payment.application.command.req.PaymentWebhookReqCommand;
import com.ddungyomi.petshop.domain.payment.application.command.res.PaymentResCommand;
import com.ddungyomi.petshop.domain.payment.application.port.out.ConfirmPaymentClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentStatusClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.SavePaymentPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossConfirmFailedException;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossPaymentStatusResult;
import com.ddungyomi.petshop.domain.payment.domain.Payment;
import com.ddungyomi.petshop.domain.payment.domain.PaymentStatus;

@ExtendWith(MockitoExtension.class)
class PaymentWriteServiceTest {

    @Mock
    private GetOrderPort getOrderPort;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private GetPaymentPort getPaymentPort;

    @Mock
    private SavePaymentPort savePaymentPort;

    @Mock
    private ConfirmPaymentClientPort confirmPaymentClientPort;

    @Mock
    private GetPaymentStatusClientPort getPaymentStatusClientPort;

    @InjectMocks
    private PaymentWriteService paymentWriteService;

    private Order createPendingOrder() {
        return Order.create(
                "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                List.of(OrderItem.create(1, 1, 25000L))
        );
    }

    @Test
    void create_금액이_일치하고_Toss_confirm이_성공하면_Payment와_Order_상태가_PAID가_된다() {
        Order order = createPendingOrder();
        when(getOrderPort.findByOrderNumber(order.orderNumber())).thenReturn(order);
        when(confirmPaymentClientPort.confirm("paymentKey1", order.orderNumber(), 25000L))
                .thenReturn(1700000000000L);
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfirmPaymentReqCommand command = ConfirmPaymentReqCommand.from("paymentKey1", order.orderNumber(), 25000L);

        PaymentResCommand result = paymentWriteService.create(command);

        assertThat(result.status()).isEqualTo("PAID");
        assertThat(result.paymentKey()).isEqualTo("paymentKey1");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(saveOrderPort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status().name()).isEqualTo("PAID");
    }

    @Test
    void create_요청_금액이_주문_총액과_다르면_Toss_confirm을_호출하지_않고_예외가_발생한다() {
        Order order = createPendingOrder();
        when(getOrderPort.findByOrderNumber(order.orderNumber())).thenReturn(order);

        ConfirmPaymentReqCommand command = ConfirmPaymentReqCommand.from("paymentKey1", order.orderNumber(), 10000L);

        assertThrows(BadRequestException.class, () -> paymentWriteService.create(command));
        verify(confirmPaymentClientPort, never()).confirm(any(), any(), any());
        verify(savePaymentPort, never()).save(any(Payment.class));
    }

    @Test
    void create_주문_상태가_PENDING이_아니면_Toss_confirm을_호출하지_않고_예외가_발생한다() {
        Order order = createPendingOrder().pay();
        when(getOrderPort.findByOrderNumber(order.orderNumber())).thenReturn(order);

        ConfirmPaymentReqCommand command = ConfirmPaymentReqCommand.from("paymentKey1", order.orderNumber(), 25000L);

        assertThrows(BadRequestException.class, () -> paymentWriteService.create(command));
        verify(confirmPaymentClientPort, never()).confirm(any(), any(), any());
    }

    @Test
    void create_Toss_confirm이_실패하면_예외를_던지지_않고_FAILED_상태로_응답한다() {
        Order order = createPendingOrder();
        when(getOrderPort.findByOrderNumber(order.orderNumber())).thenReturn(order);
        when(confirmPaymentClientPort.confirm("paymentKey1", order.orderNumber(), 25000L))
                .thenThrow(new TossConfirmFailedException("카드 한도 초과"));
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfirmPaymentReqCommand command = ConfirmPaymentReqCommand.from("paymentKey1", order.orderNumber(), 25000L);

        PaymentResCommand result = paymentWriteService.create(command);

        assertThat(result.status()).isEqualTo("FAILED");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(saveOrderPort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status().name()).isEqualTo("FAILED");
    }

    @Test
    void process_DONE_재조회_결과에서_Payment가_없으면_신규_생성되고_주문이_PAID된다() {
        Order order = createPendingOrder();
        when(getOrderPort.findByOrderNumber(order.orderNumber())).thenReturn(order);
        when(getPaymentPort.findNullableByPaymentKey("paymentKey1")).thenReturn(null);
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("DONE", 1700000000000L, null));
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", order.orderNumber()));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(savePaymentPort, times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().status()).isEqualTo(PaymentStatus.PAID);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(saveOrderPort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status().name()).isEqualTo("PAID");
    }

    @Test
    void process_DONE_재조회_결과인데_Payment가_이미_있으면_멱등하게_아무것도_하지_않는다() {
        Payment existingPayment = Payment.create(1, "paymentKey1", 25000L).confirm(1700000000000L);
        when(getPaymentPort.findNullableByPaymentKey("paymentKey1")).thenReturn(existingPayment);
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("DONE", 1700000000000L, null));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", "ORD-1"));

        verify(getOrderPort, never()).findByOrderNumber(any());
        verify(savePaymentPort, never()).save(any(Payment.class));
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void process_CANCELED_재조회_결과이고_Payment가_PAID면_결제와_주문이_CANCELLED로_갱신된다() {
        Order paidOrder = createPendingOrder().pay();
        Payment paidPayment = Payment.create(1, "paymentKey1", 25000L).confirm(1700000000000L);
        when(getPaymentPort.findNullableByPaymentKey("paymentKey1")).thenReturn(paidPayment);
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("CANCELED", null, 1700000001000L));
        when(getOrderPort.findByOrderNumber(paidOrder.orderNumber())).thenReturn(paidOrder);
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", paidOrder.orderNumber()));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(savePaymentPort, times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().status()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(paymentCaptor.getValue().updateEpoch()).isEqualTo(1700000001000L);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(saveOrderPort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status().name()).isEqualTo("CANCELLED");
    }

    @Test
    void process_CANCELED_재조회_결과인데_Payment가_없으면_아무것도_하지_않는다() {
        when(getPaymentPort.findNullableByPaymentKey("paymentKey1")).thenReturn(null);
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("CANCELED", null, null));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", "ORD-1"));

        verify(getOrderPort, never()).findByOrderNumber(any());
        verify(savePaymentPort, never()).save(any(Payment.class));
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void process_CANCELED_재조회_결과인데_이미_CANCELLED면_멱등하게_아무것도_하지_않는다() {
        Payment cancelledPayment = Payment.create(1, "paymentKey1", 25000L)
                .confirm(1700000000000L).cancel(1700000001000L);
        when(getPaymentPort.findNullableByPaymentKey("paymentKey1")).thenReturn(cancelledPayment);
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("CANCELED", null, 1700000001000L));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", "ORD-1"));

        verify(getOrderPort, never()).findByOrderNumber(any());
        verify(savePaymentPort, never()).save(any(Payment.class));
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void process_그_외_상태는_반영하지_않는다() {
        when(getPaymentStatusClientPort.findByPaymentKey("paymentKey1"))
                .thenReturn(new TossPaymentStatusResult("ABORTED", null, null));

        paymentWriteService.process(PaymentWebhookReqCommand.from("paymentKey1", "ORD-1"));

        verify(getPaymentPort, never()).findNullableByPaymentKey(any());
        verify(getOrderPort, never()).findByOrderNumber(any());
        verify(savePaymentPort, never()).save(any(Payment.class));
        verify(saveOrderPort, never()).save(any(Order.class));
    }
}
