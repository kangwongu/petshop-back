package com.ddungyomi.petshop.domain.payment.adapter.out.client;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.ddungyomi.petshop.domain.payment.adapter.out.client.dto.TossConfirmReq;
import com.ddungyomi.petshop.domain.payment.adapter.out.client.dto.TossConfirmRes;
import com.ddungyomi.petshop.domain.payment.adapter.out.client.dto.TossErrorRes;
import com.ddungyomi.petshop.domain.payment.adapter.out.client.dto.TossPaymentStatusRes;
import com.ddungyomi.petshop.domain.payment.application.port.out.ConfirmPaymentClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentStatusClientPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossConfirmFailedException;
import com.ddungyomi.petshop.domain.payment.application.port.out.TossPaymentStatusResult;

@Component
public class TossPaymentClient implements ConfirmPaymentClientPort, GetPaymentStatusClientPort {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_PATH = "/v1/payments/confirm";
    private static final String STATUS_PATH = "/v1/payments/{paymentKey}";

    private final RestClient restClient;

    public TossPaymentClient(@Value("${toss.secret-key}") String secretKey) {
        String encodedSecretKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        this.restClient = RestClient.builder()
                .baseUrl(TOSS_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                .build();
    }

    @Override
    public Long confirm(String paymentKey, String orderId, Long amount) {
        try {
            TossConfirmRes response = restClient.post()
                    .uri(CONFIRM_PATH)
                    .body(new TossConfirmReq(paymentKey, orderId, amount))
                    .retrieve()
                    .body(TossConfirmRes.class);

            return OffsetDateTime.parse(response.approvedAt()).toInstant().toEpochMilli();
        } catch (RestClientResponseException exception) {
            throw new TossConfirmFailedException(resolveErrorMessage(exception));
        }
    }

    @Override
    public TossPaymentStatusResult findByPaymentKey(String paymentKey) {
        try {
            TossPaymentStatusRes response = restClient.get()
                    .uri(STATUS_PATH, paymentKey)
                    .retrieve()
                    .body(TossPaymentStatusRes.class);

            Long approvedEpoch = response.approvedAt() != null
                    ? OffsetDateTime.parse(response.approvedAt()).toInstant().toEpochMilli()
                    : null;
            Long canceledEpoch = response.cancels() != null && !response.cancels().isEmpty()
                    ? OffsetDateTime.parse(response.cancels().getLast().canceledAt()).toInstant().toEpochMilli()
                    : null;
            return new TossPaymentStatusResult(response.status(), approvedEpoch, canceledEpoch);
        } catch (RestClientResponseException exception) {
            throw new TossConfirmFailedException(resolveErrorMessage(exception));
        }
    }

    private String resolveErrorMessage(RestClientResponseException exception) {
        try {
            TossErrorRes error = exception.getResponseBodyAs(TossErrorRes.class);
            return error != null ? error.message() : exception.getMessage();
        } catch (Exception parseException) {
            return exception.getMessage();
        }
    }
}
