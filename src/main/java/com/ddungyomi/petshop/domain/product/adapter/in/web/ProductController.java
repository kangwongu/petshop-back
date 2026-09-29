package com.ddungyomi.petshop.domain.product.adapter.in.web;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddungyomi.petshop.domain.product.adapter.in.web.dto.req.GetProductReq;
import com.ddungyomi.petshop.domain.product.adapter.in.web.dto.res.ProductRes;
import com.ddungyomi.petshop.domain.product.application.command.req.CreateProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.port.in.CreateProductUseCase;
import com.ddungyomi.petshop.domain.product.application.port.in.GetProductUseCase;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    // FR-06 초기 데이터 시딩용 고정 시드 데이터. 실제 이미지는 S3 업로드 전까지 비워둔다(TR-10).
    private static final List<CreateProductReqCommand> SEED_DATA = List.of(
            CreateProductReqCommand.from(ProductCategory.PRINTING.code(), "포토카드", 5000L, null),
            CreateProductReqCommand.from(ProductCategory.LIFESTYLE.code(), "텀블러", 20000L, null),
            CreateProductReqCommand.from(ProductCategory.LIFESTYLE.code(), "수저받침대", 5000L, null),
            CreateProductReqCommand.from(ProductCategory.LIFESTYLE.code(), "마그네틱", 5000L, null),
            CreateProductReqCommand.from(ProductCategory.LIFESTYLE.code(), "에코백", 10000L, null),
            CreateProductReqCommand.from(ProductCategory.LIFESTYLE.code(), "머그컵", 15000L, null),
            CreateProductReqCommand.from(ProductCategory.ACCESSORY.code(), "키링", 5000L, null),
            CreateProductReqCommand.from(ProductCategory.ACCESSORY.code(), "요미 카드지갑", 25000L, null),
            CreateProductReqCommand.from(ProductCategory.ACCESSORY.code(), "뚱이 카드지갑", 25000L, null),
            CreateProductReqCommand.from(ProductCategory.ACCESSORY.code(), "인형", 10000L, null)
    );

    private final GetProductUseCase getProductUseCase;
    private final CreateProductUseCase createProductUseCase;

    @Operation(summary = "상품 목록 조회", description = "카테고리 필터링을 지원하는 상품 목록을 조회한다. (FR-01)")
    @GetMapping
    public List<ProductRes> findList(@ParameterObject GetProductReq req) {
        return getProductUseCase.findList(req.toCommand()).stream()
                .map(ProductRes::fromCommand)
                .toList();
    }

    @Operation(summary = "상품 상세 조회", description = "단일 상품의 상세 정보를 조회한다. (FR-02)")
    @GetMapping("/{id}")
    public ProductRes findBySeq(@PathVariable Integer id) {
        return ProductRes.fromCommand(getProductUseCase.findBySeq(id));
    }

    @Operation(summary = "초기 데이터 시딩", description = "고정된 초기 상품 데이터를 채운다. (FR-06)")
    @PostMapping("/seed")
    public List<ProductRes> seed() {
        return createProductUseCase.createList(SEED_DATA).stream()
                .map(ProductRes::fromCommand)
                .toList();
    }
}
