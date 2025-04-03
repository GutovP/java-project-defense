package flower_shop.product;

import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.product.service.ProductService;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldGetAllProductsForAdmin() {
        Product product1 = Product.builder()
                .name("Product1")
                .description("Description1")
                .salePrice(BigDecimal.valueOf(100))
                .category("Category1")
                .image("www.image.com")
                .currentQuantity(10)
                .build();

        Product product2 = Product.builder()
                .name("Product2")
                .description("Description2")
                .salePrice(BigDecimal.valueOf(200))
                .category("Category2")
                .image("www.image1.com")
                .currentQuantity(0)
                .build();

        List<Product> mockProducts = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(mockProducts);

        List<ProductResponse> response = productService.getAllProducts("ROLE_ADMIN");

        assertEquals(2, response.size());
        verify(productRepository).findAll();
    }

    @Test
    void shouldGetOnlyAvailableProductsForUser() {
        Product product1 = Product.builder()
                .name("Product1")
                .description("Description1")
                .salePrice(BigDecimal.valueOf(100))
                .category("Category1")
                .image("www.image.com")
                .currentQuantity(10)
                .build();

        Product product2 = Product.builder()
                .name("Product2")
                .description("Description2")
                .salePrice(BigDecimal.valueOf(200))
                .category("Category2")
                .image("www.image1.com")
                .currentQuantity(0)
                .build();

        List<Product> mockProducts = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(mockProducts);

        List<ProductResponse> response = productService.getAllProducts("ROLE_USER");

        assertEquals(1, response.size());
        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductIfCategoryMatches() {
        Product product = Product.builder()
                .name("Product1")
                .description("Description1")
                .salePrice(BigDecimal.valueOf(100))
                .category("Category1")
                .image("www.image.com")
                .currentQuantity(10)
                .build();

        when(productRepository.findByName("Product1")).thenReturn(Optional.of(product));

        List<ProductResponse> response = productService.getProduct("Category1", "Product1", "ROLE_ADMIN");

        assertEquals(1, response.size());
        assertEquals(product.getName(), response.get(0).getName());
        verify(productRepository).findByName("Product1");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findByName("UnknownProduct")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct("Category1", "UnknownProduct", "ROLE_ADMIN"));
        verify(productRepository).findByName("UnknownProduct");
    }

    @Test
    void shouldAddNewProductSuccessfully() {
        ProductRequest request = ProductRequest.builder()
                .name("Product1")
                .description("Description1")
                .salePrice(BigDecimal.valueOf(100))
                .category("Category1")
                .image("www.image.com")
                .quantity(10)
                .build();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .salePrice(request.getSalePrice())
                .category(request.getCategory())
                .image(request.getImage())
                .currentQuantity(request.getQuantity())
                .restockThreshold(5)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.addNewProduct(request);

        assertNotNull(savedProduct);
        assertEquals(request.getName(), savedProduct.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProductQuantitySuccessfully() {
        Product product = Product.builder()
                .name("Product1")
                .description("Description1")
                .salePrice(BigDecimal.valueOf(100))
                .category("Category1")
                .image("www.image.com")
                .currentQuantity(10)
                .build();

        when(productRepository.findByCategoryAndName("Category1", "Product1")).thenReturn(Optional.of(product));

        boolean updated = productService.updateProductQuantity("Category1", "Product1", 15);

        assertTrue(updated);
        assertEquals(15, product.getCurrentQuantity());
        verify(productRepository).findByCategoryAndName("Category1", "Product1");
        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnFalseWhenUpdatingNonExistentProduct() {
        when(productRepository.findByCategoryAndName("Category1", "NonExistentProduct")).thenReturn(Optional.empty());

        boolean updated = productService.updateProductQuantity("Category1", "NonExistentProduct", 15);

        assertFalse(updated);
        verify(productRepository).findByCategoryAndName("Category1", "NonExistentProduct");
        verify(productRepository, never()).save(any(Product.class));
    }

}
