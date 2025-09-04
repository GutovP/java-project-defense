package flower_shop.web;

import flower_shop.product.model.Product;
import flower_shop.product.service.ProductService;
import flower_shop.security.AuthenticationMetadata;
import flower_shop.user.model.UserRole;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import flower_shop.web.dto.UpdateQuantityRequest;
import flower_shop.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = (authenticationMetadata != null) ? authenticationMetadata.getUserRole() : UserRole.USER;

        List<ProductResponse> productsResponse = productService.getAllProducts(userRole).stream()
                .map(DtoMapper::toProductResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(productsResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {

        List<String> categories = productService.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = (authenticationMetadata != null) ? authenticationMetadata.getUserRole() : UserRole.USER;

        List<ProductResponse> productsResponse = productService.getProductsByCategory(category, userRole).stream()
                .map(DtoMapper::toProductResponse)
                .toList();

       return ResponseEntity.status(HttpStatus.OK).body(productsResponse);
    }

    @GetMapping("/{category}/{name}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String category, @PathVariable String name, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = (authenticationMetadata != null) ? authenticationMetadata.getUserRole() : UserRole.USER;

        Product product = productService.getProduct(category, name, userRole);

        ProductResponse productResponse = DtoMapper.toProductResponse(product);

        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }


    @PutMapping("/{category}/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateQuantity(@PathVariable String category, @PathVariable String name,
                                               @RequestBody UpdateQuantityRequest updateQuantityRequest,
                                               @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

       UserRole userRole = authenticationMetadata.getUserRole();

       boolean isUpdated = productService.updateProductQuantity(category, name, updateQuantityRequest.getQuantity(),  userRole);

        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createNewProduct(@RequestBody @Valid ProductRequest productRequest) {

        Product product = productService.createNewProduct(productRequest);

        ProductResponse productResponse = DtoMapper.toProductResponse(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<Void> removeProduct(@PathVariable UUID productId) {

        productService.removeProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
