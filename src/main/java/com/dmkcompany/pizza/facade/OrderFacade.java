package com.dmkcompany.pizza.facade;

import com.dmkcompany.pizza.model.Order;
import com.dmkcompany.pizza.template.StandardOrderProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final StandardOrderProcessor orderProcessor;

    public OrderResult placeOrder(String deliveryAddress, String paymentMethod, String comment,
                                  String customerEmail, String customerPhone) {


        Order order = orderProcessor.processOrder(deliveryAddress, paymentMethod,
                comment, customerEmail, customerPhone);

        return new OrderResult(
                order.getOrderNumber(),
                order.getFinalAmount(),
                "Заказ успешно оформлен! Номер: " + order.getOrderNumber()
        );
    }
    /**
     * DTO для возврата результата оформления заказа
     */
    public record OrderResult(String orderNumber, Double finalAmount, String message) {}
}