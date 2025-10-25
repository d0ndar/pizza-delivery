package com.dmkcompany.pizza.strategy;

import com.dmkcompany.pizza.model.Cart;

public interface DiscountStrategy {
    double calculateDiscount(Cart cart);
    String getDescription();
}