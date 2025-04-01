package flower_shop.web;


import flower_shop.basket.model.Basket;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.basket.service.BasketService;
import flower_shop.exception.BasketNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.BasketRequest;
import flower_shop.web.dto.BasketResponse;
import flower_shop.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/basket")
public class BasketController {

    private final BasketService basketService;
    private final BasketRepository basketRepository;
    private final UserService userService;

    @Autowired
    public BasketController(BasketService basketService, BasketRepository basketRepository, UserService userService) {
        this.basketService = basketService;
        this.basketRepository = basketRepository;
        this.userService = userService;
    }

    @GetMapping("/view")
    public ResponseEntity<BasketResponse> viewBasket(@AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketRepository.findByUser(user).orElseThrow(() -> new BasketNotFoundException("Basket not found"));

        BasketResponse response = DtoMapper.mapBasketToBasketResponse(basket);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<BasketResponse> addToBasket(@RequestBody BasketRequest basketRequest, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.addToBasket(user, basketRequest.getProductId(), basketRequest.getQuantity());

        BasketResponse response = DtoMapper.mapBasketToBasketResponse(basket);

        return ResponseEntity.ok(response);
    }

    @PutMapping("{productId}/quantity")
    public ResponseEntity<BasketResponse> updateItemQuantity(@PathVariable UUID productId, @RequestParam int newQuantity, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.updateBasketItemQuantity(user, productId, newQuantity);

        BasketResponse response = DtoMapper.mapBasketToBasketResponse(basket);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{basketItemId}/remove")
    public ResponseEntity<BasketResponse> removeFromBasket(@PathVariable UUID basketItemId, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.removeBasketItem(user, basketItemId);

        BasketResponse response = DtoMapper.mapBasketToBasketResponse(basket);
        return ResponseEntity.ok(response);
    }

}
