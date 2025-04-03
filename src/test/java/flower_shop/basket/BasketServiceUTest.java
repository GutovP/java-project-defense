package flower_shop.basket;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.basket.repository.BasketItemRepository;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.basket.service.BasketService;
import flower_shop.exception.BasketItemNotFoundException;
import flower_shop.exception.NotEnoughInStockException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceUTest {

    @Mock
    private ProductRepository productRepository;
    @Mock private BasketRepository basketRepository;
    @Mock private BasketItemRepository basketItemRepository;

    @InjectMocks
    private BasketService basketService;

    @Test
    void shouldAddProductToBasketSuccessfully() {
        User user = User.builder().email("user@email.com").build();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .name("Product1")
                .salePrice(BigDecimal.valueOf(50))
                .currentQuantity(10)
                .build();

        Basket basket = Basket.builder()
                .user(user)
                .items(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(basketRepository.findByUser(user)).thenReturn(Optional.of(basket));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        Basket updatedBasket = basketService.addToBasket(user, productId, 2);

        assertNotNull(updatedBasket);
        assertEquals(1, updatedBasket.getItems().size());
        assertEquals(BigDecimal.valueOf(100), updatedBasket.getTotalPrice());
        verify(productRepository).save(product);
        verify(basketItemRepository).save(any(BasketItem.class));
        verify(basketRepository).save(basket);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughStock() {
        User user = User.builder().email("user@email.com").build();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .name("Product1")
                .salePrice(BigDecimal.valueOf(50))
                .currentQuantity(1)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(NotEnoughInStockException.class, () -> basketService.addToBasket(user, productId, 5));
        verify(productRepository, never()).save(any(Product.class));
        verify(basketItemRepository, never()).save(any(BasketItem.class));
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    void shouldUpdateBasketItemQuantitySuccessfully() {
        User user = User.builder().email("user@email.com").build();
        UUID basketItemId = UUID.randomUUID();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product1")
                .salePrice(BigDecimal.valueOf(50))
                .currentQuantity(10)
                .build();

        BasketItem basketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .quantity(2)
                .build();

        Basket basket = Basket.builder()
                .user(user)
                .items(List.of(basketItem))
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        when(basketRepository.findByUser(user)).thenReturn(Optional.of(basket));
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(basketItem));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        Basket updatedBasket = basketService.updateBasketItemQuantity(user, basketItemId, 4);

        assertEquals(4, basketItem.getQuantity());
        assertEquals(BigDecimal.valueOf(200), updatedBasket.getTotalPrice());
        verify(productRepository).save(product);
        verify(basketItemRepository).save(basketItem);
        verify(basketRepository).save(basket);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingQuantityButNotEnoughStock() {
        User user = User.builder().email("user@email.com").build();
        UUID basketItemId = UUID.randomUUID();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product1")
                .salePrice(BigDecimal.valueOf(50))
                .currentQuantity(1)
                .build();

        BasketItem basketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .quantity(2)
                .build();

        Basket basket = Basket.builder()
                .user(user)
                .items(List.of(basketItem))
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        when(basketRepository.findByUser(user)).thenReturn(Optional.of(basket));
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(basketItem));

        assertThrows(NotEnoughInStockException.class, () -> basketService.updateBasketItemQuantity(user, basketItemId, 4));
        verify(productRepository, never()).save(any(Product.class));
        verify(basketItemRepository, never()).save(any(BasketItem.class));
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    void shouldRemoveBasketItemSuccessfully() {
        User user = User.builder().email("user@email.com").build();
        UUID basketItemId = UUID.randomUUID();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product1")
                .salePrice(BigDecimal.valueOf(50))
                .currentQuantity(5)
                .build();

        BasketItem basketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .quantity(2)
                .build();

        Basket basket = Basket.builder()
                .user(user)
                .items(new ArrayList<>(List.of(basketItem)))
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        when(basketRepository.findByUser(user)).thenReturn(Optional.of(basket));
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(basketItem));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        Basket updatedBasket = basketService.removeBasketItem(user, basketItemId);

        assertEquals(0, updatedBasket.getItems().size());
        assertEquals(BigDecimal.ZERO, updatedBasket.getTotalPrice());
        verify(basketItemRepository).delete(basketItem);
        verify(basketRepository).save(basket);
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonexistentBasketItem() {
        User user = User.builder().email("user@email.com").build();
        UUID basketItemId = UUID.randomUUID();

        when(basketRepository.findByUser(user)).thenReturn(Optional.of(Basket.builder().user(user).build()));
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.empty());

        assertThrows(BasketItemNotFoundException.class, () -> basketService.removeBasketItem(user, basketItemId));
        verify(basketItemRepository, never()).delete(any());
        verify(basketRepository, never()).save(any());
    }
}
