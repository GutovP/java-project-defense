package flower_shop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.basket.model.Basket;
import flower_shop.basket.repository.BasketRepository;
import flower_shop.basket.service.BasketService;
import flower_shop.exception.BasketItemNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.BasketRequest;
import flower_shop.web.dto.BasketResponse;
import flower_shop.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BasketControllerApiTest {

    @MockitoBean
    private BasketService basketService;

    @MockitoBean
    private BasketRepository basketRepository;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api/v1/basket";

    @Test
    void shouldViewBasketSuccessfully() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();
        Basket basket = Basket.builder().user(user).totalPrice(BigDecimal.TEN).items(new ArrayList<>()).build();
        BasketResponse basketResponse = new BasketResponse(List.of(), BigDecimal.TEN);

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketRepository.findByUser(user)).thenReturn(Optional.of(basket));

        try (MockedStatic<DtoMapper> mockedStatic = Mockito.mockStatic(DtoMapper.class)) {
            mockedStatic.when(() -> DtoMapper.mapBasketToBasketResponse(basket)).thenReturn(basketResponse);

            mockMvc.perform(get(BASE_URL + "/view")
                            .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPrice").value(10));

            verify(userService).getUserByEmail("admin@gmail.com");
            verify(basketRepository).findByUser(user);
        }
    }

    @Test
    void shouldThrowExceptionWhenBasketNotFound() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketRepository.findByUser(user)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/view")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail("admin@gmail.com");
        verify(basketRepository).findByUser(user);
    }

    @Test
    void shouldAddToBasketSuccessfully() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();
        BasketRequest request = new BasketRequest(UUID.randomUUID(), 2);
        Basket basket = Basket.builder().user(user).totalPrice(BigDecimal.TEN).build();
        BasketResponse basketResponse = new BasketResponse(List.of(), BigDecimal.TEN);

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketService.addToBasket(any(), any(), anyInt())).thenReturn(basket);

        try (MockedStatic<DtoMapper> mockedStatic = Mockito.mockStatic(DtoMapper.class)) {
            mockedStatic.when(() -> DtoMapper.mapBasketToBasketResponse(basket)).thenReturn(basketResponse);

            mockMvc.perform(post(BASE_URL + "/add")
                            .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPrice").value(10));

            verify(userService).getUserByEmail("admin@gmail.com");
            verify(basketService).addToBasket(any(), any(), anyInt());
        }
    }

    @Test
    void shouldUpdateItemQuantitySuccessfully() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();
        UUID productId = UUID.randomUUID();
        Basket basket = Basket.builder().user(user).totalPrice(BigDecimal.TEN).build();
        BasketResponse basketResponse = new BasketResponse(List.of(), BigDecimal.TEN);

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketService.updateBasketItemQuantity(any(), any(), anyInt())).thenReturn(basket);

        try (MockedStatic<DtoMapper> mockedStatic = Mockito.mockStatic(DtoMapper.class)) {
            mockedStatic.when(() -> DtoMapper.mapBasketToBasketResponse(basket)).thenReturn(basketResponse);

            mockMvc.perform(put(BASE_URL + "/" + productId + "/quantity")
                            .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                            .param("newQuantity", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPrice").value(10));

            verify(userService).getUserByEmail("admin@gmail.com");
            verify(basketService).updateBasketItemQuantity(any(), any(), anyInt());
        }
    }

    @Test
    void shouldRemoveItemFromBasketSuccessfully() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();
        UUID basketItemId = UUID.randomUUID();
        Basket basket = Basket.builder().user(user).totalPrice(BigDecimal.TEN).build();
        BasketResponse basketResponse = new BasketResponse(List.of(), BigDecimal.TEN);

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketService.removeBasketItem(any(), any())).thenReturn(basket);

        try (MockedStatic<DtoMapper> mockedStatic = Mockito.mockStatic(DtoMapper.class)) {
            mockedStatic.when(() -> DtoMapper.mapBasketToBasketResponse(basket)).thenReturn(basketResponse);

            mockMvc.perform(delete(BASE_URL + "/" + basketItemId + "/remove")
                            .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPrice").value(10));

            verify(userService).getUserByEmail("admin@gmail.com");
            verify(basketService).removeBasketItem(any(), any());
        }
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonexistentItem() throws Exception {
        User user = User.builder().email("admin@gmail.com").build();
        UUID basketItemId = UUID.randomUUID();

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(user);
        when(basketService.removeBasketItem(any(), any())).thenThrow(new BasketItemNotFoundException("Item not found"));

        mockMvc.perform(delete(BASE_URL + "/" + basketItemId + "/remove")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@gmail.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail("admin@gmail.com");
        verify(basketService).removeBasketItem(any(), any());
    }

}
