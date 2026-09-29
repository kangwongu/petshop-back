package com.ddungyomi.petshop.domain.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddungyomi.petshop.domain.product.application.command.req.GetProductReqCommand;
import com.ddungyomi.petshop.domain.product.application.command.res.ProductResCommand;
import com.ddungyomi.petshop.domain.product.application.port.out.GetProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

@ExtendWith(MockitoExtension.class)
class ProductReadServiceTest {

    @Mock
    private GetProductPort getProductPort;

    @InjectMocks
    private ProductReadService productReadService;

    @Test
    void findBySeq_상품을_ProductResCommand로_변환한다() {
        Product product = new Product(1, ProductCategory.PRINTING, "뚱이 스티커", 5000L, null, 1000L, 1000L);
        when(getProductPort.findBySeq(1)).thenReturn(product);

        ProductResCommand result = productReadService.findBySeq(1);

        assertThat(result.seq()).isEqualTo(1);
        assertThat(result.categoryId()).isEqualTo(1);
        assertThat(result.categoryName()).isEqualTo("인쇄류");
        assertThat(result.name()).isEqualTo("뚱이 스티커");
        assertThat(result.price()).isEqualTo(5000L);
    }

    @Test
    void findList_categoryCode로_필터링된_결과를_ProductResCommand_목록으로_반환한다() {
        Product product1 = new Product(1, ProductCategory.LIFESTYLE, "요미 머그컵", 12000L, null, 1000L, 1000L);
        Product product2 = new Product(2, ProductCategory.LIFESTYLE, "뚱이 텀블러", 15000L, null, 1000L, 1000L);
        when(getProductPort.findList(2)).thenReturn(List.of(product1, product2));

        List<ProductResCommand> result = productReadService.findList(GetProductReqCommand.from(2));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductResCommand::name)
                .containsExactly("요미 머그컵", "뚱이 텀블러");
        verify(getProductPort).findList(2);
    }

    @Test
    void findList_categoryCode가_null이면_null을_그대로_Port에_전달한다() {
        when(getProductPort.findList(null)).thenReturn(List.of());

        productReadService.findList(GetProductReqCommand.from(null));

        verify(getProductPort).findList(null);
    }
}
