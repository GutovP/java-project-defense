package flower_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/api/**") // Allow CORS for specific endpoints
                        .allowedOrigins("http://localhost:4200") // Replace with https://yourdomain.com when in production
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
}
