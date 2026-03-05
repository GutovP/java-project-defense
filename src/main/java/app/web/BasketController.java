package app.web;

import app.basket.model.Basket;
import app.basket.service.BasketService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.BasketRequest;
import app.web.dto.BasketResponse;
import app.web.mapper.DtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static app.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/basket")
@Tag(name = "Basket Endpoints", description = "endpoints related to user's basket")
public class BasketController {

    private final BasketService basketService;
    private final UserService userService;

    @Autowired
    public BasketController(BasketService basketService, UserService userService) {
        this.basketService = basketService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BasketResponse> viewBasket(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        String email = authenticationMetadata.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.findUserBasket(user);

        BasketResponse response = DtoMapper.toBasketResponse(basket);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<BasketResponse> addToBasket(@RequestBody BasketRequest basketRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        String email = authenticationMetadata.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.addToBasket(user, basketRequest.getProductId(), basketRequest.getQuantity());

        BasketResponse response = DtoMapper.toBasketResponse(basket);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{productId}/quantity")
    public ResponseEntity<BasketResponse> updateItemQuantity(@PathVariable UUID productId, @RequestParam int newQuantity, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        String email = authenticationMetadata.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.updateBasketItemQuantity(user, productId, newQuantity);

        BasketResponse response = DtoMapper.toBasketResponse(basket);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{basketItemId}")
    public ResponseEntity<BasketResponse> removeFromBasket(@PathVariable UUID basketItemId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        String email = authenticationMetadata.getUsername();
        User user = userService.getUserByEmail(email);

        Basket basket = basketService.removeBasketItem(user, basketItemId);

        BasketResponse response = DtoMapper.toBasketResponse(basket);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
