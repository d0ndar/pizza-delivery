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
public class EmailNotificationObserver implements OrderObserver {

    private final NotificationFactory notificationFactory;

    @Override
    public void update(Order order, String eventType) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            log.debug("Email не указан для заказа {}, пропускаем отправку", order.getOrderNumber());
            return;
        }

        try {
            String message = createMessage(order, eventType);
            NotificationMessage notificationMessage = new NotificationMessage(
                    order.getCustomerEmail(),
                    getSubject(eventType),
                    message,
                    NotificationType.EMAIL
            );

            Notification notification = notificationFactory.createNotification(NotificationType.EMAIL);
            notification.send(notificationMessage);
        } catch (Exception e) {
            log.error("❌ Ошибка отправки email для заказа {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    @Override
    public boolean supports(String eventType) {
        return "ORDER_CREATED".equals(eventType) ||
                "ORDER_STATUS_CHANGED".equals(eventType);
    }

    private String createMessage(Order order, String eventType) {
        switch (eventType) {
            case "ORDER_CREATED":
                return String.format("""
                    Заказ №%s успешно создан! 🍕
                    
                    Детали заказа:
                    • Сумма: %.2f руб.
                    • Адрес доставки: %s
                    • Способ оплаты: %s
                    • Статус: %s
                    
                    Спасибо за ваш заказ! Мы уже начали готовить.
                    """, order.getOrderNumber(), order.getFinalAmount(),
                        order.getDeliveryAddress(), order.getPaymentMethod(), order.getStatus());

            case "ORDER_STATUS_CHANGED":
                return String.format("""
                    Статус вашего заказа №%s изменен.
                    
                    Новый статус: %s
                    
                    Следите за обновлениями в нашем приложении.
                    """, order.getOrderNumber(), order.getStatus());

            default:
                return String.format("Обновление по заказу №%s", order.getOrderNumber());
        }
    }

    private String getSubject(String eventType) {
        return switch (eventType) {
            case "ORDER_CREATED" -> "Ваш заказ принят!";
            case "ORDER_STATUS_CHANGED" -> "Статус заказа обновлен";
            default -> "Уведомление от Pizza Delivery";
        };
    }
}