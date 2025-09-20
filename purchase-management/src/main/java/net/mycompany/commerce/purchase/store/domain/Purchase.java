package net.mycompany.commerce.purchase.store.domain;

import java.util.Optional;
import java.util.UUID;

import net.mycompany.commerce.purchase.errorhandler.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.store.consumer.PurchaseConsumer;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Purchase {
	
	private static final Logger log = LoggerFactory.getLogger(Purchase.class);
	
    private final String environmentCurrencyCode;
    private final CurrencyRepository currencyRepository;
    private final PurchaseTransactionRepository purchaseTransactionRepository;

    public Purchase(
        PurchaseTransactionRepository purchaseTransactionRepository,
        CurrencyRepository currencyRepository,
        @Value("${environment.default.currency.code:USD}") String environmentCurrencyCode) {
        this.currencyRepository = currencyRepository;
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.environmentCurrencyCode = environmentCurrencyCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public StorePurchaseResponse newPurchase(StorePurchaseRequest request, String transactionId) {
    	
    	log.debug("Processando nova compra: {}", request);
    	log.debug("Usando moeda padrão do sistema: {}", environmentCurrencyCode);
        Optional<Currency> currencyOpt = currencyRepository.findByCode(environmentCurrencyCode);
        
        if (currencyOpt.isEmpty()) {
        	log.error("Moeda não encontrada no sistema: {}", environmentCurrencyCode);
            throw new DataBaseNotFoundException();
        }
        
        Currency currency = currencyOpt.get();
        PurchaseTransaction transaction = new PurchaseTransaction(transactionId, request.getAmount(), currency, request.getPurchaseDate(), request.getDescription());
        
        log.debug("Salvando transação de compra");
        purchaseTransactionRepository.save(transaction);
        log.debug("Transação de compra salva com sucesso: {}", transaction);
        
        
        StorePurchaseResponse response = new StorePurchaseResponse();
        response.setTransactionId(transaction.getTransactionId());
        return response;
    }
}