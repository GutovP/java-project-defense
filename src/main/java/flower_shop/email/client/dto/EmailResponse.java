package flower_shop.email.client.dto;

import java.time.LocalDateTime;

public record EmailResponse (String to, String subject, LocalDateTime sentOn) {

}