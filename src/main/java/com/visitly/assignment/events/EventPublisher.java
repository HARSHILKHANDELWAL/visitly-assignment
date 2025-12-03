package com.visitly.assignment.events;

import com.visitly.assignment.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishRegistration(Long userId, String email) {
        UserEvent evt = new UserEvent("USER_REGISTERED", userId, email, Instant.now());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "user.registered", evt);
    }

    public void publishLogin(Long userId, String email) {
        UserEvent evt = new UserEvent("USER_LOGGED_IN", userId, email, Instant.now());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "user.loggedin", evt);
    }
}
