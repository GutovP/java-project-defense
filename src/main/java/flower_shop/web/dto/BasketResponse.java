package flower_shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketResponse {

    private List<BasketItemResponse> items;

    private BigDecimal totalPrice;
}
