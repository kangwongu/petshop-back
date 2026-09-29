package com.ddungyomi.petshop.domain.product.adapter.in.web.dto.res;

import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 정보 반환 DTO")
public record ProductRes(
        @Schema(description = "상품 ID", example = "1")
        Integer id,

        @Schema(description = "상품명", example = "포토카드")
        String name,

        @Schema(description = "가격", example = "5000")
        Long price,

        @Schema(description = "카테고리 ID", example = "1")
        Integer categoryId,

        @Schema(description = "카테고리명", example = "인쇄류")
        String categoryName,

        @Schema(description = "상품 이미지 URL", example = "https://example-bucket.s3.ap-northeast-2.amazonaws.com/products/1.png")
        String imageUrl
) {
    public static ProductRes fromCommand(ProductResCommand command) {
        return new ProductRes(
                command.seq(), command.name(), command.price(),
                command.categoryId(), command.categoryName(), command.imageUrl()
        );
    }
}
