package net.mycompany.commerce.purchase.application.store.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.port.TransactionIdGeneratorPort;
import net.mycompany.commerce.purchase.infrastructure.config.audit.AuditOperation;
import net.mycompany.commerce.purchase.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.infrastructure.config.audit.TransactionObserver;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.infrastructure.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;

@Service
public class StorePurchaseService {
	
	private static final Logger log = LoggerFactory.getLogger(StorePurchaseService.class);
	
    private final String environmentCurrencyCode;
    private final CurrencyRepository currencyRepository;
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    private final PurchaseTransactionSubject purchaseTransactionSubject;
    private final TransactionObserver transactionObserver;
    private final PurchaseTransactionMapper purchaseTransactionMapper;
    private final TransactionIdGeneratorPort idGenerator;

    public StorePurchaseService(
        PurchaseTransactionRepository purchaseTransactionRepository,
        CurrencyRepository currencyRepository,
        @Value("${environment.default.currency.code}") String environmentCurrencyCode,
        PurchaseTransactionSubject purchaseTransactionSubject,
        TransactionObserver transactionObserver,
        PurchaseTransactionMapper purchaseTransactionMapper,
        TransactionIdGeneratorPort idGenerator) {
        this.currencyRepository = currencyRepository;
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.environmentCurrencyCode = environmentCurrencyCode;
        this.purchaseTransactionSubject = purchaseTransactionSubject;
        this.transactionObserver = transactionObserver;
        this.purchaseTransactionSubject.addObserver(transactionObserver);
        this.purchaseTransactionMapper = purchaseTransactionMapper;
        this.idGenerator = idGenerator;
    }

    @Transactional(rollbackFor = Exception.class)
    public StorePurchaseResponseDto storePurchase(StorePurchaseRequestDto request) {
    	
    	PurchaseTransaction purchaseTransaction = purchaseTransactionMapper.toDomain(request);
    	
    	log.debug("Processando nova compra: {}", request);
    	log.debug("Usando moeda padrão do sistema: {}", environmentCurrencyCode);
        Optional<Currency> currencyOpt = currencyRepository.findByCode(environmentCurrencyCode);
        
        if (currencyOpt.isEmpty()) {
        	log.error("Moeda não encontrada no sistema: {}", environmentCurrencyCode);
            throw new DataBaseNotFoundException();
        }
        
        purchaseTransaction.setTransactionId(idGenerator.nextId());
        purchaseTransaction.setCurrency(currencyOpt.get());
        
        
        log.debug("Salvando transação de compra");
        purchaseTransactionRepository.save(purchaseTransaction);
        log.debug("Transação de compra salva com sucesso: {}", purchaseTransaction.getTransactionId());

        StorePurchaseResponseDto response = purchaseTransactionMapper.toDto(purchaseTransaction);
        
        TransactionSynchronizationManager.registerSynchronization(
        	    new TransactionSynchronization() {
        	        @Override
        	        public void afterCommit() {
        	            CompletableFuture.runAsync(() ->
        	            purchaseTransactionSubject.notifyObserversOnPurchaseAsync(purchaseTransaction, AuditOperation.CREATE));
        	        }
        	    }
        	);
        
       
        return response;
    }
}