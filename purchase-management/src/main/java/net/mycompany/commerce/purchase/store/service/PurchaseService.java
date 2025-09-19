package net.mycompany.commerce.purchase.store.service;

import java.util.Optional;
import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {
    private final String environmentCurrencyCode;
    private final CurrencyRepository currencyRepository;
    private final PurchaseTransactionRepository purchaseTransactionRepository;

    public PurchaseService(
        PurchaseTransactionRepository purchaseTransactionRepository,
        CurrencyRepository currencyRepository,
        @Value("${environment.default.currency.code:USD}") String environmentCurrencyCode) {
        this.currencyRepository = currencyRepository;
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.environmentCurrencyCode = environmentCurrencyCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public StorePurchaseResponse newPurchase(StorePurchaseRequest request) {
        Optional<Currency> currencyOpt = currencyRepository.findByCode(environmentCurrencyCode);
        if (currencyOpt.isEmpty()) {
            throw new IllegalArgumentException("Currency not found: " + environmentCurrencyCode);
        }
        Currency currency = currencyOpt.get();
        PurchaseTransaction transaction = new PurchaseTransaction(request.getAmount(), currency, request.getPurchaseDate(), request.getDescription());
        purchaseTransactionRepository.save(transaction);
        StorePurchaseResponse response = new StorePurchaseResponse();
        response.setTransactionId(transaction.getTransactionId());
        return response;
    }
}