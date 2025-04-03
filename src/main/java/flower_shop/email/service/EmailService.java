package flower_shop.email.service;

import flower_shop.email.client.EmailClient;
import flower_shop.email.client.dto.EmailRequest;
import flower_shop.email.client.dto.EmailResponse;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final ProductRepository productRepository;
    private final EmailClient emailClient;

    @Autowired
    public EmailService(ProductRepository productRepository, EmailClient emailClient) {

        this.productRepository = productRepository;
        this.emailClient = emailClient;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void checkProducts() {

        List<Product> lowStockProducts = productRepository.findByCurrentQuantityLessThanThreshold();

        if (!lowStockProducts.isEmpty()) {
            sendRestockAlert(lowStockProducts);
        }
    }

    private void sendRestockAlert(List<Product> lowStockProducts) {

        String body = "The following products need restocking:\n" +
                lowStockProducts.stream()
                        .map(p -> p.getName() + " (Stock: " + p.getCurrentQuantity() + ")")
                        .collect(Collectors.joining("\n"));

        EmailRequest request = new EmailRequest("petar.gutov@gmail.com", "Restock Alert", body);

        emailClient.sendEmail(request);

    }

    public List<EmailResponse> fetchEmailHistory() {
        return emailClient.getEmailHistory();
    }

}
