package flower_shop.product.service;

import flower_shop.exception.AuthorizationDeniedException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import flower_shop.user.model.UserRole;
import flower_shop.web.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(UserRole userRole) {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> userRole == UserRole.ADMIN || product.getCurrentQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(String categoryName, UserRole userRole) {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> (userRole == UserRole.ADMIN || product.getCurrentQuantity() > 0) && product.getCategory().equals(categoryName))
                .collect(Collectors.toList());
    }

    public Product getProduct(String categoryName, String productName,  UserRole userRole) {

        return productRepository.findByName(productName)
                .filter(product -> product.getCategory() != null && product.getCategory().equals(categoryName))
                .filter(product -> userRole == UserRole.ADMIN || product.getCurrentQuantity() > 0)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in the specified category"));


    }

    public Product createNewProduct(ProductRequest productRequest) {

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .salePrice(productRequest.getSalePrice())
                .image(productRequest.getImage())
                .currentQuantity(productRequest.getQuantity())
                .category(productRequest.getCategory())
                .restockThreshold(5)
                .inactive(false)
                .build();

        productRepository.save(product);

        return product;
    }

    public boolean updateProductQuantity(String category, String productName, int newQuantity, UserRole userRole) {

        Optional<Product> optionalProduct = productRepository.findByCategoryAndName(category, productName);

        if (userRole != UserRole.ADMIN) {
            throw new AuthorizationDeniedException("You do not have permission to update this product");
        }

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setCurrentQuantity(newQuantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public List<String> getAllCategories() {

        return productRepository.findAllCategories();
    }

    public void removeProduct(UUID productId) {

        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();
            product.setInactive(true);
            productRepository.save(product);

        }
    }
}
