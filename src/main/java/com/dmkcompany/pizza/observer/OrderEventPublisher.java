package com.dmkcompany.pizza.observer;

import com.dmkcompany.pizza.model.Order;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderEventPublisher {
    private final List<OrderObserver> observers;

    public OrderEventPublisher(List<OrderObserver> observers) {
        this.observers = observers;
    }

    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver observer : observers) {
            try {
                if (observer.supports(eventType)) {
                    observer.update(order, eventType);
                }
            } catch (Exception e) {
                System.err.println("Ошибка в наблюдателе " + observer.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}