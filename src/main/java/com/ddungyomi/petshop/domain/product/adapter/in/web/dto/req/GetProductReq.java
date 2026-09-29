package com.ddungyomi.petshop.domain.product.adapter.in.web.dto.req;

import com.ddungyomi.petshop.domain.product.application.command.req.GetProductReqCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 목록 조회 Query String DTO")
public record GetProductReq(
        @Schema(description = "카테고리 ID (미지정 시 전체 조회)", example = "1")
        Integer categoryId
) {
    public GetProductReqCommand toCommand() {
        return GetProductReqCommand.from(this.categoryId);
    }
}
