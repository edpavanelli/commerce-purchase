package net.mycompany.commerce.purchase.application.store.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import net.mycompany.commerce.purchase.application.port.out.AuditEvent;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.application.store.publisher.PurchasePublisher;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.infrastructure.config.audit.AuditOperation;
import net.mycompany.commerce.purchase.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.infrastructure.config.audit.TransactionObserver;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.infrastructure.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;

@Service
public class StorePurchaseService {
	
	private static final Logger log = LoggerFactory.getLogger(StorePurchaseService.class);
	
    private final CurrencyRepository currencyRepository;
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    private final PurchaseTransactionSubject purchaseTransactionSubject;
    private final TransactionObserver transactionObserver;
    private final PurchaseTransactionMapper purchaseTransactionMapper;
    private final String dataBaseNotFoundMessage;
    private final String defaultCurrencyCode;
    private final PurchasePublisher purchasePublisher;

    public StorePurchaseService(
        PurchaseTransactionRepository purchaseTransactionRepository,
        CurrencyRepository currencyRepository,
        Environment environment,
        PurchaseTransactionSubject purchaseTransactionSubject,
        TransactionObserver transactionObserver,
        PurchaseTransactionMapper purchaseTransactionMapper,
        PurchasePublisher purchasePublisher,
        @Value("${error.database.notfound.message}") String dataBaseNotFoundMessage,
        @Value("${environment.default.currency.code}") String defaultCurrencyCode){
        this.currencyRepository = currencyRepository;
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.dataBaseNotFoundMessage=dataBaseNotFoundMessage; 
        this.defaultCurrencyCode=defaultCurrencyCode;
        this.purchaseTransactionSubject = purchaseTransactionSubject;
        this.transactionObserver = transactionObserver;
        this.purchaseTransactionSubject.addObserver(transactionObserver);
        this.purchaseTransactionMapper = purchaseTransactionMapper;
        this.purchasePublisher = purchasePublisher;
        
    }

    @Transactional(rollbackFor = Exception.class)
    public void storePurchase(StorePurchaseRequestDto request) {
    	
    	PurchaseTransaction purchaseTransaction = purchaseTransactionMapper.toDomain(request);
    	
    	log.debug("Processando nova compra: {}", request);
    	log.debug("Usando moeda padrão do sistema: {}", defaultCurrencyCode);
        Optional<Currency> currencyOpt = currencyRepository.findByCode(defaultCurrencyCode);
        
        if (currencyOpt.isEmpty()) {
        	log.error("Moeda não encontrada no sistema: {}", defaultCurrencyCode);
            throw new DataBaseNotFoundException(dataBaseNotFoundMessage);
        }
        
      
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
        	            purchaseTransactionSubject.notifyObserversOnPurchaseAsync(AuditEvent.builder()
			            		.transactionId(purchaseTransaction.getTransactionId())
			            		.operation(AuditOperation.CREATE)
			            		.changedBy("SystemUser")
			            		.changedDate(java.time.LocalDateTime.now())
			            		.build())
			            );
        	        }
        	    }
        	);
        
        purchasePublisher.publishResponse(response);
       
    }
}