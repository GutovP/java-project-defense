package app.basket.repository;

import app.basket.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, UUID> {
}
