package net.mycompany.commerce.purchase;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@EnableCaching
@ComponentScan(basePackages = {"net.mycompany.commerce.mock", "net.mycompany.commerce.purchase"})
public class PurchaseManagementApplication {


    public static void main(String[] args) {
        SpringApplication.run(PurchaseManagementApplication.class, args);
    }
}