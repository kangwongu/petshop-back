package com.ddungyomi.petshop.domain.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddungyomi.petshop.domain.product.application.command.req.CreateProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;
import com.ddungyomi.petshop.domain.product.application.port.out.SaveProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

@ExtendWith(MockitoExtension.class)
class ProductWriteServiceTest {

    @Mock
    private SaveProductPort saveProductPort;

    @InjectMocks
    private ProductWriteService productWriteService;

    @Test
    void createList_요청_목록을_도메인으로_변환해_저장하고_결과를_반환한다() {
        CreateProductReqCommand command = CreateProductReqCommand.from(3, "뚱이 키링", 8000L, null);
        Product saved = new Product(1, ProductCategory.ACCESSORY, "뚱이 키링", 8000L, null, 1000L, 1000L);
        when(saveProductPort.saveList(anyList())).thenReturn(List.of(saved));

        List<ProductResCommand> result = productWriteService.createList(List.of(command));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("뚱이 키링");
        assertThat(result.get(0).categoryId()).isEqualTo(3);
    }

    @Test
    void createList_존재하지_않는_카테고리_코드면_예외가_발생한다() {
        CreateProductReqCommand command = CreateProductReqCommand.from(99, "존재하지 않는 카테고리 상품", 1000L, null);

        assertThrows(
                IllegalArgumentException.class,
                () -> productWriteService.createList(List.of(command))
        );
    }
}
