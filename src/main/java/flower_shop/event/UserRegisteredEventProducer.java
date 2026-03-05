package flower_shop.event;


import flower_shop.event.payload.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRegisteredEventProducer {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Autowired
    public UserRegisteredEventProducer(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(UserRegisteredEvent userRegisteredEvent) {

        kafkaTemplate.send("user-registered-event.v1", userRegisteredEvent);
        log.info("Successfully published registered event for user [%s]".formatted(userRegisteredEvent.getUserId()));
    }
}
