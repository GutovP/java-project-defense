package flower_shop.web;

import flower_shop.product.model.Product;
import flower_shop.product.service.ProductService;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {

        return productService.getAllProducts();
    }

    @GetMapping("/{category}/{name}")
    public List<ProductResponse> getProduct(@PathVariable String category, @PathVariable String name) {

        return productService.getProduct(category, name);
    }

    @PostMapping("/add-new-product")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Product addNewProduct(@RequestBody @Valid ProductRequest productRequest) {

        return productService.addNewProduct(productRequest);
    }

}
