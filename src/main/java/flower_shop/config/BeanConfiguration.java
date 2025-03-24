package flower_shop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flower_shop.product.model.Category;
import flower_shop.product.model.CategoryDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Category.class, new CategoryDeserializer())
                .create();
    }

}
