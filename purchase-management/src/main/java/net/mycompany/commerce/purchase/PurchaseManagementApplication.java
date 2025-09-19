package net.mycompany.commerce.purchase;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class PurchaseManagementApplication {


    public static void main(String[] args) {
        SpringApplication.run(PurchaseManagementApplication.class, args);
    }
}