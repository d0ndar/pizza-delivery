package com.dmkcompany.pizza.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotification implements Notification {

    @Override
    public void send(NotificationMessage message) {
        // В реальном проекте здесь должна быть интеграция с почтовым сервисом
    
        log.info("📧 ОТПРАВКА EMAIL:");
        log.info("Кому: {}", message.getTo());
        log.info("Тема: {}", message.getSubject());
        log.info("Сообщение: {}", message.getContent());
        log.info("--- Email отправлен успешно ---");

        simulateNetworkDelay();
    }

    @Override
    public boolean supports(NotificationType type) {
        return type == NotificationType.EMAIL;
    }

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
