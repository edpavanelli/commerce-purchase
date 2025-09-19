package net.mycompany.commerce.purchase.repository;

import net.mycompany.commerce.purchase.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    java.util.Optional<Currency> findByCode(String code);
}

