package com.dmkcompany.pizza.strategy;

import com.dmkcompany.pizza.model.Cart;
import org.springframework.stereotype.Component;

@Component
public class AmountDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(Cart cart) {
        Double totalAmount = cart.getTotalAmount();
        return totalAmount >= 1000 ? 0.10 : 0.0;
    }

    @Override
    public String getDescription() {
        return "Скидка 10% при заказе от 1000 руб";
    }
}