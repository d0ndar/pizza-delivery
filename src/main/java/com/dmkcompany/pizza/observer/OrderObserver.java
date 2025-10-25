package com.dmkcompany.pizza.observer;

import com.dmkcompany.pizza.model.Order;

public interface OrderObserver {
    void update(Order order, String eventType);
    boolean supports(String eventType);
}