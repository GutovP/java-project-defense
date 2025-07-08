package flower_shop.product.repository;

import flower_shop.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByName(String name);

    Optional<Product> findByCategoryAndName(String name, @PathVariable String category);

    @Query("SELECT p FROM Product p WHERE p.currentQuantity <= p.restockThreshold")
    List<Product> findByCurrentQuantityLessThanThreshold();

    List<String> findAllCategories();
}
