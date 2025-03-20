package flower_shop.web;

import flower_shop.product.service.ProductService;
import flower_shop.web.dto.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
