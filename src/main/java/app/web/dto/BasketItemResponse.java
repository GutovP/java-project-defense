package app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemResponse {

    private UUID basketId;

    private String productName;

    private BigDecimal productPrice;

    private List<String> images;

    private int quantity;

    private BigDecimal itemTotalPrice;
}
