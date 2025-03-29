package flower_shop.web;


import flower_shop.basket.model.Basket;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.basket.service.BasketService;
import flower_shop.user.model.User;
import flower_shop.web.dto.BasketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/basket")
public class BasketController {

    private final BasketService basketService;
    private final BasketRepository basketRepository;

    @Autowired
    public BasketController(BasketService basketService, BasketRepository basketRepository) {
        this.basketService = basketService;
        this.basketRepository = basketRepository;
    }

    @GetMapping("/view")
    public ResponseEntity<BasketResponse> viewBasket(@AuthenticationPrincipal User user) {

        Basket basket = basketRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Basket not found"));

        return ResponseEntity.ok(new BasketResponse(basket));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToBasket(@RequestParam UUID productId, @RequestParam int quantity, @AuthenticationPrincipal User user) {

        Basket basket = basketService.addToBasket( user, productId, quantity);

        return ResponseEntity.ok("Item added to basket");
    }

    @PutMapping("{itemId}/quantity")
    public ResponseEntity<BasketResponse> updateItemQuantity(@PathVariable UUID itemId, @RequestParam int newQuantity, @AuthenticationPrincipal User user) {

        Basket basket = basketService.updateBasketItemQuantity(user, itemId, newQuantity);

        return ResponseEntity.ok(new BasketResponse(basket));
    }
}
