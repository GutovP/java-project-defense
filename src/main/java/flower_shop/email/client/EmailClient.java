package flower_shop.email.client;

import flower_shop.email.client.dto.RestockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "notification-service", url = "http://localhost:8081/api/v1/notifications")
public interface EmailClient {

    @PostMapping("/restockAlert")
    ResponseEntity<Void> sendRestockAlert(@RequestBody RestockRequest request);
}
