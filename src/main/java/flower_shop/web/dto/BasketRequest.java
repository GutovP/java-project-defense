package flower_shop.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BasketRequest {

    private UUID productId;

    private int quantity;
}
