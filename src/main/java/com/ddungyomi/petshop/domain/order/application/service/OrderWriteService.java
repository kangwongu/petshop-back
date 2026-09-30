package com.ddungyomi.petshop.domain.order.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddungyomi.petshop.domain.order.application.command.req.CreateOrderReqCommand;
import com.ddungyomi.petshop.domain.order.application.command.res.OrderResCommand;
import com.ddungyomi.petshop.domain.order.application.port.in.CreateOrderUseCase;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;
import com.ddungyomi.petshop.domain.product.application.port.out.GetProductPort;
import com.ddungyomi.petshop.domain.product.domain.Product;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService implements CreateOrderUseCase {

    private final SaveOrderPort saveOrderPort;
    private final GetProductPort getProductPort;

    @Override
    public OrderResCommand create(CreateOrderReqCommand command) {
        List<OrderItem> orderItemList = command.itemList().stream()
                .map(item -> {
                    Product product = getProductPort.findBySeq(item.productSeq());
                    return OrderItem.create(item.productSeq(), item.quantity(), product.price());
                })
                .toList();

        Order order = Order.create(
                command.buyerName(), command.buyerPhone(), command.buyerAddress(), orderItemList
        );

        return OrderResCommand.from(saveOrderPort.save(order));
    }
}
