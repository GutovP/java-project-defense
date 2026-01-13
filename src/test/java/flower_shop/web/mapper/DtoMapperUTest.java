package flower_shop.web.mapper;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.product.model.Product;
import flower_shop.web.dto.BasketItemResponse;
import flower_shop.web.dto.BasketResponse;
import flower_shop.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DtoMapperUTest {

    @Test
    void whenBasketIsMapped_thenBasketResponseIsCorrect() {

        // Given
        Product product1 = Product.builder()
                .name("Product A")
                .salePrice(new BigDecimal("10.00"))
                .image("imageA.png")
                .build();

        Product product2 = Product.builder()
                .name("Product B")
                .salePrice(new BigDecimal("5.50"))
                .image("imageB.png")
                .build();

        BasketItem item1 = BasketItem.builder()
                .id(UUID.randomUUID())
                .product(product1)
                .quantity(2)
                .build();

        BasketItem item2 = BasketItem.builder()
                .id(UUID.randomUUID())
                .product(product2)
                .quantity(3)
                .build();

        Basket basket = Basket.builder()
                .items(List.of(item1, item2))
                .totalPrice(new BigDecimal("36.50"))
                .build();

        // When
        BasketResponse basketResponse = DtoMapper.toBasketResponse(basket);

        // Then
        assertEquals(2, basketResponse.getItems().size());

        BasketItemResponse firstItem = basketResponse.getItems().getFirst();
        assertEquals(item1.getId(), firstItem.getBasketId());
        assertEquals(product1.getName(), firstItem.getProductName());
        assertEquals(product1.getSalePrice(), firstItem.getProductPrice());
        assertEquals(product1.getImage(), firstItem.getImage());
        assertEquals(item1.getQuantity(), firstItem.getQuantity());
        assertEquals(product1.getSalePrice().multiply(BigDecimal.valueOf(item1.getQuantity())), firstItem.getItemTotalPrice());

        BasketItemResponse secondItem = basketResponse.getItems().get(1);
        assertEquals(item2.getId(), secondItem.getBasketId());
        assertEquals(product2.getName(), secondItem.getProductName());
        assertEquals(product2.getSalePrice(), secondItem.getProductPrice());
        assertEquals(product2.getImage(), secondItem.getImage());
        assertEquals(item2.getQuantity(), secondItem.getQuantity());
        assertEquals(product2.getSalePrice().multiply(BigDecimal.valueOf(item2.getQuantity())), secondItem.getItemTotalPrice());

        assertEquals(basket.getTotalPrice(), basketResponse.getTotalPrice());
    }

    @Test
    void whenProductIsMapped_thenProductResponseIsCorrect() {

        // Given
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Laptop")
                .description("Powerful machine")
                .salePrice(new BigDecimal("999.99"))
                .category("Electronics")
                .image("laptop.png")
                .currentQuantity(7)
                .build();

        // When
        ProductResponse response = DtoMapper.toProductResponse(product);

        // Then
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getSalePrice(), response.getSalePrice());
        assertEquals(product.getCategory(), response.getCategoryName());
        assertEquals(product.getImage(), response.getImage());
        assertEquals(product.getCurrentQuantity(), response.getCurrentQuantity());
    }

}
