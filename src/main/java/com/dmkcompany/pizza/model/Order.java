package com.dmkcompany.pizza.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Order {
    private final String orderNumber;
    private final Cart cart;
    private final String deliveryAddress;
    private final String paymentMethod;
    private final String comment;
    private final Double totalAmount;
    private final Double discount;
    private final Double finalAmount;
    private final LocalDateTime orderTime;
    private OrderStatus status;
    private String customerEmail;
    private String customerPhone;

    private Order(OrderBuilder builder) {
        this.orderNumber = builder.orderNumber;
        this.cart = builder.cart;
        this.deliveryAddress = builder.deliveryAddress;
        this.paymentMethod = builder.paymentMethod;
        this.comment = builder.comment;
        this.totalAmount = builder.totalAmount;
        this.discount = builder.discount;
        this.finalAmount = builder.finalAmount;
        this.orderTime = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.customerEmail = builder.customerEmail;
        this.customerPhone = builder.customerPhone;
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static class OrderBuilder {
        private String orderNumber;
        private Cart cart;
        private String deliveryAddress;
        private String paymentMethod = "Картой онлайн";
        private String comment = "";
        private Double totalAmount;
        private Double discount;
        private Double finalAmount;
        private String customerEmail;
        private String customerPhone;

        public OrderBuilder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public OrderBuilder customerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public OrderBuilder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public OrderBuilder cart(Cart cart) {
            this.cart = cart;
            this.totalAmount = cart.getTotalAmount();
            return this;
        }

        public OrderBuilder deliveryAddress(String address) {
            this.deliveryAddress = address;
            return this;
        }

        public OrderBuilder paymentMethod(String method) {
            this.paymentMethod = method;
            return this;
        }

        public OrderBuilder comment(String instructions) {
            this.comment = instructions;
            return this;
        }

        public OrderBuilder discount(Double discount) {
            this.discount = discount;
            return this;
        }

        public OrderBuilder finalAmount(Double finalAmount) {
            this.finalAmount = finalAmount;
            return this;
        }

        public Order build() {
            validate();
            return new Order(this);
        }

        private void validate() {
            if (cart == null || cart.getItems().isEmpty()) {
                throw new IllegalStateException("Невозможно создать заказ: корзина пуста");
            }
            if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                throw new IllegalStateException("Адрес доставки обязателен");
            }
            if (orderNumber == null) {
                this.orderNumber = "ORD-" + System.currentTimeMillis();
            }
        }
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, PREPARING, DELIVERING, COMPLETED, CANCELLED
    }
}