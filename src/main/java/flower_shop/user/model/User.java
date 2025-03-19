package flower_shop.user.model;

import flower_shop.order.model.Order;
import flower_shop.shopingcart.model.ShoppingCart;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "customer")
    private ShoppingCart shoppingCart;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();
    
}
