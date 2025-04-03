package flower_shop.web.mapper;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.product.model.Product;
import flower_shop.web.dto.BasketItemResponse;
import flower_shop.web.dto.BasketResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DtoMapperUTest {

    @Test
    void shouldMapBasketToBasketResponseSuccessfully() {

        Product product = Product.builder()
                .name("Rose Bouquet")
                .salePrice(BigDecimal.valueOf(20))
                .image("www.image.com")
                .build();

        BasketItem basketItem = BasketItem.builder()
                .id(UUID.randomUUID())
                .product(product)
                .quantity(2)
                .build();

        Basket basket = Basket.builder()
                .items(List.of(basketItem))
                .totalPrice(BigDecimal.valueOf(40))
                .build();

        BasketResponse basketResponse = DtoMapper.mapBasketToBasketResponse(basket);


        assertNotNull(basketResponse);
        assertEquals(1, basketResponse.getItems().size());
        assertEquals(basket.getTotalPrice(), basketResponse.getTotalPrice());

        BasketItemResponse itemResponse = basketResponse.getItems().getFirst();
        assertEquals(basketItem.getId(), itemResponse.getBasketId());
        assertEquals(product.getName(), itemResponse.getProductName());
        assertEquals(product.getSalePrice(), itemResponse.getProductPrice());
        assertEquals(product.getImage(), itemResponse.getImage());
        assertEquals(basketItem.getQuantity(), itemResponse.getQuantity());
        assertEquals(BigDecimal.valueOf(40), itemResponse.getItemTotalPrice());
    }
}
