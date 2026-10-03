package com.ddungyomi.petshop.domain.payment.adapter.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.req.ConfirmPaymentReq;
import com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.req.PaymentWebhookReq;
import com.ddungyomi.petshop.domain.payment.adapter.in.web.dto.res.PaymentRes;
import com.ddungyomi.petshop.domain.payment.application.port.in.CreatePaymentUseCase;
import com.ddungyomi.petshop.domain.payment.application.port.in.ProcessPaymentWebhookUseCase;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    @Operation(summary = "결제 승인", description = "Toss 결제 승인을 요청하고 결과를 주문/결제에 반영한다. (FR-04)")
    @PostMapping("/confirm")
    public PaymentRes create(@Valid @RequestBody ConfirmPaymentReq req) {
        return PaymentRes.fromCommand(createPaymentUseCase.create(req.toCommand()));
    }

    @Operation(summary = "결제 웹훅 수신", description = "Toss 결제 상태 변경 웹훅을 수신해 재조회 결과를 반영한다. (FR-05)")
    @PostMapping("/webhook")
    public void webhook(@RequestBody PaymentWebhookReq req) {
        try {
            processPaymentWebhookUseCase.process(req.toCommand());
        } catch (Exception exception) {
            // Toss는 실패 응답을 받으면 웹훅을 재발송하므로, 처리 중 오류가 나도 로그만 남기고 200으로 응답한다
            log.warn("웹훅 처리 중 오류 발생. eventType={}", req.eventType(), exception);
        }
    }
}
