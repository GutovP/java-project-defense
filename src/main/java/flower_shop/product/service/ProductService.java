package flower_shop.product.service;

import flower_shop.exception.ProductNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.web.dto.ProductRequest;
import flower_shop.web.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts(String userRole) {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> userRole.equals("ROLE_ADMIN") || product.getCurrentQuantity() > 0)
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage(),
                        product.getCurrentQuantity()
                )).collect(Collectors.toList());
    }

    public List<ProductResponse> getProduct(String categoryName, String productName, String userRole) {

        Optional<Product> optionalProduct = productRepository.findByName(productName);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();

            if (product.getCategory() != null && product.getCategory().equals(categoryName)) {

                int showQuantity = userRole.equals("ROLE_ADMIN") ? product.getCurrentQuantity() : 0;

                return List.of(new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getCategory(),
                        product.getImage(),
                        showQuantity
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
                .restockThreshold(5)
                .build();

        return productRepository.save(product);
    }

    public boolean updateProductQuantity(String category, String productName, int newQuantity) {

        Optional<Product> optionalProduct = productRepository.findByCategoryAndName(category, productName);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setCurrentQuantity(newQuantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }
}
