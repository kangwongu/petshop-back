package com.ddungyomi.petshop.domain.product.application.command.req;

public record CreateProductReqCommand(
        Integer categoryCode,
        String name,
        Long price,
        String imageUrl
) {
    public static CreateProductReqCommand from(
            Integer categoryCode, String name, Long price, String imageUrl
    ) {
        return new CreateProductReqCommand(categoryCode, name, price, imageUrl);
    }
}
