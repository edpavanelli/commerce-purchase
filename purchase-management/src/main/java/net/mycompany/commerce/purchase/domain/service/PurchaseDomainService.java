package net.mycompany.commerce.purchase.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseDomainService {


    public static LocalDateTime getDateSixMonthsBack() {
        return LocalDate.now().minusMonths(6).atStartOfDay();
    }

   
}