package com.ddungyomi.petshop.common.exception;

public record ErrorRes(
        String message
) {
    public static ErrorRes from(String message) {
        return new ErrorRes(message);
    }
}
