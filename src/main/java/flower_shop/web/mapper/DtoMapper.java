package flower_shop.web.mapper;

import flower_shop.basket.model.Basket;
import flower_shop.web.dto.BasketItemResponse;
import flower_shop.web.dto.BasketResponse;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class DtoMapper {

    public static BasketResponse mapBasketToBasketResponse(Basket basket) {

        List<BasketItemResponse> itemResponses = basket.getItems().stream()
                .map( item -> new BasketItemResponse(
                       item.getId(),
                        item.getProduct().getName(),
                        item.getProduct().getSalePrice(),
                        item.getQuantity(),
                        item.getProduct().getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()))

                )).toList();

        return new BasketResponse(itemResponses, basket.getTotalPrice());
    }
}
