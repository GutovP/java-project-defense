package app.web.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

    @NotEmpty
    private List<String> images;

    @NotBlank
    private String category;
}
