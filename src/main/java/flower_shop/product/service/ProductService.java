package flower_shop.product.service;

import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> product.getCurrentQuantity() > 0)
                .map(product -> new ProductResponse(
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage()
                )).collect(Collectors.toList());
    }

    public ProductResponse getProduct(String categoryName, String productName) {

        Optional<Product> optionalProduct = productRepository.findByName(productName);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();

            if (product.getCategory() != null && product.getCategory().equals(categoryName)) {
                return new ProductResponse(
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage()
                );
            }
        }

        throw new ProductNotFoundException("Product not found in the specified category");
    }

    public Product addNewProduct(ProductRequest productRequest) {

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .salePrice(productRequest.getSalePrice())
                .image(productRequest.getImage())
                .currentQuantity(productRequest.getQuantity())
                .category(productRequest.getCategory())
                .build();

        return productRepository.save(product);
    }
}
