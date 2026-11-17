package app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal salePrice;

    private String categoryName;

    private List<String> images;

    private int currentQuantity;
}
