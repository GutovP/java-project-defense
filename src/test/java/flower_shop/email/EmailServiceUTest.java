package flower_shop.email;

import flower_shop.email.client.EmailClient;
import flower_shop.email.client.dto.EmailRequest;
import flower_shop.email.client.dto.EmailResponse;
import flower_shop.email.service.EmailService;
import flower_shop.product.model.Product;
import flower_shop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceUTest {

    @Mock
    private ProductRepository productRepository;

    @Mock private EmailClient emailClient;


    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldSendRestockAlertWhenProductsAreLowStock() {
        Product product1 = Product.builder()
                .name("Product1")
                .currentQuantity(2)
                .build();

        Product product2 = Product.builder()
                .name("Product2")
                .currentQuantity(1)
                .build();

        List<Product> lowStockProducts = List.of(product1, product2);
        when(productRepository.findByCurrentQuantityLessThanThreshold()).thenReturn(lowStockProducts);

        emailService.checkProducts();

        verify(emailClient).sendEmail(any(EmailRequest.class));
    }

    @Test
    void shouldNotSendEmailIfNoLowStockProducts() {
        when(productRepository.findByCurrentQuantityLessThanThreshold()).thenReturn(Collections.emptyList());

        emailService.checkProducts();

        verify(emailClient, never()).sendEmail(any(EmailRequest.class));
    }

    @Test
    void shouldFetchEmailHistorySuccessfully() {
        List<EmailResponse> mockHistory = List.of(
                new EmailResponse("admin@google.com", "Restock Alert", LocalDateTime.now()),
                new EmailResponse("admin@google.com", "Restock Alert", LocalDateTime.now())
        );

        when(emailClient.getEmailHistory()).thenReturn(mockHistory);

        List<EmailResponse> emailHistory = emailService.fetchEmailHistory();

        assertEquals(2, emailHistory.size());
        assertEquals("Restock Alert", emailHistory.getFirst().subject());
        verify(emailClient).getEmailHistory();
    }
}
