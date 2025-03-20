package flower_shop.product.service;

import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.web.dto.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public ProductResponse getProduct(String categoryName, String productName) {

        Optional<Product> optionalProduct = productRepository.findByName(productName);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();

            if (product.getCategory() != null && product.getCategory().getName().equals(categoryName)) {
                return new ProductResponse(
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory().getName(),
                        product.getImage()
                );
            }
        }

        throw new ProductNotFoundException("Product not found in the specified category");
    }
}
