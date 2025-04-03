package flower_shop.email.client.dto;

import java.time.LocalDateTime;

public record EmailResponse (String recipient, String subject, LocalDateTime sentOn) {

}