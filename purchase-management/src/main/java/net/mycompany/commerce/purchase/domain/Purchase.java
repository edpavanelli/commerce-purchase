package net.mycompany.commerce.purchase.domain;

import java.util.Optional;

import net.mycompany.commerce.purchase.Utils;
import net.mycompany.commerce.purchase.audit.TransactionObserver;
import net.mycompany.commerce.purchase.audit.AuditOperation;
import net.mycompany.commerce.purchase.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.repository.PurchaseTransactionRepository;


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
    private final PurchaseTransactionSubject purchaseTransactionSubject;
    private final TransactionObserver transactionObserver;

    public Purchase(
        PurchaseTransactionRepository purchaseTransactionRepository,
        CurrencyRepository currencyRepository,
        @Value("${environment.default.currency.code}") String environmentCurrencyCode,
        PurchaseTransactionSubject purchaseTransactionSubject,
        TransactionObserver transactionObserver) {
        this.currencyRepository = currencyRepository;
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.environmentCurrencyCode = environmentCurrencyCode;
        this.purchaseTransactionSubject = purchaseTransactionSubject;
        this.transactionObserver = transactionObserver;
        this.purchaseTransactionSubject.addObserver(transactionObserver);
    }

    @Transactional(rollbackFor = Exception.class)
    public PurchaseTransaction storePurchase(PurchaseTransaction purchase) {
    	
    	log.debug("Processando nova compra: {}", purchase);
    	log.debug("Usando moeda padrão do sistema: {}", environmentCurrencyCode);
        Optional<Currency> currencyOpt = currencyRepository.findByCode(environmentCurrencyCode);
        
        if (currencyOpt.isEmpty()) {
        	log.error("Moeda não encontrada no sistema: {}", environmentCurrencyCode);
            throw new DataBaseNotFoundException();
        }
        
        purchase.setTransactionId(Utils.getNanoId());
        purchase.setCurrency(currencyOpt.get());
        
        
        log.debug("Salvando transação de compra");
        purchaseTransactionRepository.save(purchase);
        log.debug("Transação de compra salva com sucesso: {}", purchase.getTransactionId());

        // Notify observer for CREATE operation asynchronously
        purchaseTransactionSubject.notifyObserversOnPurchaseAsync(purchase, AuditOperation.CREATE);
        
       
        return purchase;
    }
}