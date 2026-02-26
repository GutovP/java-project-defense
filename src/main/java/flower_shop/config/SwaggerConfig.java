package flower_shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info applicationInfo = new Info()
                .title("Flower Shop REST API")
                .description("Online shop")
                .version("1.0")
                .contact(new Contact()
                        .name("Petar Gutov")
                        .email("pgutov@gmail.com")
                        .url("http://localhost:8080"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));

        return new OpenAPI().info(applicationInfo);
    }
}
