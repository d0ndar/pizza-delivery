package com.dmkcompany.pizza.strategy;

import com.dmkcompany.pizza.model.Cart;
import org.springframework.stereotype.Component;

@Component
public class QuantityDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(Cart cart) {
        Integer totalItems = cart.getTotalItems();
        return totalItems >= 5 ? 0.05 : 0.0;
    }

    @Override
    public String getDescription() {
        return "Скидка 5% при заказе от 5 товаров";
    }
}