package flower_shop.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private String name;

    private String description;

    private BigDecimal salePrice;

    private String categoryName;

    private String image;
}
