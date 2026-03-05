package app.web.mapper;

import app.basket.model.Basket;
import app.product.model.Product;
import app.web.dto.BasketItemResponse;
import app.web.dto.BasketResponse;
import app.web.dto.ProductResponse;
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
