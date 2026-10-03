package com.ddungyomi.petshop.domain.order.application.port.out;

import com.ddungyomi.petshop.domain.order.domain.Order;

public interface GetOrderPort {

    Order findByOrderNumber(String orderNumber);
}
