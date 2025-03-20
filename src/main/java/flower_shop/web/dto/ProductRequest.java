package flower_shop.web.dto;

import flower_shop.product.model.Category;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private BigDecimal salePrice;

    @NotBlank
    private int quantity;

    @NotBlank
    private String image;

    @NotBlank
    private Category category;
}
