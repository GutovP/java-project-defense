package flower_shop.web.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
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
    private String category;
}
