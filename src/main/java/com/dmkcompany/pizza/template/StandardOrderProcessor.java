package com.dmkcompany.pizza.template;

import com.dmkcompany.pizza.model.Order;
import com.dmkcompany.pizza.model.Cart;
import com.dmkcompany.pizza.strategy.DiscountCalculator;
import com.dmkcompany.pizza.observer.OrderEventPublisher;
import com.dmkcompany.pizza.service.CartService;
import org.springframework.stereotype.Component;

@Component
public class StandardOrderProcessor extends OrderProcessingTemplate {

    public StandardOrderProcessor(DiscountCalculator discountCalculator,
                                  OrderEventPublisher eventPublisher,
                                  CartService cartService) {
        super(discountCalculator, eventPublisher, cartService);
    }

    @Override
    protected void validateOrder(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Невозможно оформить заказ: корзина пуста");
        }
        System.out.println("Валидация заказа");
    }

    @Override
    protected Order createOrder(Cart cart, String deliveryAddress, String paymentMethod,
                                String comment, String customerEmail, String customerPhone,
                                PriceCalculationResult prices) {
        return Order.builder()
                .cart(cart)
                .deliveryAddress(deliveryAddress)
                .paymentMethod(paymentMethod)
                .comment(comment)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .discount(prices.discount())
                .finalAmount(prices.finalAmount())
                .build();
    }

    @Override
    protected void saveOrder(Order order) {
        System.out.println("=== СОЗДАН ЗАКАЗ ===");
        System.out.println("Номер: " + order.getOrderNumber());
        System.out.println("Адрес: " + order.getDeliveryAddress());
        System.out.println("Способ оплаты: " + order.getPaymentMethod());
        System.out.println("Комментарий: " + order.getComment());
        System.out.println("Товаров: " + order.getCart().getTotalItems());
        System.out.println("Общая сумма: " + order.getTotalAmount() + " руб.");
        System.out.println("Скидка: " + order.getDiscount() + " руб.");
        System.out.println("Итоговая сумма: " + order.getFinalAmount() + " руб.");
        System.out.println("Статус: " + order.getStatus());
        System.out.println("====================");
    }

    @Override
    protected String getEventType() {
        return "ORDER_CREATED";
    }
}