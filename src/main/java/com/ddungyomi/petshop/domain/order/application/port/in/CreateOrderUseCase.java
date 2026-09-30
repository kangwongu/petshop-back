package com.ddungyomi.petshop.domain.order.application.port.in;

import com.ddungyomi.petshop.domain.order.application.command.req.CreateOrderReqCommand;
import com.ddungyomi.petshop.domain.order.application.command.res.OrderResCommand;

public interface CreateOrderUseCase {

    OrderResCommand create(CreateOrderReqCommand command);
}
