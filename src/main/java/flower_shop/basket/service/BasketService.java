package flower_shop.basket.service;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BasketService {
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;

    @Autowired
    public BasketService(ProductRepository productRepository, BasketRepository basketRepository) {
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
    }

    public Basket addToBasket(User user, UUID productId, int quantity) {
        Basket basket = basketRepository.findByUser(user).orElseGet(() -> {
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            newBasket.setCreatedAt(LocalDateTime.now());
            newBasket.setUpdatedAt(LocalDateTime.now());
            return basketRepository.save(newBasket);
        });

        Optional<BasketItem> existingItem = basket.getItems().stream()
                .filter(basketItem -> basketItem.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            BasketItem basketItem = existingItem.get();
            basketItem.setQuantity(basketItem.getQuantity() + quantity);

        } else {
            Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found."));
            BasketItem newBasketItem = new BasketItem();
            newBasketItem.setProduct(product);
            newBasketItem.setQuantity(quantity);
            newBasketItem.setBasket(basket);
            basket.getItems().add(newBasketItem);
        }

        basket.setUpdatedAt(LocalDateTime.now());
        return basketRepository.save(basket);
    }



}
