package flower_shop.shopingcart.model;

import flower_shop.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int quantity;

    private double totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    private ShoppingCart shoppingCart;

    @OneToOne(fetch = FetchType.EAGER)
    private Product product;
}
