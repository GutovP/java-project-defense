package flower_shop.web;


import flower_shop.basket.model.Basket;
import flower_shop.basket.service.BasketService;
import flower_shop.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/basket")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToBasket(@RequestParam UUID productId, @RequestParam int quantity, @AuthenticationPrincipal UserDetails userDetails) {

        Basket basket = basketService.addToBasket((User) userDetails, productId, quantity);

        return ResponseEntity.ok("Item added to basket");
    }
}
