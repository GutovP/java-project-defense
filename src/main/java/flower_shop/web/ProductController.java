package flower_shop.web;

import flower_shop.product.model.Product;
import flower_shop.product.service.ProductService;
import flower_shop.security.AuthenticationMetadata;
import flower_shop.user.model.UserRole;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import flower_shop.web.dto.UpdateQuantityRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = (authenticationMetadata != null) ? authenticationMetadata.getUserRole() : UserRole.USER;

        return productService.getAllProducts(userRole);
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {

        return productService.getAllCategories();
    }

    @GetMapping("/{category}/{name}")
    public List<ProductResponse> getProduct(@PathVariable String category, @PathVariable String name, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = (authenticationMetadata != null) ? authenticationMetadata.getUserRole() : UserRole.USER;

        return productService.getProduct(category, name, userRole);
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

    @PostMapping("/add-new-product")
    @PreAuthorize("hasRole('ADMIN')")
    public Product addNewProduct(@RequestBody @Valid ProductRequest productRequest) {

        return productService.addNewProduct(productRequest);
    }

}
