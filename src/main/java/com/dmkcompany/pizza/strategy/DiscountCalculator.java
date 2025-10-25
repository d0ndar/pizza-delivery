package com.dmkcompany.pizza.strategy;

import com.dmkcompany.pizza.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscountCalculator {

    private final List<DiscountStrategy> discountStrategies;

    public double calculateTotalDiscountRate(Cart cart) {
        return discountStrategies.stream()
                .mapToDouble(strategy -> strategy.calculateDiscount(cart))
                .sum();
    }

    public List<String> getAvailableDiscounts(Cart cart) {
        return discountStrategies.stream()
                .filter(strategy -> strategy.calculateDiscount(cart) > 0)
                .map(DiscountStrategy::getDescription)
                .toList();
    }

    public Double calculateDiscountAmount(Cart cart) {
        Double baseAmount = cart.getTotalAmount();
        double discountRate = calculateTotalDiscountRate(cart);
        return (double) Math.round(baseAmount * discountRate);
    }

    public Double calculateFinalAmount(Cart cart) {
        Double baseAmount = cart.getTotalAmount();
        double discountRate = calculateTotalDiscountRate(cart);
        Double finalAmount = baseAmount * (1 - discountRate);
        return (double) Math.round(finalAmount);
    }
}