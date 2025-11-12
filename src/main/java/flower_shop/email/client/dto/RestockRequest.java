package flower_shop.email.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    private String recipient;

    private String subject;

    private String body;
}
