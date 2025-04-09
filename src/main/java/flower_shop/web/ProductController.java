package flower_shop.web;

import flower_shop.product.model.Product;
import flower_shop.product.service.ProductService;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import flower_shop.web.dto.UpdateQuantityRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userRole = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        return productService.getAllProducts(userRole);
    }


    @GetMapping("/{category}/{name}")
    public List<ProductResponse> getProduct(@PathVariable String category, @PathVariable String name) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = "USER";

        if (auth != null && auth.getAuthorities() != null) {
            userRole = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER");
        }

        return productService.getProduct(category, name, userRole);
    }


    @PutMapping("/{category}/{name}")
    public ResponseEntity<?> updateQuantity(@PathVariable String category, @PathVariable String name, @RequestBody UpdateQuantityRequest updateQuantityRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = "USER";

        if (auth != null && auth.getAuthorities() != null) {
            userRole = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER");
        }

        if (!userRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have permission recipient update this product"));
        }

       boolean isUpdated = productService.updateProductQuantity(category, name, updateQuantityRequest.getQuantity());

        if (isUpdated) {
            return ResponseEntity.ok().body(Map.of("message", "Product quantity updated successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found."));
        }

    }

    @PostMapping("/add-new-product")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Product addNewProduct(@RequestBody @Valid ProductRequest productRequest) {

        return productService.addNewProduct(productRequest);
    }

}
