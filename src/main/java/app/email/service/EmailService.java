package app.email.service;

import app.email.client.EmailClient;
import app.email.client.dto.RestockRequest;
import app.product.model.Product;
import app.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {

    private final ProductRepository productRepository;
    private final EmailClient emailClient;

    @Autowired
    public EmailService(ProductRepository productRepository, EmailClient emailClient) {

        this.productRepository = productRepository;
        this.emailClient = emailClient;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void checkProducts() {

        List<Product> lowStockProducts = productRepository.findByCurrentQuantityLessThanThreshold();

        if (!lowStockProducts.isEmpty()) {

            sendRestockAlert(lowStockProducts);

            for (Product product : lowStockProducts) {
                product.setRestockAlertSent(true);
            }
            productRepository.saveAll(lowStockProducts);

            log.info("Restock-Alert sent für {} products.", lowStockProducts.size());
        }

    }

    private void sendRestockAlert(List<Product> lowStockProducts) {

        String body = "The following products need restocking:\n" +
                lowStockProducts.stream()
                        .map(product -> product.getName() + " (Stock: " + product.getCurrentQuantity() + ")")
                        .collect(Collectors.joining("\n"));

        RestockRequest request = new RestockRequest("petar.gutov@gmail.com", "Restock Alert", body);

        emailClient.sendRestockAlert(request);

    }

}
