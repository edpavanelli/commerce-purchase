package net.mycompany.commerce.purchasemgmt.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchasemgmt.domain.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    java.util.Optional<Currency> findByCode(String code);
}

