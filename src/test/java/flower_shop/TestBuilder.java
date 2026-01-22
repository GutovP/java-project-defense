package flower_shop;

import flower_shop.basket.model.Basket;
import flower_shop.basket.model.BasketItem;
import flower_shop.product.model.Product;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

        public static User aRandomUser() {

            User user = User.builder()
                    .id(UUID.randomUUID())
                    .firstName("existingFirst")
                    .lastName("existingLast")
                    .email("existing@example.com")
                    .password("123123")
                    .role(UserRole.USER)
                    .build();

            Basket basket = Basket.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .totalPrice(BigDecimal.ONE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            BasketItem basketItem = BasketItem.builder()
                    .id(UUID.randomUUID())
                    .quantity(5)
                    .build();

            Product product = Product.builder()
                    .id(UUID.randomUUID())
                    .name("SomeProduct")
                    .description("Description")
                    .costPrice(BigDecimal.ONE)
                    .salePrice(BigDecimal.TEN)
                    .currentQuantity(7)
                    .image("www.image.com")
                    .category("SomeCategory")
                    .restockThreshold(1)
                    .inactive(false)
                    .build();

            user.setBaskets(List.of(basket));
            basket.setItems(List.of(basketItem));
            product.setName(product.getName());
            return user;
        }
}
