package net.mycompany.commerce.purchase.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchase.domain.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    java.util.Optional<Currency> findByCode(String code);
}

