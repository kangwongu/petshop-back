package com.ddungyomi.petshop.domain.order.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddungyomi.petshop.common.exception.NotFoundException;
import com.ddungyomi.petshop.domain.order.application.command.req.CreateOrderReqCommand;
import com.ddungyomi.petshop.domain.order.application.command.req.OrderItemReqCommand;
import com.ddungyomi.petshop.domain.order.application.command.res.OrderResCommand;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.product.application.port.out.GetProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;
import com.ddungyomi.petshop.domain.product.domain.ProductCategory;

@ExtendWith(MockitoExtension.class)
class OrderWriteServiceTest {

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private GetProductPort getProductPort;

    @InjectMocks
    private OrderWriteService orderWriteService;

    @Test
    void create_상품_현재가로_총액을_계산하고_가격_스냅샷을_저장한다() {
        Product product1 = new Product(1, ProductCategory.ACCESSORY, "키링", 5000L, null, 1000L, 1000L);
        Product product2 = new Product(2, ProductCategory.LIFESTYLE, "머그컵", 15000L, null, 1000L, 1000L);
        when(getProductPort.findBySeq(1)).thenReturn(product1);
        when(getProductPort.findBySeq(2)).thenReturn(product2);
        when(saveOrderPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderReqCommand command = CreateOrderReqCommand.from(
                "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                List.of(OrderItemReqCommand.from(1, 2), OrderItemReqCommand.from(2, 1))
        );

        OrderResCommand result = orderWriteService.create(command);

        assertThat(result.totalAmount()).isEqualTo(25000L);
        assertThat(result.buyerName()).isEqualTo("홍길동");
        assertThat(result.status()).isEqualTo("PENDING");

        verify(saveOrderPort, times(1)).save(any(Order.class));

    }

    @Test
    void create_존재하지_않는_productSeq가_포함되면_NotFoundException이_발생한다() {
        when(getProductPort.findBySeq(99)).thenThrow(new NotFoundException("상품을 찾을 수 없습니다. seq=99"));

        CreateOrderReqCommand command = CreateOrderReqCommand.from(
                "홍길동", "010-1234-5678", "서울시 강남구 테헤란로 123",
                List.of(OrderItemReqCommand.from(99, 1))
        );

        assertThrows(NotFoundException.class, () -> orderWriteService.create(command));
        verify(saveOrderPort, never()).save(any(Order.class));
    }
}
