package com.dmkcompany.pizza.observer;

import com.dmkcompany.pizza.factory.Notification;
import com.dmkcompany.pizza.factory.NotificationFactory;
import com.dmkcompany.pizza.factory.NotificationMessage;
import com.dmkcompany.pizza.factory.NotificationType;
import com.dmkcompany.pizza.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationObserver implements OrderObserver {

    private final NotificationFactory notificationFactory;

    @Override
    public void update(Order order, String eventType) {
        if (order.getCustomerPhone() == null || order.getCustomerPhone().trim().isEmpty()) {
            log.debug("Телефон не указан для заказа {}, пропускаем отправку", order.getOrderNumber());
            return;
        }

        try {
            String message = createMessage(order, eventType);
            NotificationMessage notificationMessage = new NotificationMessage(
                    order.getCustomerPhone(),
                    message,
                    NotificationType.SMS
            );

            Notification notification = notificationFactory.createNotification(NotificationType.SMS);
            notification.send(notificationMessage);
        } catch (Exception e) {
            log.error("❌ Ошибка отправки SMS для заказа {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    @Override
    public boolean supports(String eventType) {
        // SMS только для важных событий
        return "ORDER_READY".equals(eventType) ||
                "ORDER_DELIVERED".equals(eventType);
    }

    private String createMessage(Order order, String eventType) {
        switch (eventType) {
            case "ORDER_READY":
                return String.format("Заказ №%s готов к доставке! Ожидайте курьера в течение 30 минут.",
                        order.getOrderNumber());
            case "ORDER_DELIVERED":
                return String.format("Заказ №%s доставлен! Спасибо, что выбрали нас! 🍕",
                        order.getOrderNumber());
            default:
                return String.format("Обновление по заказу №%s", order.getOrderNumber());
        }
    }
}