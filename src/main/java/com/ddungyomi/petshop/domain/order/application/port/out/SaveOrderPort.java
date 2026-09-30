package com.ddungyomi.petshop.domain.order.application.port.out;

import com.ddungyomi.petshop.domain.order.domain.Order;

public interface SaveOrderPort {

    Order save(Order order);
}
