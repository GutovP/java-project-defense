package flower_shop.order.model;

import flower_shop.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime orderDate;

    private String orderStatus;

    private String paymentStatus;

    private String paymentType;

    private String transactionId;

    private double totalPrice;

    private double discountPrice;

    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    private User customer;
}
