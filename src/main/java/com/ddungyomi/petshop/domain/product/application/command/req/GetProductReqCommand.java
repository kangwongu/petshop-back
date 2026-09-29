package com.ddungyomi.petshop.domain.product.application.command.req;

public record GetProductReqCommand(
        Integer categoryCode
) {
    public static GetProductReqCommand from(Integer categoryCode) {
        return new GetProductReqCommand(categoryCode);
    }
}
