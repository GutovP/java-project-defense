package flower_shop.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private BigDecimal costPrice;

    @Column
    private BigDecimal salePrice;

    @Column
    private int currentQuantity;

    @Column
    private String image;

    @Column
    private String category;

    @Column(nullable = false)
    private Integer restockThreshold = 5;
}
