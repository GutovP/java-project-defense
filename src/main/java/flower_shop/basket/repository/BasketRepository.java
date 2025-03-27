package flower_shop.basket.repository;

import flower_shop.basket.model.Basket;
import flower_shop.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasketRepository extends JpaRepository<Basket, UUID> {

    Optional<Basket> findByUser(User user);

    
}
