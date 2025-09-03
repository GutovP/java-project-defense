package flower_shop.web.mapper;

import flower_shop.basket.model.Basket;
import flower_shop.product.model.Product;
import flower_shop.web.dto.BasketItemResponse;
import flower_shop.web.dto.BasketResponse;
import flower_shop.web.dto.ProductResponse;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class DtoMapper {

    public static BasketResponse toBasketResponse(Basket basket) {

        List<BasketItemResponse> itemsResponse = basket.getItems().stream()
                .map( item -> new BasketItemResponse(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getProduct().getSalePrice(),
                        item.getProduct().getImage(),
                        item.getQuantity(),
                        item.getProduct().getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()))

                )).toList();

        return new BasketResponse(itemsResponse, basket.getTotalPrice());
    }

    public static ProductResponse toProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .categoryName(product.getCategory())
                .image(product.getImage())
                .currentQuantity(product.getCurrentQuantity())
                .build();
    }
}
