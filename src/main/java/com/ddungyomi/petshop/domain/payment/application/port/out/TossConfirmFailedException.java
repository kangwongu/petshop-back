package com.ddungyomi.petshop.domain.payment.application.port.out;

public class TossConfirmFailedException extends RuntimeException {

    public TossConfirmFailedException(String message) {
        super(message);
    }
}
