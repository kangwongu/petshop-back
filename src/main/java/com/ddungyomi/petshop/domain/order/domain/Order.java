package com.ddungyomi.petshop.domain.order.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public record Order(
        Integer seq,
        String orderNumber,
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        Long totalAmount,
        OrderStatus status,
        List<OrderItem> orderItemList,
        Long createEpoch,
        Long updateEpoch
) {
    private static final DateTimeFormatter ORDER_NUMBER_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String ORDER_NUMBER_SUFFIX_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ORDER_NUMBER_SUFFIX_LENGTH = 6;

    public static Order create(
            String buyerName, String buyerPhone, String buyerAddress, List<OrderItem> orderItemList
    ) {
        Long totalAmount = orderItemList.stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();
        return new Order(
                null, generateOrderNumber(), buyerName, buyerPhone, buyerAddress,
                totalAmount, OrderStatus.PENDING, orderItemList,
                System.currentTimeMillis(), System.currentTimeMillis()
        );
    }

    public Order pay() {
        return new Order(
                this.seq, this.orderNumber, this.buyerName, this.buyerPhone, this.buyerAddress,
                this.totalAmount, OrderStatus.PAID, this.orderItemList,
                this.createEpoch, System.currentTimeMillis()
        );
    }

    public Order fail() {
        return new Order(
                this.seq, this.orderNumber, this.buyerName, this.buyerPhone, this.buyerAddress,
                this.totalAmount, OrderStatus.FAILED, this.orderItemList,
                this.createEpoch, System.currentTimeMillis()
        );
    }

    public Order cancel() {
        return new Order(
                this.seq, this.orderNumber, this.buyerName, this.buyerPhone, this.buyerAddress,
                this.totalAmount, OrderStatus.CANCELLED, this.orderItemList,
                this.createEpoch, System.currentTimeMillis()
        );
    }

    private static String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(ORDER_NUMBER_TIMESTAMP_FORMAT);
        String suffix = ThreadLocalRandom.current()
                .ints(ORDER_NUMBER_SUFFIX_LENGTH, 0, ORDER_NUMBER_SUFFIX_CHARS.length())
                .mapToObj(ORDER_NUMBER_SUFFIX_CHARS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
        return "ORD-" + timestamp + "-" + suffix;
    }
}
