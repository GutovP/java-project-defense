package flower_shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private String name;

    private String description;

    private BigDecimal salePrice;

    private String categoryName;

    private String image;

    private int currentQuantity;
}
