package sii.ecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "sii.ecommerce")
@EntityScan(basePackages = {
        "sii.ecommerce.order.internal.model",
        "sii.ecommerce.inventory.internal.model"
})
@EnableJpaRepositories(basePackages = {
        "sii.ecommerce.order.internal.repository",
        "sii.ecommerce.inventory.internal.repository"
})
@EnableScheduling
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
