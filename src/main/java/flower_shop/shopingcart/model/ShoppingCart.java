package flower_shop.shopingcart.model;

import flower_shop.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int totalItems;

    private int totalPrice;

    @OneToOne(fetch = FetchType.EAGER)
    private User customer;
}
