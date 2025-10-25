package com.dmkcompany.pizza.template;

import com.dmkcompany.pizza.model.Order;
import com.dmkcompany.pizza.model.Cart;
import com.dmkcompany.pizza.strategy.DiscountCalculator;
import com.dmkcompany.pizza.observer.OrderEventPublisher;
import com.dmkcompany.pizza.service.CartService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OrderProcessingTemplate {

    protected final DiscountCalculator discountCalculator;
    protected final OrderEventPublisher eventPublisher;
    protected final CartService cartService;

    public final Order processOrder(String deliveryAddress, String paymentMethod,
                                    String comment, String customerEmail, String customerPhone) {

        Cart cart = getCart();
        validateOrder(cart);
        PriceCalculationResult prices = calculatePrices(cart);
        Order order = createOrder(cart, deliveryAddress, paymentMethod, comment,
                customerEmail, customerPhone, prices);
        saveOrder(order);
        notifyObservers(order);
        clearCart();

        return order;
    }

    protected Cart getCart() {
        return cartService.getCart();
    }

    protected PriceCalculationResult calculatePrices(Cart cart) {
        Double discount = discountCalculator.calculateDiscountAmount(cart);
        Double finalAmount = discountCalculator.calculateFinalAmount(cart);
        return new PriceCalculationResult(discount, finalAmount);
    }

    protected void notifyObservers(Order order) {
        eventPublisher.notifyObservers(order, getEventType());
    }

    protected void clearCart() {
        cartService.clearCart();
    }

    protected abstract void validateOrder(Cart cart);
    protected abstract Order createOrder(Cart cart, String deliveryAddress,
                                         String paymentMethod, String comment,
                                         String customerEmail, String customerPhone,
                                         PriceCalculationResult prices);
    protected abstract void saveOrder(Order order);
    protected abstract String getEventType();

    protected record PriceCalculationResult(Double discount, Double finalAmount) {}
}