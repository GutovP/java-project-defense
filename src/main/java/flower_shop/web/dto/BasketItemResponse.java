package flower_shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemResponse {

    private UUID basketId;

    private String productName;

    private BigDecimal productPrice;

    private String image;

    private int quantity;

    private BigDecimal itemTotalPrice;
}
