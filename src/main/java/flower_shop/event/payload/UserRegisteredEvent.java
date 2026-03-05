package flower_shop.event.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    private UUID userId;
    private LocalDateTime createdOn;

}
