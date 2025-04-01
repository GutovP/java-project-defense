package flower_shop.basket.service;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.basket.repository.BasketItemRepository;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.user.model.User;
import flower_shop.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BasketService {
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;

    @Autowired
    public BasketService(ProductRepository productRepository, BasketRepository basketRepository, BasketItemRepository basketItemRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
        this.basketItemRepository = basketItemRepository;
    }

    public Basket addToBasket(User user, UUID productId, int quantity) {

        Basket basket = basketRepository.findByUser(user).orElseGet(() -> {
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            newBasket.setCreatedAt(LocalDateTime.now());
            newBasket.setUpdatedAt(LocalDateTime.now());
            newBasket.setTotalPrice(BigDecimal.ZERO);
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

        BigDecimal updatedTotalPrice = calculateTotalPrice(basket);
        basket.setTotalPrice(updatedTotalPrice);
        basket.setUpdatedAt(LocalDateTime.now());

        return basketRepository.save(basket);
    }

    public BigDecimal calculateTotalPrice(Basket basket) {
        return basket.getItems().stream()
                .map(this::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getItemTotalPrice(BasketItem basketItem) {

        return basketItem.getProduct().getSalePrice().multiply(BigDecimal.valueOf(basketItem.getQuantity()));
    }

    public Basket updateBasketItemQuantity(User user, UUID basketItemId, int newQuantity) {

        Basket basket = basketRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Basket not found."));

        BasketItem basketItem = basketItemRepository.findById(basketItemId).orElseThrow(() -> new RuntimeException("BasketItem not found."));
        basketItem.setQuantity(newQuantity);

        BigDecimal updatedTotalPrice = calculateTotalPrice(basket);
        basket.setTotalPrice(updatedTotalPrice);
        basket.setUpdatedAt(LocalDateTime.now());

        return basketRepository.save(basket);
    }

}
