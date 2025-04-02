package flower_shop.email.client;

import flower_shop.email.client.dto.EmailRequest;
import flower_shop.email.client.dto.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "email-service", url = "http://localhost:8081/api/v1/email")
public interface EmailClient {

    @GetMapping("/history")
    List<EmailResponse> getEmailHistory();

    @PostMapping("/send")
    ResponseEntity<String> sendEmail(@RequestBody EmailRequest request);
}
