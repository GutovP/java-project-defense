package flower_shop.product.service;

import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import flower_shop.web.dto.UpdateQuantityRequest;
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

    public List<ProductResponse> getAllProducts(String userRole) {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> userRole.equals("ROLE_ADMIN") || product.getCurrentQuantity() > 0)
                .map(product -> new ProductResponse(
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage(),
                        product.getCurrentQuantity()
                )).collect(Collectors.toList());
    }

    public List<ProductResponse> getProduct(String categoryName, String productName) {

        Optional<Product> optionalProduct = productRepository.findByName(productName);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();

            if (product.getCategory() != null && product.getCategory().equals(categoryName)) {
                return List.of(new ProductResponse(
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage(),
                        product.getCurrentQuantity()
                ));
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

    public Product updateProductQuantity(String category, String productName, UpdateQuantityRequest updateQuantityRequest) {

        Optional<Product> optionalProduct = productRepository.findByCategoryAndName(productName, category);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setCurrentQuantity(updateQuantityRequest.getNewQuantity());
            return productRepository.save(product);
        }
        throw new ProductNotFoundException("Product not found in the specified category");
    }
}
