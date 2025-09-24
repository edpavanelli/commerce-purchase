package net.mycompany.commerce.purchasemgmt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@EnableCaching
@ComponentScan(basePackages = {"net.mycompany.commerce.mock", "net.mycompany.commerce.purchasemgmt"})
public class PurchaseManagementApplication {


    public static void main(String[] args) {
        SpringApplication.run(PurchaseManagementApplication.class, args);
    }
}