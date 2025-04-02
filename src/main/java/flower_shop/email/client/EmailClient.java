package flower_shop.email.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "email-service", url = "http://localhost:8080/api/v1/email")
public interface EmailClient {

    @GetMapping("/history")
    String getEmail();

    @PostMapping("/send")
    String sendEmail();
}
